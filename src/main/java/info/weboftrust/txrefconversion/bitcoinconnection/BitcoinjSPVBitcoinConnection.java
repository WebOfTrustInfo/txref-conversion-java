package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import org.bitcoinj.core.BlockChain;

import info.weboftrust.txrefconversion.ChainAndBlockLocation;
import info.weboftrust.txrefconversion.ChainAndTxid;

public class BitcoinjSPVBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final BitcoinjSPVBitcoinConnection instance = new BitcoinjSPVBitcoinConnection();

	protected BlockChain blockChain;

	public BitcoinjSPVBitcoinConnection(BlockChain blockChain) {

		this.blockChain = blockChain;
	}

	public BitcoinjSPVBitcoinConnection() {

		this(null);
	}

	public static BitcoinjSPVBitcoinConnection get() {

		return instance;
	}

	@Override
	public ChainAndTxid lookupChainAndTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException {

		throw new RuntimeException("Not implemented.");
	}

	@Override
	public ChainAndBlockLocation lookupChainAndBlockLocation(ChainAndTxid chainAndTxid) throws IOException {

		throw new RuntimeException("Not implemented.");
	}

	/*
	 * Getters and setters
	 */

	public BlockChain getBlockChain() {

		return this.blockChain;
	}

	public void setBlockChain(BlockChain blockChain) {

		this.blockChain = blockChain;
	}
}
