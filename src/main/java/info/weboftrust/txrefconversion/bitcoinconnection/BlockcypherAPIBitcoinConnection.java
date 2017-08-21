package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;

public class BlockcypherAPIBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final Gson gson = new Gson();

	private static final BlockcypherAPIBitcoinConnection instance = new BlockcypherAPIBitcoinConnection();

	private BlockcypherAPIBitcoinConnection() {

	}

	public static BlockcypherAPIBitcoinConnection get() {

		return instance;
	}

	@Override
	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		URI uri;
		if (chain == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/blocks/" + blockHeight + "?txstart=" + blockIndex + "&limit=1");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/blocks/" + blockHeight + "?txstart=" + blockIndex + "&limit=1");
		}

		JsonObject txData = retrieveJson(uri);
		String txid = txData.get("txids").getAsJsonArray().get(0).getAsString();

		return txid;
	}

	@Override
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException {

		URI uri;
		if (chain == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/txs/" + txid + "?limit=500");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/txs/" + txid + "?limit=500");
		}

		JsonObject txData = retrieveJson(uri);
		long blockHeight = txData.get("block_height").getAsLong();
		long blockIndex = txData.get("block_index").getAsLong();

		return new ChainAndBlockLocation(chain, blockHeight, blockIndex);
	}

	private static JsonObject retrieveJson(URI uri) throws IOException {

		URLConnection con = uri.toURL().openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;

		return gson.fromJson(IOUtils.toString(in, encoding), JsonObject.class);
	}
}
