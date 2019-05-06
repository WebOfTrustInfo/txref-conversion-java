package info.weboftrust.txrefconversion;

import junit.framework.TestCase;

public class TxrefConverterTest extends TestCase {

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

		// mainnet

		new Object[] { Chain.MAINNET, "tx1:rqqq-qqqq-qmhu-qhp", (long) 0x0, (long) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:rqqq-qqll-l8xh-jkg", (long) 0x0, (long) 0x7FFF },
		new Object[] { Chain.MAINNET, "tx1:r7ll-llqq-qghq-qr8", (long) 0xFFFFFF, (long) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:r7ll-llll-l5xt-jzw", (long) 0xFFFFFF, (long) 0x7FFF },

		new Object[] { Chain.MAINNET, "tx1:rzqq-qqqq-qgqu-t84", (long) 1, (long) 0 },	
		new Object[] { Chain.MAINNET, "tx1:r7ll-lrgl-qqsy-mff", (long) 2097151, (long) 1000 },	
		new Object[] { Chain.MAINNET, "tx1:r7ll-lrll-8xwc-yjp", (long) 2097151, (long) 8191 },	
		new Object[] { Chain.MAINNET, "tx1:rk63-uqnf-zscg-527", (long) 467883, (long) 2355 },	
		new Object[] { Chain.MAINNET, "tx1:r7ll-lrqq-q32l-zcx", (long) 2097151, (long) 0 },	
		new Object[] { Chain.MAINNET, "tx1:rqqq-qqll-8vnm-xax", (long) 0, (long) 8191 },	
		new Object[] { Chain.MAINNET, "tx1:rjk0-uqay-zsrw-hqe", (long) 0x71F69, (long) 0x89D },	

		// testnet

		new Object[] { Chain.TESTNET, "txtest1:xqqq-qqqq-qkla-64l", (long) 0, (long) 0 },	
		new Object[] { Chain.TESTNET, "txtest1:xqqq-qqll-l2wk-g5k", (long) 0, (long) 0x7FFF },	
		new Object[] { Chain.TESTNET, "txtest1:x7ll-llqq-q9lp-6pe", (long) 0xFFFFFF, (long) 0 },	
		new Object[] { Chain.TESTNET, "txtest1:x7ll-llll-lew2-gqs", (long) 0xFFFFFF, (long) 0x7FFF },	

		new Object[] { Chain.TESTNET, "txtest1:xk63-uqnf-zasf-wgq", (long) 467883, (long) 2355 },	
		new Object[] { Chain.TESTNET, "txtest1:xyv2-xzpq-q9wa-p7t", (long) 1152194, (long) 1 },

		// mainnet with utxoIndex

		new Object[] { Chain.MAINNET, "tx1:yqqq-qqqq-qqqq-ksvh-26", (long) 0x0, (long) 0x0, (long) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:yqqq-qqll-lqqq-v0h2-2k", (long) 0x0, (long) 0x7FFF, (long) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llqq-qqqq-a5zy-tc", (long) 0xFFFFFF, (long) 0x0, (long) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llll-lqqq-8tee-t5", (long) 0xFFFFFF, (long) 0x7FFF, (long) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:yqqq-qqqq-qpqq-5j9q-nz", (long) 0x0, (long) 0x0, (long) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:yqqq-qqll-lpqq-wd7a-nw", (long) 0x0, (long) 0x7FFF, (long) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llqq-qpqq-lktn-jq", (long) 0xFFFFFF, (long) 0, (long) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llll-lpqq-9fsw-jv", (long) 0xFFFFFF, (long) 0x7FFF, (long) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:yjk0-uqay-zrfq-g2cg-t8", (long) 0x71F69, (long) 0x89D, (long) 0x123 },
		new Object[] { Chain.MAINNET, "tx1:yjk0-uqay-zu4x-nk6u-pc", (long) 0x71F69, (long) 0x89D, (long) 0x1ABC },

		// testnet with utxoIndex

		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqqq-qqqq-cgru-fa", (long) 0x0, (long) 0x0, (long) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqll-lqqq-zhcp-f3", (long) 0x0, (long) 0x7FFF, (long) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llqq-qqqq-nvd0-gl", (long) 0xFFFFFF, (long) 0x0, (long) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llll-lqqq-fnkj-gn", (long) 0xFFFFFF, (long) 0x7FFF, (long) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqqq-qpqq-622t-s9", (long) 0x0, (long) 0x0, (long) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqll-lpqq-q43k-sf", (long) 0x0, (long) 0x7FFF, (long) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llqq-qpqq-3wyc-38", (long) 0xFFFFFF, (long) 0x0, (long) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llll-lpqq-t3l9-3t", (long) 0xFFFFFF, (long) 0x7FFF, (long) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:8jk0-uqay-zrfq-xjhr-gq", (long) 0x71F69, (long) 0x89D, (long) 0x123 },
		new Object[] { Chain.TESTNET, "txtest1:8jk0-uqay-zu4x-aw4h-zl", (long) 0x71F69, (long) 0x89D, (long) 0x1ABC },

		new Object[] { Chain.TESTNET, "txtest1:8z35-jznz-qqqq-xstv-nc", (long) 1354001, (long) 83, (long) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8kyt-fzzq-qqqq-ase0-d8", (long) 1201739, (long) 2, (long) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8ksa-czpq-qqqq-k85h-97", (long) 1456907, (long) 1, (long) 0x0 }
	};

	public void testTxrefEncode() throws Exception {

		for (Object[] test : tests1) {

			String result;

			if (test.length > 4)			
				result = TxrefEncoder.txrefEncode((Chain) test[0], (long) test[2], (long) test[3], (long) test[4]);
			else
				result = TxrefEncoder.txrefEncode((Chain) test[0], (long) test[2], (long) test[3]);

			assertEquals((String) test[1], result);
		}
	}

	public void testTxrefDecode() throws Exception {

		for (Object[] test : tests1) {

			String txref = (String) test[1];

			ChainAndBlockLocation result = TxrefDecoder.txrefDecode(txref);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals((long) test[2], result.getBlockHeight());
			assertEquals((long) test[3], result.getBlockIndex());

			if (test.length > 4) assertEquals((long) test[4], result.getUtxoIndex());
		}
	}
}
