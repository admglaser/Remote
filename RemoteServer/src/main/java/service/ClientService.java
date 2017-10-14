package service;

import java.util.Collection;
import java.util.Collections;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import model.Client;

@Startup
@Singleton
public class ClientService {

	private Clients clients;
	private ClientPairings clientPairings;

	public ClientService() {
		clients = new Clients();
		clientPairings = new ClientPairings();
	}

	public Collection<Client> removeSender(Client sender) {
		clients.removeClient(sender);
		clientPairings.removeSender(sender);
		return Collections.emptySet();
	}

	public Client removeReceiverAndReturnSenderToStop(Client receiver) {
		clients.removeClient(receiver);
		clientPairings.removeReceiver(receiver);
		Client senderClientToStop = null;
		Client senderClient = clientPairings.getSender(receiver);
		if (clientPairings.getReceivers(senderClient).size() == 0) {
			senderClientToStop = senderClient;
		}
		return senderClientToStop;
	}

	public boolean isSenderCapturing(Client sender) {
		return clientPairings.getReceivers(sender).size() > 0;
	}
	
	public Clients getClients() {
		return clients;
	}
	
	public ClientPairings getClientPairings() {
		return clientPairings;
	}

}
