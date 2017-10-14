package util;

import java.util.Random;

public class RandomUtilities {

	public static String generateDigits(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= length; i++) {
			int randomInt = getRandomDigit();
			sb.append(randomInt);
		}
		return sb.toString();
	}

	private static int getRandomDigit() {
		int min = 0;
		int max = 9;
		Random random = new Random();
		int randomInt = random.nextInt(max - min + 1) + min;
		return randomInt;
	}

}
