package info.weboftrust.txrefconversion.blockchainconnection;

import java.io.IOException;

import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;

public abstract class AbstractBlockchainConnection implements BlockchainConnection {

	@Override
	public final String getTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException {

		return getTxid(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.getBlockIndex());
	}
}
