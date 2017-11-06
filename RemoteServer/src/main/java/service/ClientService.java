package service;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.Session;

import model.AnonymousAccess;
import model.Client;
import model.User;

@Startup
@Singleton
public class ClientService {

	private Set<Client> clients;

	public ClientService() {
		clients = new HashSet<>();
	}
	
	public Set<Client> getClients() {
		return clients;
	}
	
	public Client findClientById(String id) {
		for (Client client : clients) {
			if (id.equals(client.getId())) {
				return client;
			}
		}
		return null;
	}

	public Client findClientBySession(Session session) {
		for (Client client : clients) {
			if (session.equals(client.getSession())) {
				return client;
			}
		}
		return null;
	}

	public Set<Client> findClientsByUser(User user) {
		Set<Client> clients = new HashSet<>();
		for (Client client : this.clients) {
			if (user.equals(client.getUser())) {
				clients.add(client);
			}
		}
		return clients;
	}

	public Client findClientByAccess(AnonymousAccess anonymousAccess) {
		for (Client client : clients) {
			if (anonymousAccess.equals(client.getAnonymousAccess())) {
				return client;
			}
		}
		return null;
	}

	public boolean isAccessUnique(AnonymousAccess anonymousAccess) {
		for (Client client : clients) {
			if (anonymousAccess.equals(client.getAnonymousAccess())) {
				return false;
			}
		}
		return true;
	}

	public Set<Client> removeSenderAndReturnReceiversToNotify(Client sender) {
		clients.remove(sender);
		Set<Client> receivers = sender.getReceivers();
		for (Client receiver : receivers) {
			receiver.setSender(null);
		}
		return receivers;
	}

	public Client removeReceiverAndReturnSenderToStopIfLastReceiver(Client receiver) {
		clients.remove(receiver);
		receiver.getSender().getReceivers().remove(receiver);
		
		if (receiver.getSender().getReceivers().size() == 0) {
			return receiver.getSender();
		} else {
			return null;
		}
	}

}
