package info.weboftrust.txrefconversion;

public class ChainAndTxid {

	public Chain chain;
	public String txid;
	public long utxoIndex;

	public ChainAndTxid(Chain chain, String txid, long utxoIndex) {

		this.chain = chain;
		this.txid = txid;
		this.utxoIndex = utxoIndex;
	}

	public ChainAndTxid(Chain chain, String txid) {

		this(chain, txid, -1);
	}

	public Chain getChain() { 

		return this.chain;
	}

	public String getTxid() {

		return this.txid;
	}

	public long getUtxoIndex() {

		return this.utxoIndex;
	}

	@Override
	public String toString() {
		return "ChainAndTxid [chain=" + chain + ", txid=" + txid + ", utxoIndex=" + utxoIndex + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chain == null) ? 0 : chain.hashCode());
		result = prime * result + ((txid == null) ? 0 : txid.hashCode());
		result = prime * result + (int) (utxoIndex ^ (utxoIndex >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChainAndTxid other = (ChainAndTxid) obj;
		if (chain != other.chain)
			return false;
		if (txid == null) {
			if (other.txid != null)
				return false;
		} else if (!txid.equals(other.txid))
			return false;
		if (utxoIndex != other.utxoIndex)
			return false;
		return true;
	}
}