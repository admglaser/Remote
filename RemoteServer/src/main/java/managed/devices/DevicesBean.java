package managed.devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import managed.SessionBean;
import model.Client;
import model.User;
import service.ClientService;
import util.Constants;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class DevicesBean {

	@EJB
	private ClientService clientService;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	private List<Client> clients;

	@PostConstruct
	public void init() {
		clients = new ArrayList<>();
		User user = sessionBean.getUser();
		if (user != null) {
			Collection<Client> clients2 = clientService.getClients().getClients(user);
			System.out.println(clients2.size());
			clients.addAll(clients2);
		}
	}
	
	public void connect(Client client) throws IOException {
		sessionBean.setClient(client);
		FacesUtils.redirect(Constants.PAGE_VIEWER);
	}

	public List<Client> getClients() {
		return clients;
	}

	public void setClients(List<Client> clients) {
		this.clients = clients;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
