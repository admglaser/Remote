package managed.connect;

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
public class ConnectBean {

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

		if (!id.matches(Constants.NUMERIC_ID_REGEXP)) {
			FacesUtils.addMessage("Id must be 9 characters long and contain numbers only.", FacesMessage.SEVERITY_ERROR, connectButton);
			return;
		}
		if (!password.matches(Constants.NUMERIC_PASS_REGEXP)) {
			FacesUtils.addMessage("Password must be 4 characters long and contain numbers only.", FacesMessage.SEVERITY_ERROR, connectButton);
			return;
		}

		AnonymousAccess anonymousAccess = new AnonymousAccess(id, password);
		Client client = clientService.findClientByAccess(anonymousAccess);
		if (client == null) {
			FacesUtils.addMessage("Invalid id or password.", FacesMessage.SEVERITY_ERROR, connectButton);
			return;
		}

		sessionBean.setClient(client);
		FacesUtils.redirect(Constants.PAGE_DEVICE);
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
