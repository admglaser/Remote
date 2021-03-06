package managed;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import entity.Account;
import model.Client;
import util.Constants;
import util.FacesUtils;

@ManagedBean
@SessionScoped
public class SessionBean {

	private Account user;
	private Client client;
	private Client anonymousConnectedClient;

	public void logout() throws IOException {
		this.user = null;
		this.client = null;
		FacesUtils.redirect(Constants.PAGE_INDEX);
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getAnonymousConnectedClient() {
		return anonymousConnectedClient;
	}

	public void setAnonymousConnectedClient(Client anonymousConnectedClient) {
		this.anonymousConnectedClient = anonymousConnectedClient;
	}

}
