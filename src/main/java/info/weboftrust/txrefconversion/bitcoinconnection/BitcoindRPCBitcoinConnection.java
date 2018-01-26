package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;
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
	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chain == Chain.MAINNET ? this.bitcoindRpcClientMainnet : this.bitcoindRpcClientTestnet;

		Block block = bitcoindRpcClient.getBlock((int) blockHeight);
		if (block == null) return null;
		if (block.tx().size() <= blockIndex) return null;

		String txid = block.tx().get((int) blockIndex);

		return txid;
	}

	@Override
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chain == Chain.MAINNET ? bitcoindRpcClientMainnet : bitcoindRpcClientTestnet;

		RawTransaction rawTransaction = bitcoindRpcClient.getRawTransaction(txid);
		if (rawTransaction == null) return null;

		Block block = bitcoindRpcClient.getBlock(rawTransaction.blockHash());
		if (block == null) return null;

		long blockHeight = block.height();
		long blockIndex;
		for (blockIndex=0; blockIndex<block.size(); blockIndex++) { if (block.tx().get((int) blockIndex).equals(txid)) break; }
		if (blockIndex == block.size()) return null;

		return new ChainAndBlockLocation(chain, blockHeight, blockIndex);
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
