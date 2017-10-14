package ui.access;

import model.ConnectionStatus;
import model.LoginStatus;
import ui.Presenter;
import util.RandomUtilities;

public class AccessPresenter extends Presenter<AccessScreen, AccessModel> {

	public AccessPresenter() {
		AccessModel model = new AccessModel();
		attachModel(model);
	}

	public void generateAnonymousId() {
		String digits = RandomUtilities.generateDigits(9);
		model.setAnonymousId(digits);
		screen.updateAnonymousId();
	}

	public void generateAnonymousPassword() {
		String digits = RandomUtilities.generateDigits(4);
		model.setAnonymousPassword(digits);
		screen.updateAnonymousPassword();
	}

	public void connectAnonymous() {
		if (!serverConnection.isConnected()) {
			screen.showMessage("Connection lost to server.");
			return;
		}
		String id = model.getAnonymousId();
		String password = model.getAnonymousPassword();

		serverConnection.connectAnonymous(id, password);

		model.setAnonymousConnectionStatus(ConnectionStatus.WAITING);
		screen.updateAnonymousConnectionStatus();
	}

	public void connectAccount() {
		if (!serverConnection.isConnected()) {
			screen.showMessage("Connection lost to server.");
			return;
		}
		String username = model.getAccountUsername();
		String password = model.getAccountPassword();

		serverConnection.connectAccount(username, password);

		model.setAccountLoginStatus(LoginStatus.WAITING);
		screen.updateAccountConnectionStatus();
	}

	public void disconnectAccount() {
		if (!serverConnection.isConnected()) {
			screen.showMessage("Connection lost to server.");
			return;
		}
		
		serverConnection.disconnectAccount();
		
		model.setAccountLoginStatus(LoginStatus.DISCONNECTED);
		screen.updateAccountConnectionStatus();
	}

	public void disconnectAnonymous() {
		if (!serverConnection.isConnected()) {
			screen.showMessage("Connection lost to server.");
			return;
		}

		serverConnection.disconnectAnonymous();

		model.setAnonymousConnectionStatus(ConnectionStatus.DISCONNECTED);
		screen.updateAnonymousConnectionStatus();
	}

	public void anonymousConnected(boolean connected) {
		ConnectionStatus connectionStatus;
		if (connected) {
			connectionStatus = ConnectionStatus.CONNECTED;
		} else {
			connectionStatus = ConnectionStatus.DISCONNECTED;
		}
		model.setAnonymousConnectionStatus(connectionStatus);
		screen.updateAnonymousConnectionStatus();
	}

	public void showMessage(String message) {
		screen.showMessage(message);
	}

	public void accountConnected(boolean connected) {
		LoginStatus loginStatus;
		if (connected) {
			loginStatus = LoginStatus.CONNECTED;
		} else {
			loginStatus = LoginStatus.DISCONNECTED;
		}
		model.setAccountLoginStatus(loginStatus);
		screen.updateAccountConnectionStatus();
	}
	

}
