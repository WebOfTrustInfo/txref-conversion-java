package info.weboftrust.txrefconversion;

public class ChainAndTxid {

	public Chain chain;
	public String txid;

	public ChainAndTxid(Chain chain, String txid) {

		this.chain = chain;
		this.txid = txid;
	}

	public Chain getChain() { 

		return this.chain;
	}

	public String getTxid() {

		return this.txid;
	}

	@Override
	public String toString() {
		return "ChainAndTxid [chain=" + chain + ", txid=" + txid + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chain == null) ? 0 : chain.hashCode());
		result = prime * result + ((txid == null) ? 0 : txid.hashCode());
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
		return true;
	}
}