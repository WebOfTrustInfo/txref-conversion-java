package info.weboftrust.txrefconversion;

import java.io.IOException;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;

public interface BlockchainSource {

	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException;
	public String getTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException;
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException;
}
