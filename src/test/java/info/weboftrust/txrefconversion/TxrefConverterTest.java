package info.weboftrust.txrefconversion;

import info.weboftrust.txrefconversion.TxrefConverter.Chain;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndBlockLocation;
import info.weboftrust.txrefconversion.TxrefConverter.ChainAndTxid;
import info.weboftrust.txrefconversion.bitcoinconnection.BlockcypherAPIBitcoinConnection;
import junit.framework.TestCase;

public class TxrefConverterTest extends TestCase {

	private static TxrefConverter txrefConverter;

	static {

		txrefConverter = new TxrefConverter(BlockcypherAPIBitcoinConnection.get());
	}

	@Override
	protected void setUp() throws Exception {

	}

	@Override
	protected void tearDown() throws Exception {

	}

	private static Object[][] tests1 = new Object[][] {
		new Object[] { Chain.MAINNET, "tx1-rqqq-qqqq-qmhu-qk", (long) 0, (long) 0 },	
		new Object[] { Chain.MAINNET, "tx1-rzqq-qqqq-uvlj-ez", (long) 1, (long) 0 },	
		new Object[] { Chain.MAINNET, "tx1-r7ll-lrar-a27h-kt", (long) 2097151, (long) 1000 },	
		new Object[] { Chain.MAINNET, "tx1-r7ll-llll-khym-tq", (long) 2097151, (long) 8191 },	
		new Object[] { Chain.MAINNET, "tx1-rjk0-u5ng-4jsf-mc", (long) 466793, (long) 2205 },	
		new Object[] { Chain.MAINNET, "tx1-rk63-uvxf-9pqc-sy", (long) 467883, (long) 2355 },	
		new Object[] { Chain.MAINNET, "tx1-r7ll-lrar-a27h-kt", (long) 2097151, (long) 1000 },	
		new Object[] { Chain.MAINNET, "tx1-r7ll-llll-khym-tq", (long) 2097151, (long) 8191 },	
		new Object[] { Chain.MAINNET, "tx1-r7ll-lrqq-vq5e-gg", (long) 2097151, (long) 0 },	
		new Object[] { Chain.MAINNET, "tx1-rqqq-qull-6v87-r7", (long) 0, (long) 8191 },	
		new Object[] { Chain.TESTNET, "txtest1-xk63-uqvx-fqx8-xqr8", (long) 467883, (long) 2355 },	
		new Object[] { Chain.TESTNET, "txtest1-xqqq-qqqq-qqkn-3gh9", (long) 0, (long) 0 },	
		new Object[] { Chain.TESTNET, "txtest1-xyv2-xzyq-qqm5-tyke", (long) 1152194, (long) 1 }	
	};

	public void testTxRefEncode() throws Exception {

		for (Object[] test : tests1) {

			String result = txrefConverter.txrefEncode((Chain) test[0], (long) test[2], (long) test[3]);
			assertEquals((String) test[1], result);
		}
	}

	public void testTxRefDecode() throws Exception {

		for (Object[] test : tests1) {

			ChainAndBlockLocation result = txrefConverter.txrefDecode((String) test[1]);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals((long) test[2], result.getBlockHeight());
			assertEquals((long) test[3], result.getBlockIndex());
		}
	}

	private static Object[][] tests2 = new Object[][] {
		new Object[] { Chain.TESTNET, "f8cdaff3ebd9e862ed5885f8975489090595abe1470397f79780ead1c7528107", "txtest1-xyv2-xzyq-qqm5-tyke" },
		new Object[] { Chain.MAINNET, "016b71d9ec62709656504f1282bb81f7acf998df025e54bd68ea33129d8a425b", "tx1-rk63-uvxf-9pqc-sy" }
	};

	public void testTxidToTxref() throws Exception {

		for (Object[] test : tests2) {

			String result = txrefConverter.txidToTxref((String) test[1], (Chain) test[0]);
			assertEquals((String) test[2], result);
		}
	}

	public void testTxrefToTxid() throws Exception {

		for (Object[] test : tests2) {

			ChainAndTxid result = txrefConverter.txrefToTxid((String) test[2]);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals((String) test[1], result.getTxid());
		}
	}
}
