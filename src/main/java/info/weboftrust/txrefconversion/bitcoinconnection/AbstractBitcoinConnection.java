package info.weboftrust.txrefconversion.bitcoinconnection;

import java.io.IOException;

import info.weboftrust.txrefconversion.Chain;
import info.weboftrust.txrefconversion.ChainAndBlockLocation;
import info.weboftrust.txrefconversion.ChainAndTxid;
import info.weboftrust.txrefconversion.TxrefDecoder;
import info.weboftrust.txrefconversion.TxrefEncoder;

public abstract class AbstractBitcoinConnection implements BitcoinConnection {

	@Override
	public ChainAndTxid lookupChainAndTxid(Chain chain, long blockHeight, long blockIndex, long utxoIndex) throws IOException {

		return this.lookupChainAndTxid(new ChainAndBlockLocation(chain, blockHeight, blockIndex, utxoIndex));
	}

	@Override
	public ChainAndTxid lookupChainAndTxid(Chain chain, long blockHeight, long blockIndex) throws IOException {

		return this.lookupChainAndTxid(new ChainAndBlockLocation(chain, blockHeight, blockIndex));
	}

	@Override
	public ChainAndBlockLocation lookupChainAndBlockLocation(Chain chain, String txid) throws IOException {

		return this.lookupChainAndBlockLocation(new ChainAndTxid(chain, txid));
	}

	@Override
	public String toTxref(ChainAndTxid chainAndTxid) throws IOException {

		ChainAndBlockLocation blockLocation = this.lookupChainAndBlockLocation(chainAndTxid);
		if (blockLocation == null) return null;

		String txref = TxrefEncoder.txrefEncode(blockLocation);
		return txref;
	}

	@Override
	public ChainAndTxid fromTxref(String txref) throws IOException {

		ChainAndBlockLocation chainAndBlockLocation = TxrefDecoder.txrefDecode(txref);
		if (chainAndBlockLocation == null) throw new IllegalArgumentException("Could not decode txref " + txref);

		return this.lookupChainAndTxid(chainAndBlockLocation);
	}
}
