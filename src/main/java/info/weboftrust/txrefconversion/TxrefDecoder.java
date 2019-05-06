package info.weboftrust.txrefconversion;

import info.weboftrust.txrefconversion.Bech32.HrpAndData;

public class TxrefDecoder {

	public static ChainAndBlockLocation txrefDecode(String txref) {

		String stripped = txref.replace("-", "");
		stripped = stripped.replace(":", "");

		HrpAndData result = Bech32.bech32Decode(stripped);
		if (result == null) return null;

		byte[] buf = result.getData();

		boolean extendedTxref = buf.length == 12;

		byte chainMarker = buf[0];

		long blockHeight = 0;
		long blockIndex = 0;
		long utxoIndex = 0;

		blockHeight = (buf[1] >> 1);
		blockHeight |= (buf[2] << 4);
		blockHeight |= (buf[3] << 9);
		blockHeight |= (buf[4] << 14);
		blockHeight |= (buf[5] << 19);

		blockIndex = buf[6];
		blockIndex |= (buf[7] << 5);
		blockIndex |= (buf[8] << 10);

		if (extendedTxref) {

			utxoIndex = buf[9];
			utxoIndex |= (buf[10] << 5);
			utxoIndex |= (buf[11] << 10);
		}

		Chain chain;

		if (chainMarker == TxrefConstants.MAGIC_BTC_MAINNET || chainMarker == TxrefConstants.MAGIC_BTC_MAINNET_EXTENDED) {

			chain = Chain.MAINNET;
		} else {

			chain = Chain.TESTNET;
		}

		if (extendedTxref) {

			return new ChainAndBlockLocation(chain, blockHeight, blockIndex, utxoIndex);
		} else {

			return new ChainAndBlockLocation(chain, blockHeight, blockIndex);
		}
	}
}
