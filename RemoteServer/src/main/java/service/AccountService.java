package service;

import java.util.Base64;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import model.User;
import util.Passwords;

@Stateless
public class AccountService {

	@PersistenceContext
	protected EntityManager em;

	@EJB
	private LoginService loginService;

	public void changePassword(String username, String password, String newPassword) throws Exception {
		User user = loginService.getUser(username, password);
		if (user == null) {
			throw new Exception("Invalid password.");
		}

		byte[] salt = Passwords.getNextSalt();
		byte[] hash = Passwords.hash(newPassword.toCharArray(), salt);
		Base64.Encoder encoder = Base64.getEncoder();
		String saltStr = encoder.encodeToString(salt);
		String hashStr = encoder.encodeToString(hash);

		user.setPasswordHash(hashStr);
		user.setPasswordSalt(saltStr);
		em.merge(user);
	}

}
