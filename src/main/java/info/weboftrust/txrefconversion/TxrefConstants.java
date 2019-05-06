package info.weboftrust.txrefconversion;

public class TxrefConstants {

	public static final byte MAGIC_BTC_MAINNET = 0x03;
	public static final byte MAGIC_BTC_MAINNET_EXTENDED = 0x04;
	public static final byte MAGIC_BTC_TESTNET = 0x06;
	public static final byte MAGIC_BTC_TESTNET_EXTENDED = 0x07;

	public static final char MAGIC_BTC_MAINNET_BECH32_CHAR = Bech32.CHARSET.charAt(MAGIC_BTC_MAINNET);	// 'r'
	public static final char MAGIC_BTC_MAINNET_EXTENDED_BECH32_CHAR = Bech32.CHARSET.charAt(MAGIC_BTC_MAINNET_EXTENDED);	// 'y'
	public static final char MAGIC_BTC_TESTNET_BECH32_CHAR = Bech32.CHARSET.charAt(MAGIC_BTC_TESTNET);	// 'x'
	public static final char MAGIC_BTC_TESTNET_EXTENDED_BECH32_CHAR = Bech32.CHARSET.charAt(MAGIC_BTC_TESTNET_EXTENDED);	// '8'

	public static final String TXREF_BECH32_HRP_MAINNET = "tx";
	public static final String TXREF_BECH32_HRP_TESTNET = "txtest";

	public static final byte[] TXREF_BECH32_HRP_MAINNET_BYTES = TXREF_BECH32_HRP_MAINNET.getBytes();
	public static final byte[] TXREF_BECH32_HRP_TESTNET_BYTES = TXREF_BECH32_HRP_TESTNET.getBytes();
}
