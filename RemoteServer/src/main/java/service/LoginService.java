package service;

import java.util.Base64;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import model.User;
import model.User_;
import util.Passwords;

@Stateless
public class LoginService {

	@PersistenceContext
	protected EntityManager em;

	public User getUser(String username, String password) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<User> query = builder.createQuery(User.class);
		Root<User> userRoot = query.from(User.class);

		query.where(builder.equal(userRoot.get(User_.username), username.toLowerCase()));
		try {
			User member = em.createQuery(query).getSingleResult();
			String passwordHash = member.getPasswordHash();
			String passwordSalt = member.getPasswordSalt();

			Base64.Decoder decoder = Base64.getDecoder();

			boolean result = Passwords.isExpectedPassword(password.toCharArray(), decoder.decode(passwordSalt),
					decoder.decode(passwordHash));
			if (result) {
				return member;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

}
