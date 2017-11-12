package managed.anonymous;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;

import managed.SessionBean;
import model.AnonymousAccess;
import model.Client;
import service.ClientService;
import util.Constants;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class AnonymousBean {

	@EJB
	private ClientService clientService;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	private String id;
	private String password;

	private UIComponent connectButton;

	public void connect() throws IOException {
		String id = this.id.replaceAll(" ", "");
		String password = this.password;

		if (!id.matches("[0-9]{9}")) {
			FacesUtils.addMessage("Id must be 9 characters long and contain numbers only.", FacesMessage.SEVERITY_ERROR, connectButton);
			return;
		}
		if (!password.matches("[0-9]{4}")) {
			FacesUtils.addMessage("Password must be 4 characters long and contain numbers only.", FacesMessage.SEVERITY_ERROR, connectButton);
			return;
		}

		AnonymousAccess anonymousAccess = new AnonymousAccess(id, password);
		Client client = clientService.findClientByAccess(anonymousAccess);
		if (client == null) {
			FacesUtils.addMessage("Invalid id or password.", FacesMessage.SEVERITY_ERROR, connectButton);
			return;
		}

		sessionBean.setAnonymousConnectedClient(client);
		FacesUtils.reload();
	}
	
	public void view() throws IOException {
		sessionBean.setClient(sessionBean.getAnonymousConnectedClient());
		FacesUtils.redirect(Constants.PAGE_VIEWER);
	}
	
	public void browse() throws IOException {
		sessionBean.setClient(sessionBean.getAnonymousConnectedClient());
		FacesUtils.redirect(Constants.PAGE_BROWSER);
	}
	
	public void disconnect() throws IOException {
		sessionBean.setAnonymousConnectedClient(null);
		FacesUtils.reload();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UIComponent getConnectButton() {
		return connectButton;
	}

	public void setConnectButton(UIComponent connectButton) {
		this.connectButton = connectButton;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
