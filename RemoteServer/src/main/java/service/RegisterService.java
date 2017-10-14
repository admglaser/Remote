package service;

import java.util.Base64;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import model.User;
import model.User_;
import util.Passwords;

@Stateless
public class RegisterService {

	@PersistenceContext
	protected EntityManager em;
	
	public void register(String username, String password) throws Exception {
		if (userExists(username)) {
			throw new Exception("Username already exists.");
		}
		User user = new User();
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
	
	private boolean userExists(String username) {
		User user = getUser(username);
		return user != null;
	}

	private User getUser(String username) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> userRoot = query.from(User.class);
		query.where(builder.equal(userRoot.get(User_.username), username.toLowerCase()));
		try {
			User user = em.createQuery(query).getSingleResult();
			return user;
		} catch (NoResultException e) {
			return null;
		}
	}
	
}
