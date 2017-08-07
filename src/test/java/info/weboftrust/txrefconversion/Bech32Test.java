package info.weboftrust.txrefconversion;

import java.util.Arrays;

import info.weboftrust.txrefconversion.Bech32.HrpAndData;
import junit.framework.TestCase;

public class Bech32Test extends TestCase {

	private static Object[][] tests = new Object[][] {
		new Object[] { "A12UEL5L", new byte[] { 97 }, new byte[] { } },	
		new Object[] { "an83characterlonghumanreadablepartthatcontainsthenumber1andtheexcludedcharactersbio1tt5tgs", new byte[] { 97, 110, 56, 51, 99, 104, 97, 114, 97, 99, 116, 101, 114, 108, 111, 110, 103, 104, 117, 109, 97, 110, 114, 101, 97, 100, 97, 98, 108, 101, 112, 97, 114, 116, 116, 104, 97, 116, 99, 111, 110, 116, 97, 105, 110, 115, 116, 104, 101, 110, 117, 109, 98, 101, 114, 49, 97, 110, 100, 116, 104, 101, 101, 120, 99, 108, 117, 100, 101, 100, 99, 104, 97, 114, 97, 99, 116, 101, 114, 115, 98, 105, 111 }, new byte[] { } },	
		new Object[] { "abcdef1qpzry9x8gf2tvdw0s3jn54khce6mua7lmqqqxw", new byte[] { 97, 98, 99, 100, 101, 102 }, new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 } },	
		new Object[] { "11qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqc8247j", new byte[] { 49 }, new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },	
		new Object[] { "split1checkupstagehandshakeupstreamerranterredcaperred2y9e3w", new byte[] { 115, 112, 108, 105, 116 }, new byte[] { 24, 23, 25, 24, 22, 28, 1, 16, 11, 29, 8, 25, 23, 29, 19, 13, 16, 23, 29, 22, 25, 28, 1, 16, 11, 3, 25, 29, 27, 25, 3, 3, 29, 19, 11, 25, 3, 3, 25, 13, 24, 29, 1, 25, 3, 3, 25, 13 } }	
	};

	public void testBech32() throws Exception {

		for (Object[] test : tests) {

			HrpAndData result1 = Bech32.bech32Decode((String) test[0]);
			assert(Arrays.equals((byte[]) test[1], result1.getHrp()));
			assert(Arrays.equals((byte[]) test[2], result1.getData()));

			String result2 = Bech32.bech32Encode((byte[]) test[1], (byte[]) test[2]);
			assertEquals(((String) test[0]).toLowerCase(), result2);
		}
	}
}
