package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import org.bitcoinj.core.BlockChain;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;

public class BitcoinjSPVBitcoinConnection extends AbstractBitcoinConnection implements BitcoinConnection {

	private static final BitcoinjSPVBitcoinConnection instance = new BitcoinjSPVBitcoinConnection();

	protected final BlockChain blockChain;

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
	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		throw new RuntimeException("Not implemented.");
	}

	@Override
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException {

		throw new RuntimeException("Not implemented.");
	}
}
