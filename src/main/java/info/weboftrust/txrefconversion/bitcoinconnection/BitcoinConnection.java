package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndBlockLocation;

public interface BitcoinConnection {

	public String getTxid(Chain chain, long blockHeight, long blockIndex) throws IOException;
	public String getTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException;
	public ChainAndBlockLocation getChainAndBlockLocation(Chain chain, String txid) throws IOException;
}
