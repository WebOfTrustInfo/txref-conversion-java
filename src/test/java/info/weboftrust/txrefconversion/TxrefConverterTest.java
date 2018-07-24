package info.weboftrust.txrefconversion;

import junit.framework.TestCase;

public class TxrefConverterTest extends TestCase {

	private static TxrefConverter txrefConverter;

	static {

		txrefConverter = TxrefConverter.get();
	}

	@Override
	protected void setUp() throws Exception {

	}

	@Override
	protected void tearDown() throws Exception {

	}

	/*
	 * txref encode / decode
	 */

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

	public void testTxrefEncode() throws Exception {

		for (Object[] test : tests1) {

			String result = txrefConverter.txrefEncode((Chain) test[0], (long) test[2], (long) test[3]);
			assertEquals((String) test[1], result);
		}
	}

	public void testTxrefDecode() throws Exception {

		for (Object[] test : tests1) {

			ChainAndBlockLocation result = txrefConverter.txrefDecode((String) test[1]);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals((long) test[2], result.getBlockHeight());
			assertEquals((long) test[3], result.getBlockIndex());
		}
	}

	/*
	 * txrefext encode / decode
	 */

	private static Object[][] tests2 = new Object[][] {
		new Object[] { Chain.MAINNET, "tx1-rqqq-qqqq-qqqu-au7hl", (long) 0, (long) 0, (long) 0 },
		new Object[] { Chain.MAINNET, "tx1-rqqq-qqqq-yrq9-mqh4w", (long) 0, (long) 0, (long) 100 },
		new Object[] { Chain.MAINNET, "tx1-rqqq-qqqq-ll8t-emcac", (long) 0, (long) 0, (long) 0x1FFF },
		new Object[] { Chain.MAINNET, "tx1-rqqq-qull-qqq5-ktx95", (long) 0, (long) 0x1FFF, (long) 0 },
		new Object[] { Chain.MAINNET, "tx1-rqqq-qull-yrqd-sh089", (long) 0, (long) 0x1FFF, (long) 100 },
		new Object[] { Chain.MAINNET, "tx1-r7ll-lrqq-qqqm-m5vjv", (long) 0x1FFFFF, (long) 0, (long) 0 },
		new Object[] { Chain.MAINNET, "tx1-r7ll-llll-qqqn-sr5q8", (long) 0x1FFFFF, (long) 0x1FFF, (long) 0 },
		new Object[] { Chain.MAINNET, "tx1-r7ll-llll-yrq2-klazk", (long) 0x1FFFFF, (long) 0x1FFF, (long) 100 },
		new Object[] { Chain.MAINNET, "tx1-rjk0-u5ng-qqq8-lsnk3", (long) 0x71F69, (long) 0x89D, (long) 0 },
		new Object[] { Chain.MAINNET, "tx1-rjk0-u5ng-dqqa-9wk8d", (long) 0x71F69, (long) 0x89D, (long) 13 }
	};

	public void testTxrefextEncode() throws Exception {

		for (Object[] test : tests2) {

			String result = txrefConverter.txrefextEncode((Chain) test[0], (long) test[2], (long) test[3], (long) test[4]);
			assertEquals((String) test[1], result);
		}
	}

	public void testTxrefextDecode() throws Exception {

		for (Object[] test : tests2) {

			ChainAndBlockLocationAndUtxoIndex result = txrefConverter.txrefextDecode((String) test[1]);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals((long) test[2], result.getBlockHeight());
			assertEquals((long) test[3], result.getBlockIndex());
			assertEquals((long) test[4], result.getUtxoIndex());
		}
	}

	/*
	 * txid <-> txref
	 */

	private static Object[][] tests3 = new Object[][] {
		new Object[] { Chain.TESTNET, "f8cdaff3ebd9e862ed5885f8975489090595abe1470397f79780ead1c7528107", "txtest1-xyv2-xzyq-qqm5-tyke" },
		new Object[] { Chain.MAINNET, "016b71d9ec62709656504f1282bb81f7acf998df025e54bd68ea33129d8a425b", "tx1-rk63-uvxf-9pqc-sy" }
	};

	public void testTxidToTxref() throws Exception {

		for (Object[] test : tests3) {

			String result = txrefConverter.txidToTxref((String) test[1], (Chain) test[0]);
			assertEquals((String) test[2], result);
		}
	}

	public void testTxrefToTxid() throws Exception {

		for (Object[] test : tests3) {

			ChainAndTxid result = txrefConverter.txrefToTxid((String) test[2]);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals((String) test[1], result.getTxid());
		}
	}
}
