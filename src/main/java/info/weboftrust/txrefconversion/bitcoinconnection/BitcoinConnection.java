package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndLocationData;
import info.weboftrust.txrefconversion.ChainAndTxid;

public interface BitcoinConnection {

	public ChainAndTxid lookupChainAndTxid(ChainAndLocationData chainAndLocationData) throws IOException;
	public ChainAndTxid lookupChainAndTxid(Chain chain, int blockHeight, int transactionPosition, int txoIndex) throws IOException;
	public ChainAndTxid lookupChainAndTxid(Chain chain, int blockHeight, int transactionPosition) throws IOException;
	public ChainAndLocationData lookupChainAndLocationData(ChainAndTxid chainAndTxid) throws IOException;
	public ChainAndLocationData lookupChainAndLocationData(Chain chain, String txid, int txoIndex) throws IOException;
	public ChainAndLocationData lookupChainAndLocationData(Chain chain, String txid) throws IOException;

	public String toTxref(ChainAndTxid chainAndTxid) throws IOException;
	public String toTxref(Chain chain, String txid) throws IOException;
	public ChainAndTxid fromTxref(String txref) throws IOException;
}
