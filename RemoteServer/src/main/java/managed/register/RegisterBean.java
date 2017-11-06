package managed.register;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;

import service.AccountService;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class RegisterBean {

	private String username;
	private String password;
	private String passwordRepeat;

	private UIComponent registerButton;

	@EJB
	private AccountService accountService;

	public void register() throws IOException {
		String message = null;
		Severity severity = null;
		try {
			validateFields();
			accountService.registerAccount(username, password);
			message = "Successfully registered.";
			severity = FacesMessage.SEVERITY_INFO;
		} catch (Exception e) {
			message = e.getMessage();
			severity = FacesMessage.SEVERITY_ERROR;
		}
		FacesUtils.addMessage(message, severity, registerButton);
	}

	private void validateFields() throws Exception {
		if (username.isEmpty()) {
			throw new Exception("Username is missing.");
		}
		if (password.isEmpty() || passwordRepeat.isEmpty()) {
			throw new Exception("Password is missing.");
		}
		if (!password.equals(passwordRepeat)) {
			throw new Exception("Passwords do not match.");
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	public UIComponent getRegisterButton() {
		return registerButton;
	}

	public void setRegisterButton(UIComponent registerButton) {
		this.registerButton = registerButton;
	}

}
