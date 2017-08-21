package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;
import java.net.URL;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.Block;
import wf.bitcoin.javabitcoindrpcclient.BitcoindRpcClient.RawTransaction;

public class BitcoindRPCBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final BitcoindRPCBitcoinConnection instance = new BitcoindRPCBitcoinConnection();

	protected final BitcoinJSONRPCClient bitcoindRpcClientMainnet;
	protected final BitcoinJSONRPCClient bitcoindRpcClientTestnet;

	public BitcoindRPCBitcoinConnection(URL rpcUrlMainnet, URL rpcUrlTestnet) {

		this.bitcoindRpcClientMainnet = new BitcoinJSONRPCClient(rpcUrlMainnet);
		this.bitcoindRpcClientTestnet = new BitcoinJSONRPCClient(rpcUrlTestnet);
	}

	public BitcoindRPCBitcoinConnection() {

		this(BitcoinJSONRPCClient.DEFAULT_JSONRPC_URL, BitcoinJSONRPCClient.DEFAULT_JSONRPC_TESTNET_URL);
	}

	public static BitcoindRPCBitcoinConnection get() {

		return instance;
	}

	@Override
	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		BitcoindRpcClient bitcoindRpcClient = chain == Chain.MAINNET ? bitcoindRpcClientMainnet : bitcoindRpcClientTestnet;

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
}
