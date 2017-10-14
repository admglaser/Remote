package service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.Client;

public class ClientPairings {

	private Map<Client, Set<Client>> senderReceiversMap = new HashMap<>();
	private Map<Client, Client> receiverSenderMap = new HashMap<>();

	public void addPairing(Client sender, Client receiver) {
		if (senderReceiversMap.containsKey(sender)) {
			Set<Client> set = senderReceiversMap.get(sender);
			set.add(receiver);
		} else {
			Set<Client> set = new HashSet<>();
			set.add(receiver);
			senderReceiversMap.put(sender, set);
		}
		receiverSenderMap.put(receiver, sender);
	}

	public Client getSender(Client receiver) {
		return receiverSenderMap.get(receiver);
	}
	
	public Collection<Client> getReceivers(Client sender) {
		if (senderReceiversMap.containsKey(sender)) {
			return senderReceiversMap.get(sender);
		} 
		return Collections.emptySet();
	}

	public void removeSender(Client sender) {
		if (senderReceiversMap.containsKey(sender)) {
			Set<Client> receivers = senderReceiversMap.remove(sender);
			for (Client receiver : receivers) {
				receiverSenderMap.remove(receiver);
			}
		}
	}
	
	public void removeReceiver(Client receiver) {
		if (receiverSenderMap.containsKey(receiver)) {
			Client sender = receiverSenderMap.get(receiver);
			Set<Client> receivers = senderReceiversMap.get(sender);
			receivers.remove(receiver);
		}
	}
	
}
