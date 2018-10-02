package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndBlockLocation;

public class BTCDRPCBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final BTCDRPCBitcoinConnection instance = new BTCDRPCBitcoinConnection();

	public static final URL DEFAULT_JSONRPC_URL;
	public static final URL DEFAULT_JSONRPC_TESTNET_URL;
	public static final String DEFAULT_RPC_USER = "user";
	public static final String DEFAULT_RPC_PASS = "pass";

	protected JsonRpcHttpClient btcdRpcClientMainnet;
	protected JsonRpcHttpClient btcdRpcClientTestnet;

	static {

		try {

			DEFAULT_JSONRPC_URL = new URL("http://localhost:8334");
			DEFAULT_JSONRPC_TESTNET_URL = new URL("http://localhost:18334");
		} catch (MalformedURLException ex) {

			throw new ExceptionInInitializerError(ex);
		}
	}

	public BTCDRPCBitcoinConnection(JsonRpcHttpClient btcdRpcClientMainnet, JsonRpcHttpClient btcdRpcClientTestnet) {

		this.btcdRpcClientMainnet = btcdRpcClientMainnet;
		this.btcdRpcClientTestnet = btcdRpcClientTestnet;
	}

	public BTCDRPCBitcoinConnection(URL rpcUrlMainnet, URL rpcUrlTestnet) {

		this(btcdRpcClient(rpcUrlMainnet), btcdRpcClient(rpcUrlTestnet));
	}

	public BTCDRPCBitcoinConnection(String rpcUrlMainnet, String rpcUrlTestnet) throws MalformedURLException {

		this(new URL(rpcUrlMainnet), new URL(rpcUrlTestnet));
	}

	public BTCDRPCBitcoinConnection() {

		this(DEFAULT_JSONRPC_URL, DEFAULT_JSONRPC_TESTNET_URL);
	}

	public static BTCDRPCBitcoinConnection get() {

		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		JsonRpcHttpClient btcdRpcClient = chain == Chain.MAINNET ? this.btcdRpcClientMainnet : this.btcdRpcClientTestnet;

		String getblockhash_result;

		try {

			getblockhash_result = btcdRpcClient.invoke("getblockhash", new Object[] { blockHeight }, String.class);
		} catch (IOException ex) {

			throw ex;
		} catch (Throwable ex) {

			throw new IOException("getblockhash() exception: " + ex.getMessage(), ex);
		}

		String blockHash = getblockhash_result;

		LinkedHashMap<String, Object> getblock_result;

		try {

			getblock_result = btcdRpcClient.invoke("getblock", new Object[] { blockHash, true, true }, LinkedHashMap.class);
		} catch (IOException ex) {

			throw ex;
		} catch (Throwable ex) {

			throw new IOException("getblock() exception: " + ex.getMessage(), ex);
		}

		ArrayList<Object> rawTxs = (ArrayList<Object>) getblock_result.get("rawtx");
		if (rawTxs == null) return null;

		LinkedHashMap<String, Object> rawTx = (LinkedHashMap<String, Object>) rawTxs.get((int) blockIndex);
		if (rawTx == null) return null;

		String txid = (String) rawTx.get("txid");
		if (txid == null) return null;

		return txid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException {

		JsonRpcHttpClient btcdRpcClient = chain == Chain.MAINNET ? this.btcdRpcClientMainnet : this.btcdRpcClientTestnet;

		LinkedHashMap<String, Object> getrawtransaction_result;

		System.out.println(chain + "  " + txid);
		try {

			getrawtransaction_result = btcdRpcClient.invoke("getrawtransaction", new Object[] { txid, 1 }, LinkedHashMap.class);
		} catch (IOException ex) {

			throw ex;
		} catch (Throwable ex) {

			throw new IOException("getrawtransaction() exception: " + ex.getMessage(), ex);
		}

		String blockHash = (String) getrawtransaction_result.get("blockhash");

		LinkedHashMap<String, Object> getblock_result;

		try {

			getblock_result = btcdRpcClient.invoke("getblock", new Object[] { blockHash, true, false }, LinkedHashMap.class);
		} catch (IOException ex) {

			throw ex;
		} catch (Throwable ex) {

			throw new IOException("getblock() exception: " + ex.getMessage(), ex);
		}

		Integer blockHeight = (Integer) getblock_result.get("height");

		ArrayList<String> txs = (ArrayList<String>) getblock_result.get("tx");
		System.out.println(getblock_result);

		long blockIndex;
		for (blockIndex=0; blockIndex<txs.size(); blockIndex++) { if (txs.get((int) blockIndex).equals(txid)) break; }
		if (blockIndex == txs.size()) return null;

		return new ChainAndBlockLocation(chain, blockHeight, blockIndex);
	}

	/*
	 * Helper methods
	 */

	private static JsonRpcHttpClient btcdRpcClient(URL rpcUrl, String rpcUser, String rpcPass) {

		JsonRpcHttpClient btcdRpcClient = new JsonRpcHttpClient(rpcUrl);
		Map<String, String> headers = new HashMap<String, String> (btcdRpcClient.getHeaders());
		headers.put("Authorization", "Basic " + Base64.getEncoder().encodeToString((rpcUser + ":" + rpcPass).getBytes()));
		btcdRpcClient.setHeaders(headers);

		return btcdRpcClient;
	}

	private static JsonRpcHttpClient btcdRpcClient(URL rpcUrl) {

		return btcdRpcClient(rpcUrl, DEFAULT_RPC_USER, DEFAULT_RPC_PASS);
	}

	/*
	 * Getters and setters
	 */

	public JsonRpcHttpClient getBtcdRpcClientMainnet() {

		return this.btcdRpcClientMainnet;
	}

	public void setBtcdRpcClientMainnet(JsonRpcHttpClient btcdRpcClientMainnet) {

		this.btcdRpcClientMainnet = btcdRpcClientMainnet;
	}

	public void setRpcUrlMainnet(URL rpcUrlMainnet) {

		this.setBtcdRpcClientMainnet(btcdRpcClient(rpcUrlMainnet));
	}

	public void setRpcUrlMainnet(String rpcUrlMainnet) throws MalformedURLException {

		this.setRpcUrlMainnet(new URL(rpcUrlMainnet));
	}

	public JsonRpcHttpClient getBtcdRpcClientTestnet() {

		return this.btcdRpcClientTestnet;
	}

	public void setBtcdRpcClientTestnet(JsonRpcHttpClient btcdRpcClientTestnet) {

		this.btcdRpcClientTestnet = btcdRpcClientTestnet;
	}

	public void setRpcUrlTestnet(URL rpcUrlTestnet) {

		this.setBtcdRpcClientTestnet(btcdRpcClient(rpcUrlTestnet));
	}

	public void setRpcUrlTestnet(String rpcUrlTestnet) throws MalformedURLException {

		this.setRpcUrlTestnet(new URL(rpcUrlTestnet));
	}
}
