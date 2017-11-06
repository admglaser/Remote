package service;

import java.util.Base64;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import entity.Account;
import entity.Account_;
import util.Passwords;

@Stateless
public class AccountService {

	@PersistenceContext
	protected EntityManager em;

	public Account getAccount(String username, String password) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Account> query = builder.createQuery(Account.class);
		Root<Account> userRoot = query.from(Account.class);
	
		query.where(builder.equal(userRoot.get(Account_.username), username.toLowerCase()));
		try {
			Account account = em.createQuery(query).getSingleResult();
			String passwordHash = account.getPasswordHash();
			String passwordSalt = account.getPasswordSalt();
	
			Base64.Decoder decoder = Base64.getDecoder();
	
			boolean result = Passwords.isExpectedPassword(password.toCharArray(), decoder.decode(passwordSalt),
					decoder.decode(passwordHash));
			if (result) {
				return account;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public void registerAccount(String username, String password) throws Exception {
		if (getAccountByUsername(username) != null) {
			throw new Exception("Username already exists.");
		}
		Account user = new Account();
		user.setUsername(username);
		
		byte[] salt = Passwords.getNextSalt();
		byte[] hash = Passwords.hash(password.toCharArray(), salt);
		Base64.Encoder encoder = Base64.getEncoder();
		String saltStr = encoder.encodeToString(salt);
		String hashStr = encoder.encodeToString(hash);
		
		user.setPasswordHash(hashStr);
		user.setPasswordSalt(saltStr);
		em.persist(user);
	}

	public void changePassword(String username, String password, String newPassword) throws Exception {
		Account user = getAccount(username, password);
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

	private Account getAccountByUsername(String username) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Account> query = builder.createQuery(Account.class);
		Root<Account> userRoot = query.from(Account.class);
		query.where(builder.equal(userRoot.get(Account_.username), username.toLowerCase()));
		try {
			Account user = em.createQuery(query).getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
	}

}
