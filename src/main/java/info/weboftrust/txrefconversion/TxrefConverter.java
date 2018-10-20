package info.weboftrust.txrefconversion;

import java.io.IOException;

import info.weboftrust.txrefconversion.Bech32.HrpAndData;
import info.weboftrust.txrefconversion.bitcoinconnection.BitcoinConnection;
import info.weboftrust.txrefconversion.bitcoinconnection.BlockcypherAPIBitcoinConnection;

public class TxrefConverter {

	public static final byte MAGIC_BTC_MAINNET = 0x03;
	public static final byte MAGIC_BTC_TESTNET = 0x06;

	public static final char MAGIC_BTC_MAINNET_BECH32_CHAR = Bech32.CHARSET.charAt(MAGIC_BTC_MAINNET);	// 'r'
	public static final char MAGIC_BTC_TESTNET_BECH32_CHAR = Bech32.CHARSET.charAt(MAGIC_BTC_TESTNET);	// 'x'

	public static final String TXREF_BECH32_HRP_MAINNET = "tx";
	public static final String TXREF_BECH32_HRP_TESTNET = "txtest";

	public static final byte[] TXREF_BECH32_HRP_MAINNET_BYTES = TXREF_BECH32_HRP_MAINNET.getBytes();
	public static final byte[] TXREF_BECH32_HRP_TESTNET_BYTES = TXREF_BECH32_HRP_TESTNET.getBytes();

	private final BitcoinConnection bitcoinConnection;

	private static final TxrefConverter instance = new TxrefConverter();

	public TxrefConverter(BitcoinConnection bitcoinConnection) {

		this.bitcoinConnection = bitcoinConnection;
	}

	public TxrefConverter() {

		this(BlockcypherAPIBitcoinConnection.get());
	}

	public static TxrefConverter get() {

		return instance;
	}

	/*
	 * txref encode / decode
	 */

	public String txrefEncode(Chain chain, long blockHeight, long blockIndex) {

		byte magic = chain == Chain.MAINNET ? MAGIC_BTC_MAINNET : MAGIC_BTC_TESTNET;
		byte[] prefix = chain == Chain.MAINNET ? TXREF_BECH32_HRP_MAINNET_BYTES : TXREF_BECH32_HRP_TESTNET_BYTES;
		boolean nonStandard = chain != Chain.MAINNET;

		byte[] shortId;
		shortId = nonStandard ? new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } : new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		if ((nonStandard && (blockHeight > 0x1FFFFF || blockIndex > 0x1FFF || magic > 0x1F)) ||
				(nonStandard && (blockHeight > 0x3FFFFFF || blockIndex > 0x3FFFF || magic > 0x1F))) {

			return null;
		}

		/* set the magic */
		shortId[0] = magic;

		/* make sure the version bit is 0 */
		shortId[1] &= ~(1 << 0);

		shortId[1] |= ((blockHeight & 0xF) << 1);
		shortId[2] |= ((blockHeight & 0x1F0) >> 4);
		shortId[3] |= ((blockHeight & 0x3E00) >> 9);
		shortId[4] |= ((blockHeight & 0x7C000) >> 14);

		if (nonStandard) {
			// use extended blockheight (up to 0x3FFFFFF)
			// use extended txpos (up to 0x3FFFF)
			shortId[5] |= ((blockHeight & 0xF80000) >> 19);
			shortId[6] |= ((blockHeight & 0x3000000) >> 24);

			shortId[6] |= ((blockIndex & 0x7) << 2);
			shortId[7] |= ((blockIndex & 0xF8) >> 3);
			shortId[8] |= ((blockIndex & 0x1F00) >> 8);
			shortId[9] |= ((blockIndex & 0x3E000) >> 13);
		} else {
			shortId[5] |= ((blockHeight & 0x180000) >> 19);
			shortId[5] |= ((blockIndex & 0x7) << 2);
			shortId[6] |= ((blockIndex & 0xF8) >> 3);
			shortId[7] |= ((blockIndex & 0x1F00) >> 8);
		}

		String result = Bech32.bech32Encode(prefix, shortId);

