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

		new Object[] { Chain.MAINNET, "tx1:rqqq-qqqq-qmhu-qhp", (int) 0x0, (int) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:rqqq-qqll-l8xh-jkg", (int) 0x0, (int) 0x7FFF },
		new Object[] { Chain.MAINNET, "tx1:r7ll-llqq-qghq-qr8", (int) 0xFFFFFF, (int) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:r7ll-llll-l5xt-jzw", (int) 0xFFFFFF, (int) 0x7FFF },

		new Object[] { Chain.MAINNET, "tx1:rzqq-qqqq-qgqu-t84", (int) 1, (int) 0 },	
		new Object[] { Chain.MAINNET, "tx1:r7ll-lrgl-qqsy-mff", (int) 2097151, (int) 1000 },	
		new Object[] { Chain.MAINNET, "tx1:r7ll-lrll-8xwc-yjp", (int) 2097151, (int) 8191 },	
		new Object[] { Chain.MAINNET, "tx1:rk63-uqnf-zscg-527", (int) 467883, (int) 2355 },	
		new Object[] { Chain.MAINNET, "tx1:r7ll-lrqq-q32l-zcx", (int) 2097151, (int) 0 },	
		new Object[] { Chain.MAINNET, "tx1:rqqq-qqll-8vnm-xax", (int) 0, (int) 8191 },	
		new Object[] { Chain.MAINNET, "tx1:rjk0-uqay-zsrw-hqe", (int) 0x71F69, (int) 0x89D },	

		// testnet

		new Object[] { Chain.TESTNET, "txtest1:xqqq-qqqq-qkla-64l", (int) 0, (int) 0 },	
		new Object[] { Chain.TESTNET, "txtest1:xqqq-qqll-l2wk-g5k", (int) 0, (int) 0x7FFF },	
		new Object[] { Chain.TESTNET, "txtest1:x7ll-llqq-q9lp-6pe", (int) 0xFFFFFF, (int) 0 },	
		new Object[] { Chain.TESTNET, "txtest1:x7ll-llll-lew2-gqs", (int) 0xFFFFFF, (int) 0x7FFF },	

		new Object[] { Chain.TESTNET, "txtest1:xk63-uqnf-zasf-wgq", (int) 467883, (int) 2355 },	
		new Object[] { Chain.TESTNET, "txtest1:xyv2-xzpq-q9wa-p7t", (int) 1152194, (int) 1 },
		new Object[] { Chain.TESTNET, "txtest1:xz35-jznz-q6mr-7q6", (int) 1354001, (int) 83 },

		// mainnet with utxoIndex

		new Object[] { Chain.MAINNET, "tx1:yqqq-qqqq-qqqq-ksvh-26", (int) 0x0, (int) 0x0, (int) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:yqqq-qqll-lqqq-v0h2-2k", (int) 0x0, (int) 0x7FFF, (int) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llqq-qqqq-a5zy-tc", (int) 0xFFFFFF, (int) 0x0, (int) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llll-lqqq-8tee-t5", (int) 0xFFFFFF, (int) 0x7FFF, (int) 0x0 },
		new Object[] { Chain.MAINNET, "tx1:yqqq-qqqq-qpqq-5j9q-nz", (int) 0x0, (int) 0x0, (int) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:yqqq-qqll-lpqq-wd7a-nw", (int) 0x0, (int) 0x7FFF, (int) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llqq-qpqq-lktn-jq", (int) 0xFFFFFF, (int) 0, (int) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:y7ll-llll-lpqq-9fsw-jv", (int) 0xFFFFFF, (int) 0x7FFF, (int) 0x1 },
		new Object[] { Chain.MAINNET, "tx1:yjk0-uqay-zrfq-g2cg-t8", (int) 0x71F69, (int) 0x89D, (int) 0x123 },
		new Object[] { Chain.MAINNET, "tx1:yjk0-uqay-zu4x-nk6u-pc", (int) 0x71F69, (int) 0x89D, (int) 0x1ABC },

		// testnet with utxoIndex

		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqqq-qqqq-cgru-fa", (int) 0x0, (int) 0x0, (int) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqll-lqqq-zhcp-f3", (int) 0x0, (int) 0x7FFF, (int) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llqq-qqqq-nvd0-gl", (int) 0xFFFFFF, (int) 0x0, (int) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llll-lqqq-fnkj-gn", (int) 0xFFFFFF, (int) 0x7FFF, (int) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqqq-qpqq-622t-s9", (int) 0x0, (int) 0x0, (int) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:8qqq-qqll-lpqq-q43k-sf", (int) 0x0, (int) 0x7FFF, (int) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llqq-qpqq-3wyc-38", (int) 0xFFFFFF, (int) 0x0, (int) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:87ll-llll-lpqq-t3l9-3t", (int) 0xFFFFFF, (int) 0x7FFF, (int) 0x1 },
		new Object[] { Chain.TESTNET, "txtest1:8jk0-uqay-zrfq-xjhr-gq", (int) 0x71F69, (int) 0x89D, (int) 0x123 },
		new Object[] { Chain.TESTNET, "txtest1:8jk0-uqay-zu4x-aw4h-zl", (int) 0x71F69, (int) 0x89D, (int) 0x1ABC },

		new Object[] { Chain.TESTNET, "txtest1:8z35-jznz-qqqq-xstv-nc", (int) 1354001, (int) 83, (int) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8kyt-fzzq-qqqq-ase0-d8", (int) 1201739, (int) 2, (int) 0x0 },
		new Object[] { Chain.TESTNET, "txtest1:8ksa-czpq-qqqq-k85h-97", (int) 1456907, (int) 1, (int) 0x0 }
	};

	public void testTxrefEncode() throws Exception {

		for (Object[] test : tests1) {

			String result;

			if (test.length > 4)			
				result = ChainAndLocationData.txrefEncode((Chain) test[0], (int) test[2], (int) test[3], (int) test[4]);
			else
				result = ChainAndLocationData.txrefEncode((Chain) test[0], (int) test[2], (int) test[3]);

			assertEquals((String) test[1], result);
		}
	}

	public void testTxrefDecode() throws Exception {

		for (Object[] test : tests1) {

			String txref = (String) test[1];

			ChainAndLocationData result = ChainAndLocationData.txrefDecode(txref);
			assertEquals((Chain) test[0], result.getChain());
			assertEquals(test[2], result.getLocationData().getBlockHeight());
			assertEquals(test[3], result.getLocationData().getTransactionPosition());

			if (test.length > 4) assertEquals(test[4], result.getLocationData().getTxoIndex());
		}
	}
}
