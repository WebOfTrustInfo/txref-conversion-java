package info.weboftrust.txrefconversion;

public class ChainAndBlockLocation {

	public Chain chain;
	public long blockHeight;
	public long blockIndex;

	public ChainAndBlockLocation(Chain chain, long blockHeight, long blockIndex) {

		this.chain = chain;
		this.blockHeight = blockHeight;
		this.blockIndex = blockIndex;
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

	@Override
	public String toString() {
		return "ChainAndBlockLocation [chain=" + chain + ", blockHeight=" + blockHeight + ", blockIndex="
				+ blockIndex + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (blockHeight ^ (blockHeight >>> 32));
		result = prime * result + (int) (blockIndex ^ (blockIndex >>> 32));
		result = prime * result + ((chain == null) ? 0 : chain.hashCode());
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
		return true;
	}
}