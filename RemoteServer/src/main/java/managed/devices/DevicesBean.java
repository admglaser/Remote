package managed.devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import entity.Account;
import managed.SessionBean;
import model.Client;
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
		this.clients = new ArrayList<>();
		Account user = sessionBean.getUser();
		if (user != null) {
			Set<Client> clients = clientService.findClientsByAccount(user);
			this.clients.addAll(clients);
		}
	}
	
	public void connect(Client client) throws IOException {
		sessionBean.setClient(client);
		FacesUtils.redirect(Constants.PAGE_VIEWER);
	}
	
	public void browse(Client client) throws IOException {
		sessionBean.setClient(client);
		FacesUtils.redirect(Constants.PAGE_BROWSER);
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
