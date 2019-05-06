package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndBlockLocation;
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
	public ChainAndTxid lookupChainAndTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chainAndBlockLocation.getChain() == Chain.MAINNET ? this.bitcoindRpcClientMainnet : this.bitcoindRpcClientTestnet;

		Block block = bitcoindRpcClient.getBlock((int) chainAndBlockLocation.getBlockHeight());
		if (block == null) return null;
		if (block.tx().size() <= chainAndBlockLocation.getBlockIndex()) return null;

		String txid = block.tx().get((int) chainAndBlockLocation.getBlockIndex());

		return new ChainAndTxid(chainAndBlockLocation.getChain(), txid, chainAndBlockLocation.getUtxoIndex());
	}

	@Override
	public ChainAndBlockLocation lookupChainAndBlockLocation(ChainAndTxid chainAndTxid) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chainAndTxid.getChain() == Chain.MAINNET ? bitcoindRpcClientMainnet : bitcoindRpcClientTestnet;

		RawTransaction rawTransaction = bitcoindRpcClient.getRawTransaction(chainAndTxid.getTxid());
		if (rawTransaction == null) return null;

		Block block = bitcoindRpcClient.getBlock(rawTransaction.blockHash());
		if (block == null) return null;

		long blockHeight = block.height();
		long blockIndex;
		for (blockIndex=0; blockIndex<block.size(); blockIndex++) { if (block.tx().get((int) blockIndex).equals(chainAndTxid.getTxid())) break; }
		if (blockIndex == block.size()) return null;

		return new ChainAndBlockLocation(chainAndTxid.getChain(), blockHeight, blockIndex, chainAndTxid.getUtxoIndex());
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
