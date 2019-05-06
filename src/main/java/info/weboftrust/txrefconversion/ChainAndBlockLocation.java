package info.weboftrust.txrefconversion;

public class ChainAndBlockLocation {

	private Chain chain;
	private long blockHeight;
	private long blockIndex;
	private long utxoIndex;

	public ChainAndBlockLocation(Chain chain, long blockHeight, long blockIndex, long utxoIndex) {

		this.chain = chain;
		this.blockHeight = blockHeight;
		this.blockIndex = blockIndex;
		this.utxoIndex = utxoIndex;
	}

	public ChainAndBlockLocation(Chain chain, long blockHeight, long blockIndex) {

		this(chain, blockHeight, blockIndex, -1);
	}

	public Chain getChain() { 

		return this.chain;
	}

	public long getBlockHeight() { 

		return this.blockHeight;
	}

	public long getBlockIndex() {

		return this.blockIndex;
	}

	public long getUtxoIndex() {

		return this.utxoIndex;
	}

	@Override
	public String toString() {
		return "ChainAndBlockLocation [chain=" + chain + ", blockHeight=" + blockHeight + ", blockIndex=" + blockIndex
				+ ", utxoIndex=" + utxoIndex + "]";
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
		ChainAndBlockLocation other = (ChainAndBlockLocation) obj;
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