package service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Set;

import javax.inject.Inject;
import javax.websocket.Session;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import entity.Account;
import model.AnonymousAccess;
import model.Client;
import model.ClientType;

@RunWith(Arquillian.class)
public class ClientServiceTest {

	@Inject
	ClientService clientService;

	@Deployment
	public static Archive<?> createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addClass(ClientService.class);
	}
	
	@Before
	public void setup() {
		clientService.getClients().clear();
	}

	@Test
	public void findClientById() {
		Client client1 = new Client("id1", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		Client client2 = new Client("id2", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		clientService.getClients().add(client1);
		clientService.getClients().add(client2);

		assertEquals(client1, clientService.findClientById(client1.getId()));
		assertEquals(client2, clientService.findClientById(client2.getId()));
		assertEquals(null, clientService.findClientById("id3"));
	}

	@Test
	public void findClientBySession() {
		Client client1 = new Client("id1", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		Client client2 = new Client("id2", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		clientService.getClients().add(client1);
		clientService.getClients().add(client2);

		assertEquals(client1, clientService.findClientBySession(client1.getSession()));
		assertEquals(client2, clientService.findClientBySession(client2.getSession()));
		assertEquals(null, clientService.findClientBySession(mock(Session.class)));
	}

	@Test
	public void findClientByAccount() {
		Client client1 = new Client("id1", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		Client client2 = new Client("id2", "device2", 0, 0, ClientType.SENDER, mock(Session.class));
		Account account1 = new Account();
		client1.setAccount(account1);
		client2.setAccount(account1);
		clientService.getClients().add(client1);
		clientService.getClients().add(client2);

		Set<Client> clientsByAccount = clientService.findClientsByAccount(account1);
		assertEquals(2, clientsByAccount.size());
		assertEquals(true, clientsByAccount.contains(client1));
		assertEquals(true, clientsByAccount.contains(client2));
	}

	@Test
	public void findClientByAccess() {
		Client client = new Client("id", "device", 0, 0, ClientType.SENDER, mock(Session.class));
		AnonymousAccess access1 = new AnonymousAccess("id1", "password1");
		AnonymousAccess access2 = new AnonymousAccess("id2", "password2");
		client.setAnonymousAccess(access1);
		clientService.getClients().add(client);

		assertEquals(client, clientService.findClientByAccess(access1));
		assertEquals(null, clientService.findClientByAccess(access2));
	}

	@Test
	public void isAccessUnique() {
		Client client = new Client("id", "device", 0, 0, ClientType.SENDER, mock(Session.class));
		AnonymousAccess access1 = new AnonymousAccess("id1", "password1");
		AnonymousAccess access2 = new AnonymousAccess("id1", "password1");
		AnonymousAccess access3 = new AnonymousAccess("id3", "password3");
		client.setAnonymousAccess(access1);
		clientService.getClients().add(client);

		clientService.isAccessUnique(access2);

		assertEquals(false, clientService.isAccessUnique(access2));
		assertEquals(true, clientService.isAccessUnique(access3));
	}

	@Test
	public void removeSenderAndReturnReceiversToNotify() {
		Client sender = new Client("id1", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		Client receiver1 = new Client("id2", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client receiver2 = new Client("id3", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		sender.getReceivers().add(receiver1);
		sender.getReceivers().add(receiver2);
		receiver1.setSender(sender);
		receiver2.setSender(sender);
		clientService.getClients().add(sender);
		clientService.getClients().add(receiver1);
		clientService.getClients().add(receiver2);

		Set<Client> receiversToNotify = clientService.removeSenderAndReturnReceiversToNotify(sender);
		Set<Client> clients = clientService.getClients();

		assertEquals(false, clients.contains(sender));
		assertEquals(2, receiversToNotify.size());
		assertEquals(true, receiversToNotify.contains(receiver1));
		assertEquals(true, receiversToNotify.contains(receiver2));
	}

	@Test
	public void removeReceiverAndReturnSenderToStopIfLastReceiver() {
		Client sender1 = new Client("id1", "device1", 0, 0, ClientType.SENDER, mock(Session.class));
		Client receiver1 = new Client("id2", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		sender1.getReceivers().add(receiver1);
		receiver1.setSender(sender1);
		
		Client sender2 = new Client("id3", "device2", 0, 0, ClientType.SENDER, mock(Session.class));
		Client receiver2 = new Client("id4", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client receiver3 = new Client("id5", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		sender2.getReceivers().add(receiver2);
		sender2.getReceivers().add(receiver3);
		receiver2.setSender(sender2);
		receiver3.setSender(sender2);
		
		clientService.getClients().add(sender1);
		clientService.getClients().add(receiver1);
		clientService.getClients().add(sender2);
		clientService.getClients().add(receiver2);
		clientService.getClients().add(receiver3);

		Client senderToStop1 = clientService.removeReceiverAndReturnSenderToStopIfLastReceiver(receiver1);
		Client senderToStop2 = clientService.removeReceiverAndReturnSenderToStopIfLastReceiver(receiver2);
		Set<Client> clients = clientService.getClients();

		assertEquals(false, clients.contains(receiver1));
		assertEquals(false, clients.contains(receiver2));
		assertEquals(sender1, senderToStop1);
		assertEquals(null, senderToStop2);
		assertEquals(false, sender2.getReceivers().contains(receiver2));
	}

}
