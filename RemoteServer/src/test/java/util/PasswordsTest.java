package util;

import java.util.Base64;

import org.junit.Assert;
import org.junit.Test;

public class PasswordsTest {

	@Test
	public void generateTwoDifferentRandomPasswords() {
		String password1 = Passwords.generateRandomPassword(10);
		String password2 = Passwords.generateRandomPassword(10);
		Assert.assertNotEquals(password1, password2);
	}
	
	@Test
	public void generateRandomPasswordThenSaltAndHashIt() {
		String password = Passwords.generateRandomPassword(10);

		byte[] salt = Passwords.getNextSalt();
		byte[] hash = Passwords.hash(password.toCharArray(), salt);
		Base64.Encoder encoder = Base64.getEncoder();
		String saltStr = encoder.encodeToString(salt);
		String hashStr = encoder.encodeToString(hash);
		
		Base64.Decoder decoder = Base64.getDecoder();
		Assert.assertTrue(Passwords.isExpectedPassword(password.toCharArray(), decoder.decode(saltStr), decoder.decode(hashStr)));
		
		String newGeneratedPassword = Passwords.generateRandomPassword(10);
		Assert.assertFalse(Passwords.isExpectedPassword(newGeneratedPassword.toCharArray(), decoder.decode(saltStr), decoder.decode(hashStr)));
	}
	
}
