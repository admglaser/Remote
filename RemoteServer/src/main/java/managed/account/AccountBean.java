package managed.account;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;

import managed.SessionBean;
import model.User;
import service.AccountService;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class AccountBean {

	private String currentPassword;
	private String newPassword;
	private String newPasswordRepeat;

	private UIComponent sendButton;

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;
	
	@EJB
	private AccountService accountService;

	public void changePassword() throws IOException {
		String message = null;
		Severity severity = null;
		try {
			User user = sessionBean.getUser();
			if (user == null) {
				throw new Exception("Internal error.");
			}
			validateFields();
			accountService.changePassword(user.getUsername(), currentPassword, newPassword);
			message = "Successfully changed password.";
			severity = FacesMessage.SEVERITY_INFO;
		} catch (Exception e) {
			message = e.getMessage();
			severity = FacesMessage.SEVERITY_ERROR;
		}
		FacesUtils.addMessage(message, severity, sendButton);
	}
	
	private void validateFields() throws Exception {
		if (currentPassword.isEmpty() || newPassword.isEmpty() || newPasswordRepeat.isEmpty()) {
			throw new Exception("A password is missing.");
		}
		if (!newPassword.equals(newPasswordRepeat)) {
			throw new Exception("Passwords do not match.");
		}
	}
	
	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordRepeat() {
		return newPasswordRepeat;
	}

	public void setNewPasswordRepeat(String newPasswordRepeat) {
		this.newPasswordRepeat = newPasswordRepeat;
	}

	public UIComponent getSendButton() {
		return sendButton;
	}

	public void setSendButton(UIComponent sendButton) {
		this.sendButton = sendButton;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}



}
