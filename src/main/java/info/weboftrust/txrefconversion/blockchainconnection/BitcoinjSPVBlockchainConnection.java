package info.weboftrust.txrefconversion.blockchainconnection;

import java.io.IOException;

import org.bitcoinj.core.BlockChain;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;

public class BitcoinjSPVBlockchainConnection extends AbstractBlockchainConnection implements BlockchainConnection {

	private static final BitcoinjSPVBlockchainConnection instance = new BitcoinjSPVBlockchainConnection();

	private final BlockChain blockChain;

	private BitcoinjSPVBlockchainConnection(BlockChain blockChain) {

		this.blockChain = blockChain;
	}

	private BitcoinjSPVBlockchainConnection() {

		this(null);
	}

	public static BitcoinjSPVBlockchainConnection get() {

		return instance;
	}

	@Override
	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		throw new RuntimeException("Not implemented.");
	}

	@Override
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException {

		throw new RuntimeException("Not implemented.");
	}
}
