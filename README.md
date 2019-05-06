Built a few weeks after the [BTCR Virtual Hackathon](https://github.com/WebOfTrustInfo/btcr-hackathon) July 10th - July 16th 2017.

![RWoT Logo](https://github.com/WebOfTrustInfo/ld-signatures-java/blob/master/wot-logo.png?raw=true)

### Information

This is an implementation of [BIP 136: Bech32 Encoded Tx Position References](https://github.com/bitcoin/bips/pull/555).

Use at your own risk! Pull requests welcome.

### Maven

Build:

	mvn clean install

Dependency:

	<dependency>
		<groupId>info.weboftrust</groupId>
		<artifactId>txref-conversion-java</artifactId>
		<version>0.1-SNAPSHOT</version>
		<scope>compile</scope>
	</dependency>

### Examples

#### Convert a TXID to a TX ref

	import info.weboftrust.txrefconversion.TxrefConverter;
	import info.weboftrust.txrefconversion.TxrefConverter.Chain;
	
	public class Test {
		
		public static void main(String[] args) throws Exception {
	
			BitcoinConnection bitcoinConnection = BlockcypherAPIBitcoinConnection.get();
			String txref = bitcoinConnection.toTxref(Chain.MAINNET, "016b71d9ec62709656504f1282bb81f7acf998df025e54bd68ea33129d8a425b");
			System.out.println(txref); // expect "tx1:rk63-uqnf-zscg-527"
		}
	}

#### Convert a TX ref to a TXID

	import info.weboftrust.txrefconversion.TxrefConverter;
	import info.weboftrust.txrefconversion.TxrefConverter.TxidAndChain;
	
	public class Test {
		
		public static void main(String[] args) throws Exception {
	
			BitcoinConnection bitcoinConnection = BlockcypherAPIBitcoinConnection.get();
			ChainAndTxid chainAndTxid = bitcoinConnection.fromTxref("txtest1:xyv2-xzpq-q9wa-p7t");
			System.out.println(chainAndTxid.getChain()); // expect "TESTNET"
			System.out.println(chainAndTxid.getTxid()); // expect "f8cdaff3ebd9e862ed5885f8975489090595abe1470397f79780ead1c7528107"
		}
	}


### About

Rebooting Web-of-Trust - http://www.weboftrust.info/

Markus Sabadello, Danube Tech -  https://danubetech.com/
