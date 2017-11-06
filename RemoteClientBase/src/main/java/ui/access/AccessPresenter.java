package ui.access;

import model.ConnectionStatus;
import model.LoginStatus;
import ui.Presenter;
import util.RandomDigitsGenerator;

public class AccessPresenter extends Presenter<AccessScreen, AccessModel> {

	protected static final String CONNECTION_LOST_TO_SERVER = "Connection lost to server.";
	
	protected RandomDigitsGenerator randomDigitsGenerator;
	
	public AccessPresenter() {
		model = new AccessModel();
		randomDigitsGenerator = new RandomDigitsGenerator();
	}

	public void generateAnonymousId() {
		String digits = randomDigitsGenerator.generateDigits(9);
		model.setAnonymousId(digits);
		screen.updateAnonymousId();
	}

	public void generateAnonymousPassword() {
		String digits = randomDigitsGenerator.generateDigits(4);
		model.setAnonymousPassword(digits);
		screen.updateAnonymousPassword();
	}

	public void connectAccount() {
		if (!serverConnection.isConnected()) {
			screen.showMessage(CONNECTION_LOST_TO_SERVER);
			return;
		}
		String username = model.getAccountUsername();
		String password = model.getAccountPassword();
	
		serverConnection.connectAccount(username, password);
	
		model.setAccountLoginStatus(LoginStatus.WAITING);
		screen.updateAccountConnectionStatus();
	}

	public void connectAnonymous() {
		if (!serverConnection.isConnected()) {
			screen.showMessage(CONNECTION_LOST_TO_SERVER);
			return;
		}
		String id = model.getAnonymousId();
		String password = model.getAnonymousPassword();

		serverConnection.connectAnonymous(id, password);

		model.setAnonymousConnectionStatus(ConnectionStatus.WAITING);
		screen.updateAnonymousConnectionStatus();
	}

	public void disconnectAccount() {
		if (!serverConnection.isConnected()) {
			screen.showMessage(CONNECTION_LOST_TO_SERVER);
			return;
		}
		
		serverConnection.disconnectAccount();
		
		model.setAccountLoginStatus(LoginStatus.DISCONNECTED);
		screen.updateAccountConnectionStatus();
	}

	public void disconnectAnonymous() {
		if (!serverConnection.isConnected()) {
			screen.showMessage(CONNECTION_LOST_TO_SERVER);
			return;
		}

		serverConnection.disconnectAnonymous();

		model.setAnonymousConnectionStatus(ConnectionStatus.DISCONNECTED);
		screen.updateAnonymousConnectionStatus();
	}

	public void accountConnected() {
		LoginStatus loginStatus = LoginStatus.CONNECTED;
		model.setAccountLoginStatus(loginStatus);
		screen.updateAccountConnectionStatus();
	}
	
	public void anonymousConnected() {
		ConnectionStatus connectionStatus = ConnectionStatus.CONNECTED;
		model.setAnonymousConnectionStatus(connectionStatus);
		screen.updateAnonymousConnectionStatus();
	}
	
	public void accountDisconnected() {
		LoginStatus loginStatus  = LoginStatus.DISCONNECTED;
		model.setAccountLoginStatus(loginStatus);
		screen.updateAccountConnectionStatus();
	}

	public void anonymousDisconnected() {
		ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
		model.setAnonymousConnectionStatus(connectionStatus);
		screen.updateAnonymousConnectionStatus();
	}

	public void showMessage(String message) {
		screen.showMessage(message);
	}

}
