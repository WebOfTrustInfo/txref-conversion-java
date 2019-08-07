package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndLocationData;
import info.weboftrust.txrefconversion.ChainAndTxid;

public class BlockcypherAPIBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final BlockcypherAPIBitcoinConnection instance = new BlockcypherAPIBitcoinConnection();

	protected static final Gson gson = new Gson();

	public BlockcypherAPIBitcoinConnection() {

	}

	public static BlockcypherAPIBitcoinConnection get() {

		return instance;
	}

	@Override
	public ChainAndTxid lookupChainAndTxid(ChainAndLocationData chainAndLocationData) throws IOException {

		URI uri;
		if (chainAndLocationData.getChain() == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/blocks/" + chainAndLocationData.getLocationData().getBlockHeight() + "?txstart=" + chainAndLocationData.getLocationData().getTransactionPosition() + "&limit=1");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/blocks/" + chainAndLocationData.getLocationData().getBlockHeight() + "?txstart=" + chainAndLocationData.getLocationData().getTransactionPosition() + "&limit=1");
		}

		JsonObject txData = retrieveJson(uri);
		String txid = txData.get("txids").getAsJsonArray().get(0).getAsString();

		return new ChainAndTxid(chainAndLocationData.getChain(), txid, chainAndLocationData.getLocationData().getTxoIndex());
	}

	@Override
	public ChainAndLocationData lookupChainAndLocationData(ChainAndTxid chainAndTxid) throws IOException {

		URI uri;
		if (chainAndTxid.getChain() == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/txs/" + chainAndTxid.getTxid() + "?limit=500");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/txs/" + chainAndTxid.getTxid() + "?limit=500");
		}

		JsonObject txData = retrieveJson(uri);
		int blockHeight = txData.get("block_height").getAsInt();
		int transactionPosition = txData.get("block_index").getAsInt();

		if (blockHeight == -1 || transactionPosition == -1) return null;
		return new ChainAndLocationData(chainAndTxid.getChain(), blockHeight, transactionPosition, chainAndTxid.getTxoIndex());
	}

	protected static JsonObject retrieveJson(URI uri) throws IOException {

		URLConnection con = uri.toURL().openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;

		return gson.fromJson(IOUtils.toString(in, encoding), JsonObject.class);
	}
}
