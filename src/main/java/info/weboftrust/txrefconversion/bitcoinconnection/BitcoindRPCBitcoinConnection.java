package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndLocationData;
import info.weboftrust.txrefconversion.ChainAndTxid;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction;

public class BitcoindRPCBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final BitcoindRPCBitcoinConnection instance = new BitcoindRPCBitcoinConnection();

	protected BitcoinJSONRPCClient bitcoindRpcClientMainnet;
	protected BitcoinJSONRPCClient bitcoindRpcClientTestnet;

	public BitcoindRPCBitcoinConnection(BitcoinJSONRPCClient bitcoindRpcClientMainnet, BitcoinJSONRPCClient bitcoindRpcClientTestnet) {

		this.bitcoindRpcClientMainnet = bitcoindRpcClientMainnet;
		this.bitcoindRpcClientTestnet = bitcoindRpcClientTestnet;
	}

	public BitcoindRPCBitcoinConnection(URL rpcUrlMainnet, URL rpcUrlTestnet) {

		this(new BitcoinJSONRPCClient(rpcUrlMainnet), new BitcoinJSONRPCClient(rpcUrlTestnet));
	}

	public BitcoindRPCBitcoinConnection(String rpcUrlMainnet, String rpcUrlTestnet) throws MalformedURLException {

		this(new URL(rpcUrlMainnet), new URL(rpcUrlTestnet));
	}

	public BitcoindRPCBitcoinConnection() {

		this(BitcoinJSONRPCClient.DEFAULT_JSONRPC_URL, BitcoinJSONRPCClient.DEFAULT_JSONRPC_TESTNET_URL);
	}

	public static BitcoindRPCBitcoinConnection get() {

		return instance;
	}

	@Override
	public ChainAndTxid lookupChainAndTxid(ChainAndLocationData chainAndLocationData) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chainAndLocationData.getChain() == Chain.MAINNET ? this.bitcoindRpcClientMainnet : this.bitcoindRpcClientTestnet;

		Block block = bitcoindRpcClient.getBlock(chainAndLocationData.getLocationData().getBlockHeight());
		if (block == null) return null;
		if (block.tx().size() <= chainAndLocationData.getLocationData().getTransactionPosition()) return null;

		String txid = block.tx().get(chainAndLocationData.getLocationData().getTransactionPosition());

		return new ChainAndTxid(chainAndLocationData.getChain(), txid, chainAndLocationData.getLocationData().getTxoIndex());
	}

	@Override
	public ChainAndLocationData lookupChainAndLocationData(ChainAndTxid chainAndTxid) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chainAndTxid.getChain() == Chain.MAINNET ? bitcoindRpcClientMainnet : bitcoindRpcClientTestnet;

		RawTransaction rawTransaction = bitcoindRpcClient.getRawTransaction(chainAndTxid.getTxid());
		if (rawTransaction == null) return null;

		Block block = bitcoindRpcClient.getBlock(rawTransaction.blockHash());
		if (block == null) return null;

		int blockHeight = block.height();
		int transactionPosition;
		for (transactionPosition=0; transactionPosition<block.size(); transactionPosition++) { if (block.tx().get(transactionPosition).equals(chainAndTxid.getTxid())) break; }
		if (transactionPosition == block.size()) return null;

		return new ChainAndLocationData(chainAndTxid.getChain(), blockHeight, transactionPosition, chainAndTxid.getTxoIndex());
	}

	/*
	 * Getters and setters
	 */

	public BitcoinJSONRPCClient getBitcoindRpcClientMainnet() {

		return this.bitcoindRpcClientMainnet;
	}

	public void setBitcoindRpcClientMainnet(BitcoinJSONRPCClient bitcoindRpcClientMainnet) {

		this.bitcoindRpcClientMainnet = bitcoindRpcClientMainnet;
	}

	public void setRpcUrlMainnet(URL rpcUrlMainnet) {

		this.setBitcoindRpcClientMainnet(new BitcoinJSONRPCClient(rpcUrlMainnet));
	}

	public void setRpcUrlMainnet(String rpcUrlMainnet) throws MalformedURLException {

		this.setRpcUrlMainnet(new URL(rpcUrlMainnet));
	}

	public BitcoinJSONRPCClient getBitcoindRpcClientTestnet() {

		return this.bitcoindRpcClientTestnet;
	}

	public void setBitcoindRpcClientTestnet(BitcoinJSONRPCClient bitcoindRpcClientTestnet) {

		this.bitcoindRpcClientTestnet = bitcoindRpcClientTestnet;
	}

	public void setRpcUrlTestnet(URL rpcUrlTestnet) {

		this.setBitcoindRpcClientTestnet(new BitcoinJSONRPCClient(rpcUrlTestnet));
	}

	public void setRpcUrlTestnet(String rpcUrlTestnet) throws MalformedURLException {

		this.setRpcUrlTestnet(new URL(rpcUrlTestnet));
	}
}
