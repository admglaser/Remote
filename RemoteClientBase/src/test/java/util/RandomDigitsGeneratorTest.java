package util;

import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class RandomDigitsGeneratorTest {

	@Test
	public void twoGeneratedDigitsAreDifferent() {
		String digits1 = new RandomDigitsGenerator().generateDigits(9);
		String digits2 = new RandomDigitsGenerator().generateDigits(9);
		
		assertNotEquals(digits1, digits2);
	}
	
}
