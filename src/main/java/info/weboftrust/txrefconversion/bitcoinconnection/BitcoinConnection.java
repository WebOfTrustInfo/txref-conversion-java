package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndBlockLocation;
import info.weboftrust.txrefconversion.ChainAndTxid;

public interface BitcoinConnection {

	public ChainAndTxid lookupChainAndTxid(ChainAndBlockLocation chainAndBlockLocation) throws IOException;
	public ChainAndTxid lookupChainAndTxid(Chain chain, long blockHeight, long blockIndex, long utxoIndex) throws IOException;
	public ChainAndTxid lookupChainAndTxid(Chain chain, long blockHeight, long blockIndex) throws IOException;
	public ChainAndBlockLocation lookupChainAndBlockLocation(ChainAndTxid chainAndTxid) throws IOException;
	public ChainAndBlockLocation lookupChainAndBlockLocation(Chain chain, String txid) throws IOException;

	public String toTxref(ChainAndTxid chainAndTxid) throws IOException;
	public ChainAndTxid fromTxref(String txref) throws IOException;
}
