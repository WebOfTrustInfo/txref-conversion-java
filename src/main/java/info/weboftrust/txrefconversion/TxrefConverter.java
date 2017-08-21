package info.weboftrust.txrefconversion;

import java.io.IOException;

import info.weboftrust.txrefconversion.Bech32.HrpAndData;
import info.weboftrust.txrefconversion.bitcoinconnection.BitcoinConnection;
import info.weboftrust.txrefconversion.bitcoinconnection.BlockcypherAPIBitcoinConnection;

public class TxrefConverter {

	public static final byte MAGIC_BTC_MAINNET = 0x03;
	public static final byte MAGIC_BTC_TESTNET = 0x06;

	public static final byte[] TXREF_BECH32_HRP_MAINNET = "tx".getBytes();
	public static final byte[] TXREF_BECH32_HRP_TESTNET = "txtest".getBytes();

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

	public String txrefEncode(Chain chain, long blockHeight, long blockIndex) {

		byte magic = chain == Chain.MAINNET ? MAGIC_BTC_MAINNET : MAGIC_BTC_TESTNET;
		byte[] prefix = chain == Chain.MAINNET ? TXREF_BECH32_HRP_MAINNET : TXREF_BECH32_HRP_TESTNET;
		boolean nonStandard = chain != Chain.MAINNET;

		byte[] shortId;
		shortId = nonStandard ? new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } : new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		shortId[0] = magic;

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
	};

	public String txrefEncode(ChainAndBlockLocation chainAndBlockLocation) {

		return txrefEncode(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.getBlockIndex());
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

	public String txidToTxref(String txid, Chain chain) throws IOException {

		ChainAndBlockLocation blockLocation = this.bitcoinConnection.getChainAndBlockLocation(chain, txid);

		String txref = txrefEncode(blockLocation);
		return txref;
	}

	public ChainAndTxid txrefToTxid(String txref) throws IOException {

		ChainAndBlockLocation chainAndBlockLocation = txrefDecode(txref);
		if (chainAndBlockLocation == null) throw new IllegalArgumentException("Could not decode txref " + txref);

		String txid = this.bitcoinConnection.getTxid(chainAndBlockLocation);
		return new ChainAndTxid(chainAndBlockLocation.getChain(), txid);
	}

	public static enum Chain {

		MAINNET,
		TESTNET
	}

	public static class ChainAndBlockLocation {

		public Chain chain;
		public long blockHeight;
		public long blockIndex;

		public ChainAndBlockLocation(Chain chain, long blockHeight, long blockIndex) { this.chain = chain; this.blockHeight = blockHeight; this.blockIndex = blockIndex; }
		public Chain getChain() { return this.chain; }
		public long getBlockHeight() { return this.blockHeight; }
		public long getBlockIndex() { return this.blockIndex; }
	}

	public static class ChainAndTxid {

		public Chain chain;
		public String txid;

		public ChainAndTxid(Chain chain, String txid) { this.chain = chain; this.txid = txid; }
		public Chain getChain() { return this.chain; }
		public String getTxid() { return this.txid; }
	}
}
