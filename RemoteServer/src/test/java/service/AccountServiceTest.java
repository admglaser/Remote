package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import entity.Account;

@RunWith(Arquillian.class)
public class AccountServiceTest {

	@Inject
	AccountService accountService;
	
	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addClass(Account.class)
				.addClass(AccountService.class)
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
	}

	@Test
	public void registerAccountThenChangeItsPassword() throws Exception {
		String username = "user";
		String password = "password123";
		String newPassword = "passwor456";
		
		accountService.registerAccount(username, password);
		
		assertNotEquals(null, accountService.getAccount(username, password));

		accountService.changePassword(username, password, newPassword);
		
		assertEquals(null, accountService.getAccount(username, password));
		assertNotEquals(null, accountService.getAccount(username, newPassword));
	}
	
	@Test(expected=Exception.class)
	public void registerSameAccountTwice() throws Exception {
		accountService.registerAccount("user", "password123");
		accountService.registerAccount("user", "password456");
	}

}
