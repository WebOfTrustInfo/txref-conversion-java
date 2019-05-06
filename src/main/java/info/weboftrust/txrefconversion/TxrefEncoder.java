package info.weboftrust.txrefconversion;

public class TxrefEncoder {

	/*
	 * txref encode / decode
	 */

	public static String txrefEncode(Chain chain, long blockHeight, long blockIndex, long utxoIndex) {

		byte[] prefix = chain == Chain.MAINNET ? TxrefConstants.TXREF_BECH32_HRP_MAINNET_BYTES : TxrefConstants.TXREF_BECH32_HRP_TESTNET_BYTES;
		boolean extendedTxref = utxoIndex >= 0;

		byte magic;

		if (extendedTxref) {
			magic = chain == Chain.MAINNET ? TxrefConstants.MAGIC_BTC_MAINNET_EXTENDED : TxrefConstants.MAGIC_BTC_TESTNET_EXTENDED;
		} else {
			magic = chain == Chain.MAINNET ? TxrefConstants.MAGIC_BTC_MAINNET : TxrefConstants.MAGIC_BTC_TESTNET;
		}

		byte[] shortId;

		if (extendedTxref) {

			shortId = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		} else {

			shortId = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		}

		if (blockHeight > 0xFFFFFF || blockIndex > 0x7FFF || magic > 0x1F) return null;
		if (extendedTxref && utxoIndex > 0x7FFF) return null;

		/* set the magic */
		shortId[0] = magic;

		/* make sure the version bit is 0 */
		shortId[1] &= ~(1 << 0);

		shortId[1] |= ((blockHeight & 0xF) << 1);
		shortId[2] |= ((blockHeight & 0x1F0) >> 4);
		shortId[3] |= ((blockHeight & 0x3E00) >> 9);
		shortId[4] |= ((blockHeight & 0x7C000) >> 14);
		shortId[5] |= ((blockHeight & 0xF80000) >> 19);

		shortId[6] |= (blockIndex & 0x1F);
		shortId[7] |= ((blockIndex & 0x3E0) >> 5);
		shortId[8] |= ((blockIndex & 0x7C00) >> 10);

		if(extendedTxref) {

			shortId[9] |= (utxoIndex & 0x1F);
			shortId[10] |= ((utxoIndex & 0x3E0) >> 5);
			shortId[11] |= ((utxoIndex & 0x7C00) >> 10);
		}

		String result = Bech32.bech32Encode(prefix, shortId);

		int origLength = result.length();
		int breakIndex = prefix.length + 1;
		String finalResult = result.substring(0, breakIndex) + ":" +
				result.substring(breakIndex, breakIndex + 4) + "-" +
				result.substring(breakIndex + 4, breakIndex + 8) + "-" +
				result.substring(breakIndex + 8, breakIndex + 12) + "-";

		if (origLength - breakIndex < 16) {

			finalResult += result.substring(breakIndex + 12, result.length());
		} else {

			finalResult += result.substring(breakIndex + 12, breakIndex + 16) + "-" + result.substring(breakIndex + 16, result.length());
		}

		return finalResult;
	}

	public static String txrefEncode(Chain chain, long blockHeight, long blockIndex) {

		return txrefEncode(chain, blockHeight, blockIndex, -1);
	}

	public static String txrefEncode(ChainAndBlockLocation chainAndBlockLocation) {

		return txrefEncode(chainAndBlockLocation.getChain(), chainAndBlockLocation.getBlockHeight(), chainAndBlockLocation.getBlockIndex(), chainAndBlockLocation.getUtxoIndex());
	}
}
