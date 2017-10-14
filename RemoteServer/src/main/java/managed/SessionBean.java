package managed;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import model.Client;
import model.User;
import util.Constants;
import util.FacesUtils;

@ManagedBean
@SessionScoped
public class SessionBean {

	private User user;
	private Client client;

	public void logout() throws IOException {
		this.user = null;
		FacesUtils.redirect(Constants.PAGE_INDEX);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
