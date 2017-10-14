package ui.access;

import model.ConnectionStatus;
import model.LoginStatus;

public class AccessModel {

	private ConnectionStatus anonymousConnectionStatus;
	private String anonymousId;
	private String anonymousPassword;
	private LoginStatus accountLoginStatus;
	private String accountUsername;
	private String accountPassword;

	public AccessModel() {
		anonymousConnectionStatus = ConnectionStatus.DISCONNECTED;
		accountLoginStatus = LoginStatus.DISCONNECTED;
	}

	public String getAnonymousId() {
		return anonymousId;
	}

	public void setAnonymousId(String anonymousId) {
		this.anonymousId = anonymousId;
	}

	public String getAnonymousPassword() {
		return anonymousPassword;
	}

	public void setAnonymousPassword(String anonymousPassword) {
		this.anonymousPassword = anonymousPassword;
	}

	public ConnectionStatus getAnonymousConnectionStatus() {
		return anonymousConnectionStatus;
	}

	public void setAnonymousConnectionStatus(ConnectionStatus connectionStatus) {
		this.anonymousConnectionStatus = connectionStatus;
	}

	public LoginStatus getAccountLoginStatus() {
		return accountLoginStatus;
	}

	public void setAccountLoginStatus(LoginStatus loginStatus) {
		this.accountLoginStatus = loginStatus;
	}

	public String getAccountUsername() {
		return accountUsername;
	}

	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}

	public String getAccountPassword() {
		return accountPassword;
	}

	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}

}
