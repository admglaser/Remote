package service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import model.AnonymousAccess;
import model.Client;
import model.User;

public class Clients {

	private Map<Session, Client> sessionClientMap;
	private Map<String, Client> idClientMap;

	private Map<AnonymousAccess, Client> anonymousAccessClientMap;
	private Map<Client, AnonymousAccess> clientAnonymousAccessMap;

	private Map<User, Set<Client>> userClientsMap;
	private Map<Client, User> clientUserMap;

	public Clients() {
		sessionClientMap = new HashMap<>();
		idClientMap = new HashMap<>();

		anonymousAccessClientMap = new HashMap<>();
		clientAnonymousAccessMap = new HashMap<>();

		userClientsMap = new HashMap<User, Set<Client>>();
		clientUserMap = new HashMap<>();
	}

	public void addClient(Client client) {
		sessionClientMap.put(client.getSession(), client);
		idClientMap.put(client.getId(), client);
	}

	public void removeClient(Client client) {
		sessionClientMap.remove(client.getSession());
		idClientMap.remove(client.getId());
		removeAnonymousAccess(client);
		removeAccountAccess(client);
	}

	public void addAnonymousAccess(Client client, AnonymousAccess anonymousAccess) {
		anonymousAccessClientMap.put(anonymousAccess, client);
		clientAnonymousAccessMap.put(client, anonymousAccess);
	}

	public void removeAnonymousAccess(Client client) {
		if (clientAnonymousAccessMap.containsKey(client)) {
			AnonymousAccess anonymousAccess = clientAnonymousAccessMap.remove(client);
			anonymousAccessClientMap.remove(anonymousAccess);
		}
	}

	public void addAccountAccess(Client client, User user) {
		if (userClientsMap.containsKey(user)) {
			Set<Client> clients = userClientsMap.get(user);
			clients.add(client);
		} else {
			Set<Client> clients = new HashSet<>();
			clients.add(client);
			userClientsMap.put(user, clients);
		}
		clientUserMap.put(client, user);
	}

	public void removeAccountAccess(Client client) {
		if (clientUserMap.containsKey(client)) {
			User user = clientUserMap.remove(client);
			userClientsMap.get(user).remove(client);
		}
	}

	public boolean hasClient(Client client) {
		return sessionClientMap.containsKey(client.getSession());
	}

	public boolean isAnonymousAccessUnique(AnonymousAccess anonymousAccess) {
		return !anonymousAccessClientMap.containsKey(anonymousAccess);
	}

	public Collection<Client> getClients() {
		return sessionClientMap.values();
	}

	public Client getClient(Session session) {
		return sessionClientMap.get(session);
	}

	public Collection<Client> getClients(User user) {
		if (userClientsMap.containsKey(user)) {
			return userClientsMap.get(user);
		}
		return Collections.emptySet();
	}

	public Client getClient(AnonymousAccess anonymousAccess) {
		return anonymousAccessClientMap.get(anonymousAccess);
	}

	public Client getClient(String id) {
		return idClientMap.get(id);
	}
	
}