		int breakIndex = prefix.length + 1;
		String finalResult = result.substring(0, breakIndex) + "-" +
				result.substring(breakIndex, breakIndex + 4) + "-" +
				result.substring(breakIndex + 4, breakIndex + 8) + "-" +
				result.substring(breakIndex + 8, breakIndex + 12) + "-" +
				result.substring(breakIndex + 12, result.length());

		return finalResult;
	}

	public String txrefEncode(ChainAndBlockLocation chainAndBlockLocation) {

		return this.txrefEncode(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.getBlockIndex());
	}

	public ChainAndBlockLocation txrefDecode(String bech32Tx) {

		String stripped = bech32Tx.replace("-", "");

		HrpAndData result = Bech32.bech32Decode(stripped);
		if (result == null) {
			return null;
		}
		byte[] buf = result.getData();

		byte chainMarker = buf[0];
		boolean nonStandard = chainMarker != MAGIC_BTC_MAINNET;

		long bStart = (buf[1] >> 1) |
				(buf[2] << 4) |
				(buf[3] << 9) |
				(buf[4] << 14);

		long blockHeight = 0;
		long blockIndex = 0;

		if (nonStandard) {
			blockHeight = bStart | (buf[5] << 19);
			blockHeight |= ((buf[6] & 0x03) << 24);

			blockIndex = (buf[6] & 0x1C) >> 2;
			blockIndex |= (buf[7] << 3);
			blockIndex |= (buf[8] << 8);
			blockIndex |= (buf[9] << 13);
		} else {
			blockHeight = bStart | ((buf[5] & 0x03) << 19);
			blockIndex = (buf[5] & 0x1C) >> 2;
			blockIndex |= (buf[6] << 3);
			blockIndex |= (buf[7] << 8);
		}

		Chain chain = chainMarker == MAGIC_BTC_MAINNET ? Chain.MAINNET : Chain.TESTNET;

		return new ChainAndBlockLocation(chain, blockHeight, blockIndex);
	}

	/*
	 * txrefext encode / decode
	 */

	public String txrefextEncode(Chain chain, long blockHeight, long blockIndex, long utxoIndex) {

		byte magic = chain == Chain.MAINNET ? MAGIC_BTC_MAINNET : MAGIC_BTC_TESTNET;
		byte[] prefix = chain == Chain.MAINNET ? TXREF_BECH32_HRP_MAINNET_BYTES : TXREF_BECH32_HRP_TESTNET_BYTES;
		boolean nonStandard = chain != Chain.MAINNET;

		byte[] shortId;
		shortId = nonStandard ? new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } : new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		if ((nonStandard && (blockHeight > 0x1FFFFF || blockIndex > 0x1FFF || utxoIndex > 0x1FFF || magic > 0x1F)) ||
				(nonStandard && (blockHeight > 0x3FFFFFF || blockIndex > 0x3FFFF || utxoIndex > 0x1FFF || magic > 0x1F))) {

			return null;
		}

		/* set the magic */
		shortId[0] = magic;

		/* make sure the version bit is 0 */
		shortId[1] &= ~(1 << 0);

		shortId[1] |= ((blockHeight & 0xF) << 1);
		shortId[2] |= ((blockHeight & 0x1F0) >> 4);
		shortId[3] |= ((blockHeight & 0x3E00) >> 9);
		shortId[4] |= ((blockHeight & 0x7C000) >> 14);

		if (nonStandard) {
			// use extended blockheight (up to 0x3FFFFFF)
			// use extended txpos (up to 0x3FFFF)
			shortId[5] |= ((blockHeight & 0xF80000) >> 19);
			shortId[6] |= ((blockHeight & 0x3000000) >> 24);

			shortId[6] |= ((blockIndex & 0x7) << 2);
			shortId[7] |= ((blockIndex & 0xF8) >> 3);
			shortId[8] |= ((blockIndex & 0x1F00) >> 8);
			shortId[9] |= ((blockIndex & 0x3E000) >> 13);
			shortId[10] |= ((utxoIndex & 0x1F));
			shortId[11] |= ((utxoIndex & 0x3E0) >> 5);
			shortId[12] |= ((utxoIndex & 0x1C00) >> 10);
		} else {
			shortId[5] |= ((blockHeight & 0x180000) >> 19);
			shortId[5] |= ((blockIndex & 0x7) << 2);
			shortId[6] |= ((blockIndex & 0xF8) >> 3);
			shortId[7] |= ((blockIndex & 0x1F00) >> 8);
			shortId[8] |= ((utxoIndex & 0x1F));
			shortId[9] |= ((utxoIndex & 0x3E0) >> 5);
			shortId[10] |= ((utxoIndex & 0x1C00) >> 10);
		}

		String result = Bech32.bech32Encode(prefix, shortId);

		int breakIndex = prefix.length + 1;
		String finalResult = result.substring(0, breakIndex) + "-" +
				result.substring(breakIndex, breakIndex + 4) + "-" +
				result.substring(breakIndex + 4, breakIndex + 8) + "-" +
				result.substring(breakIndex + 8, breakIndex + 12) + "-" +
				result.substring(breakIndex + 12, result.length());

		return finalResult;
	}

	public String txrefextEncode(ChainAndBlockLocationAndUtxoIndex chainAndBlockLocationAndUtxoIndex) {

		return this.txrefextEncode(chainAndBlockLocationAndUtxoIndex.getChain(), chainAndBlockLocationAndUtxoIndex.getBlockHeight(), chainAndBlockLocationAndUtxoIndex.getBlockIndex(), chainAndBlockLocationAndUtxoIndex.getUtxoIndex());
	}

	public ChainAndBlockLocationAndUtxoIndex txrefextDecode(String bech32Tx) {

		String stripped = bech32Tx.replace("-", "");

		HrpAndData result = Bech32.bech32Decode(stripped);
		if (result == null) {
			return null;
		}
		byte[] buf = result.getData();

		byte chainMarker = buf[0];
		boolean nonStandard = chainMarker != MAGIC_BTC_MAINNET;

		long bStart = (buf[1] >> 1) |
				(buf[2] << 4) |
				(buf[3] << 9) |
				(buf[4] << 14);

		long blockHeight = 0;
		long blockIndex = 0;
		long utxoIndex = 0;

		if (nonStandard) {
			blockHeight = bStart | (buf[5] << 19);
			blockHeight |= ((buf[6] & 0x03) << 24);

			blockIndex = (buf[6] & 0x1C) >> 2;
			blockIndex |= (buf[7] << 3);
			blockIndex |= (buf[8] << 8);
			blockIndex |= (buf[9] << 13);
			utxoIndex = buf[10];
			utxoIndex |= (buf[11] << 5);
			utxoIndex |= (buf[12] << 10);
		} else {
			blockHeight = bStart | ((buf[5] & 0x03) << 19);
			blockIndex = (buf[5] & 0x1C) >> 2;
			blockIndex |= (buf[6] << 3);
			blockIndex |= (buf[7] << 8);
			utxoIndex = buf[8];
			utxoIndex |= (buf[9] << 5);
			utxoIndex |= (buf[10] << 10);
		}

		Chain chain = chainMarker == MAGIC_BTC_MAINNET ? Chain.MAINNET : Chain.TESTNET;

		return new ChainAndBlockLocationAndUtxoIndex(chain, blockHeight, blockIndex, utxoIndex);
	}

	/*
	 * txid <-> txref
	 */

	public String txidToTxref(String txid, Chain chain) throws IOException {

		ChainAndBlockLocation blockLocation = this.bitcoinConnection.getChainAndBlockLocation(chain, txid);
		if (blockLocation == null) return null;

		String txref = txrefEncode(blockLocation);
		return txref;
	}

	public String txidToTxref(ChainAndTxid chainAndTxid) throws IOException {

		return this.txidToTxref(chainAndTxid.getTxid(), chainAndTxid.getChain());
	}

	public ChainAndTxid txrefToTxid(String txref) throws IOException {

		ChainAndBlockLocation chainAndBlockLocation = txrefDecode(txref);
		if (chainAndBlockLocation == null) throw new IllegalArgumentException("Could not decode txref " + txref);

		return txrefToTxid(chainAndBlockLocation);
	}

	public ChainAndTxid txrefToTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException {

		String txid = this.bitcoinConnection.getTxid(chainAndBlockLocation);
		return new ChainAndTxid(chainAndBlockLocation.getChain(), txid);
	}
}
