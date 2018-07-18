package info.weboftrust.txrefconversion;

public class ChainAndBlockLocationAndUtxoIndex extends ChainAndBlockLocation {

	public long utxoIndex;

	public ChainAndBlockLocationAndUtxoIndex(Chain chain, long blockHeight, long blockIndex, long utxoIndex) {

		super(chain, blockHeight, blockIndex);
		this.utxoIndex = utxoIndex;
	}

	public ChainAndBlockLocationAndUtxoIndex(ChainAndBlockLocation chainAndBlockLocation, long utxoIndex) {

		super(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.blockIndex);
		this.utxoIndex = utxoIndex;
	}

	public ChainAndBlockLocationAndUtxoIndex(ChainAndBlockLocation chainAndBlockLocation) {

		super(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.blockIndex);
		this.utxoIndex = 0;
	}

	public long getUtxoIndex() {

		return this.utxoIndex;
	}

	@Override
	public String toString() {
		return "ChainAndBlockLocationAndUtxoIndex [chain=" + chain + ", blockHeight=" + blockHeight + ", blockIndex="
				+ blockIndex + ", utxoIndex=" + utxoIndex + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blockHeight ^ (blockHeight >>> 32));
		result = prime * result + (int) (blockIndex ^ (blockIndex >>> 32));
		result = prime * result + ((chain == null) ? 0 : chain.hashCode());
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
		ChainAndBlockLocationAndUtxoIndex other = (ChainAndBlockLocationAndUtxoIndex) obj;
		if (blockHeight != other.blockHeight)
			return false;
		if (blockIndex != other.blockIndex)
			return false;
		if (chain != other.chain)
			return false;
		if (utxoIndex != other.utxoIndex)
			return false;
		return true;
	}
}