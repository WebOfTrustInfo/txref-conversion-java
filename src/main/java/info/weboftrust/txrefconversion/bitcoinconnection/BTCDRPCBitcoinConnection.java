package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndLocationData;
import info.weboftrust.txrefconversion.ChainAndTxid;

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
	public ChainAndTxid lookupChainAndTxid(ChainAndLocationData chainAndLocationData) throws IOException {

		JsonRpcHttpClient btcdRpcClient = chainAndLocationData.getChain() == Chain.MAINNET ? this.btcdRpcClientMainnet : this.btcdRpcClientTestnet;

		String getblockhash_result;

		try {

			getblockhash_result = btcdRpcClient.invoke("getblockhash", new Object[] { chainAndLocationData.getLocationData().getBlockHeight() }, String.class);
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

		LinkedHashMap<String, Object> rawTx = (LinkedHashMap<String, Object>) rawTxs.get(chainAndLocationData.getLocationData().getTransactionPosition());
		if (rawTx == null) return null;

		String txid = (String) rawTx.get("txid");
		if (txid == null) return null;

		return new ChainAndTxid(chainAndLocationData.getChain(), txid, chainAndLocationData.getLocationData().getTxoIndex());
	}

	@SuppressWarnings("unchecked")
	@Override
	public ChainAndLocationData lookupChainAndLocationData(ChainAndTxid chainAndTxid) throws IOException {

		JsonRpcHttpClient btcdRpcClient = chainAndTxid.getChain() == Chain.MAINNET ? this.btcdRpcClientMainnet : this.btcdRpcClientTestnet;

		String blockHash;

		try {

			LinkedHashMap<String, Object> getrawtransaction_result = btcdRpcClient.invoke("getrawtransaction", new Object[] { chainAndTxid.getTxid(), 1 }, LinkedHashMap.class);
			blockHash = (String) getrawtransaction_result.get("blockhash");
		} catch (JsonRpcClientException ex) {

			throw ex;
		} catch (IOException ex) {

			throw ex;
		} catch (Throwable ex) {

			throw new IOException("getrawtransaction() exception: " + ex.getMessage(), ex);
		}

		if (blockHash == null) return null;

		Integer blockHeight;
		ArrayList<String> txs;

		try {

			LinkedHashMap<String, Object> getblock_result = btcdRpcClient.invoke("getblock", new Object[] { blockHash, true, false }, LinkedHashMap.class);
			blockHeight = (Integer) getblock_result.get("height");
			txs = (ArrayList<String>) getblock_result.get("tx");
		} catch (JsonRpcClientException ex) {

			if (ex.getCode() == -5) {

				blockHeight = null;
				txs = null;
			} else {

				throw ex;
			}
		} catch (IOException ex) {

			throw ex;
		} catch (Throwable ex) {

			throw new IOException("getblock() exception: " + ex.getMessage(), ex);
		}

		if (blockHeight == null || txs == null) return null;

		int transactionPosition;
		for (transactionPosition=0; transactionPosition<txs.size(); transactionPosition++) { if (txs.get(transactionPosition).equals(chainAndTxid.getTxid())) break; }
		if (transactionPosition == txs.size()) return null;

		return new ChainAndLocationData(chainAndTxid.getChain(), blockHeight, transactionPosition, chainAndTxid.getTxoIndex());
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
