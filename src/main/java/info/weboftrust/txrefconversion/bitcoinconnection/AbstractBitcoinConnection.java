package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import info.weboftrust.txrefconversion.ChainAndBlockLocation;

public abstract class AbstractBitcoinConnection implements BitcoinConnection {

	@Override
	public final String getTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException {

		return getTxid(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.getBlockIndex());
	}
}
