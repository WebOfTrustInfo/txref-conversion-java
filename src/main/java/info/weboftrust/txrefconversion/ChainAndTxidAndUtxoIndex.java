package info.weboftrust.txrefconversion;

public class ChainAndTxidAndUtxoIndex extends ChainAndTxid{

	public long utxoIndex;

	protected ChainAndTxidAndUtxoIndex(Chain chain, String txid, long utxoIndex) {

		super(chain, txid);
		this.utxoIndex = utxoIndex;
	}

	public ChainAndTxidAndUtxoIndex create(Chain chain, String txid, long utxoIndex) {

		return new ChainAndTxidAndUtxoIndex(chain, txid, utxoIndex);
	}

	public ChainAndTxidAndUtxoIndex create(ChainAndTxid chainAndTxid, long utxoIndex) {

		return new ChainAndTxidAndUtxoIndex(chainAndTxid.getChain(), chainAndTxid.getTxid(), utxoIndex);
	}

	public long getUtxoIndex() {

		return this.utxoIndex;
	}

	@Override
	public String toString() {
		return "ChainAndTxidAndUtxoIndex [utxoIndex=" + utxoIndex + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (utxoIndex ^ (utxoIndex >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChainAndTxidAndUtxoIndex other = (ChainAndTxidAndUtxoIndex) obj;
		if (utxoIndex != other.utxoIndex)
			return false;
		return true;
	}
}