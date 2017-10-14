package managed.clients;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import model.Client;
import model.ClientType;
import service.ClientService;

@ManagedBean
@ViewScoped
public class ClientsBean {

	@EJB
	private ClientService clientService;
	
	private List<Client> senders;
	private List<Client> receivers;
	
	@PostConstruct
	public void init() {
		senders = new ArrayList<>();
		receivers = new ArrayList<>();
		for (Client client : clientService.getClients().getClients()) {
			if (client.getType() == ClientType.SENDER) {
				senders.add(client);
			} else if (client.getType() == ClientType.RECEIVER) {
				receivers.add(client);
			}
		}
	}

	public List<Client> getSenders() {
		return senders;
	}

	public void setSenders(List<Client> senders) {
		this.senders = senders;
	}

	public List<Client> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<Client> receivers) {
		this.receivers = receivers;
	}
	
	public String format(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
	
	public List<Client> getReceivers(Client sender) {
		List<Client> list = new ArrayList<>();
		list.addAll(clientService.getClientPairings().getReceivers(sender));
		return list;
	}
	
}
