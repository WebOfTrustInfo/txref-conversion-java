package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndBlockLocation;
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
	public ChainAndTxid lookupChainAndTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException {

		URI uri;
		if (chainAndBlockLocation.getChain() == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/blocks/" + chainAndBlockLocation.getBlockHeight() + "?txstart=" + chainAndBlockLocation.getBlockIndex() + "&limit=1");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/blocks/" + chainAndBlockLocation.getBlockHeight() + "?txstart=" + chainAndBlockLocation.getBlockIndex() + "&limit=1");
		}

		JsonObject txData = retrieveJson(uri);
		String txid = txData.get("txids").getAsJsonArray().get(0).getAsString();

		return new ChainAndTxid(chainAndBlockLocation.getChain(), txid, chainAndBlockLocation.getUtxoIndex());
	}

	@Override
	public ChainAndBlockLocation lookupChainAndBlockLocation(ChainAndTxid chainAndTxid) throws IOException {

		URI uri;
		if (chainAndTxid.getChain() == Chain.MAINNET) {
			uri = URI.create("https://api.blockcypher.com/v1/btc/main/txs/" + chainAndTxid.getTxid() + "?limit=500");
		} else {
			uri = URI.create("https://api.blockcypher.com/v1/btc/test3/txs/" + chainAndTxid.getTxid() + "?limit=500");
		}

		JsonObject txData = retrieveJson(uri);
		long blockHeight = txData.get("block_height").getAsLong();
		long blockIndex = txData.get("block_index").getAsLong();

		if (blockHeight == -1 || blockIndex == -1) return null;
		return new ChainAndBlockLocation(chainAndTxid.getChain(), blockHeight, blockIndex, chainAndTxid.getUtxoIndex());
	}

	protected static JsonObject retrieveJson(URI uri) throws IOException {

		URLConnection con = uri.toURL().openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();
		encoding = encoding == null ? "UTF-8" : encoding;

		return gson.fromJson(IOUtils.toString(in, encoding), JsonObject.class);
	}
}
