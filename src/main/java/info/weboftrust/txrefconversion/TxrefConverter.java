package info.weboftrust.txrefconversion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import info.weboftrust.txrefconversion.Bech32.HrpAndData;

public class TxrefConverter {

	public static final byte MAGIC_BTC_MAINNET = 0x03;
	public static final byte MAGIC_BTC_TESTNET = 0x06;

	public static final byte[] TXREF_BECH32_HRP_MAINNET = "tx".getBytes();
	public static final byte[] TXREF_BECH32_HRP_TESTNET = "txtest".getBytes();

	private static final Gson gson = new Gson();

	public static String txrefEncode(Chain chain, long blockHeight, long txPos) {

		byte magic = chain == Chain.MAINNET ? MAGIC_BTC_MAINNET : MAGIC_BTC_TESTNET;
		byte[] prefix = chain == Chain.MAINNET ? TXREF_BECH32_HRP_MAINNET : TXREF_BECH32_HRP_TESTNET;
		boolean nonStandard = chain != Chain.MAINNET;

		byte[] shortId;
		shortId = nonStandard ? new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 } : new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		shortId[0] = magic;

		if ((nonStandard && (blockHeight > 0x1FFFFF || txPos > 0x1FFF || magic > 0x1F)) ||
				(nonStandard && (blockHeight > 0x3FFFFFF || txPos > 0x3FFFF || magic > 0x1F))) {

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

			shortId[6] |= ((txPos & 0x7) << 2);
			shortId[7] |= ((txPos & 0xF8) >> 3);
			shortId[8] |= ((txPos & 0x1F00) >> 8);
			shortId[9] |= ((txPos & 0x3E000) >> 13);
		} else {
			shortId[5] |= ((blockHeight & 0x180000) >> 19);
			shortId[5] |= ((txPos & 0x7) << 2);
			shortId[6] |= ((txPos & 0xF8) >> 3);
			shortId[7] |= ((txPos & 0x1F00) >> 8);
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

	public static BlockLocation txrefDecode(String bech32Tx) {

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

		return new BlockLocation(blockHeight, blockIndex, chain);
	}

	private static JsonObject parseTxDetails(JsonObject txData, Chain chain, String txid) {

		long blockHeight = txData.get("block_height").getAsLong();
		long blockIndex = txData.get("block_index").getAsLong();

		JsonObject jsonObject = new JsonObject();
		jsonObject.add("blockHeight", new JsonPrimitive(blockHeight));
		jsonObject.add("blockIndex", new JsonPrimitive(blockIndex));

		return jsonObject;
	}

	private static JsonObject getTxDetails(String txid, Chain chain) throws IOException {

		URI uri;
		if (chain == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/txs/" + txid + "?limit=500");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/txs/" + txid + "?limit=500");
		}

		JsonObject txData = retrieveJson(uri);

		return parseTxDetails(txData, chain, txid);
	}

	public static String txidToTxref(String txid, Chain chain) throws IOException {

		JsonObject txDetails = getTxDetails(txid, chain);

		String txref = txrefEncode(chain, txDetails.get("blockHeight").getAsLong(), txDetails.get("blockIndex").getAsLong());
		return txref;
	}

	public static TxidAndChain txrefToTxid(String txref) throws IOException {

		BlockLocation blockLocation = txrefDecode(txref);
		if (blockLocation == null) throw new IllegalArgumentException("Could not decode txref " + txref);

		long blockHeight = blockLocation.blockHeight;
		long blockIndex = blockLocation.blockIndex;
		Chain chain = blockLocation.chain;

		URI uri;
		if (chain == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/blocks/" + blockHeight + "?txstart=" + blockIndex + "&limit=1");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/blocks/" + blockHeight + "?txstart=" + blockIndex + "&limit=1");
		}

		JsonObject txData = retrieveJson(uri);

		return new TxidAndChain(txData.get("txids").getAsJsonArray().get(0).getAsString(), chain);
	}

	private static JsonObject retrieveJson(URI uri) throws IOException {

		URLConnection con = uri.toURL().openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;

		return gson.fromJson(IOUtils.toString(in, encoding), JsonObject.class);
	}

	public static enum Chain {

		MAINNET,
		TESTNET
	}

	public static class BlockLocation {

		public long blockHeight;
		public long blockIndex;
		public Chain chain;

		public BlockLocation(long blockHeight, long blockIndex, Chain chain) { this.blockHeight = blockHeight; this.blockIndex = blockIndex; this.chain = chain; }
		public long getBlockHeight() { return this.blockHeight; }
		public long getBlockIndex() { return this.blockIndex; }
		public Chain getChain() { return this.chain; }
	}

	public static class TxidAndChain {

		public String txid;
		public Chain chain;

		public TxidAndChain(String txid, Chain chain) { this.txid = txid; this.chain = chain; }
		public String getTxid() { return this.txid; }
		public Chain getChain() { return this.chain; }
	}
}
