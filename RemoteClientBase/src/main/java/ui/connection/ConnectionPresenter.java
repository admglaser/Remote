package ui.connection;

import model.ConnectionStatus;
import ui.Presenter;

public class ConnectionPresenter extends Presenter<ConnectionScreen, ConnectionModel> {

	public ConnectionPresenter() {
		model = new ConnectionModel();
	}

	public void connect() {
		model.setConnectionStatus(ConnectionStatus.WAITING);
		screen.updateConnectionStatus();
		serverConnection.connect(model.getAddress());
	}

	public void disconnect() {
		serverConnection.disconnect();
	}

	public void connected() {
		model.setConnectionStatus(ConnectionStatus.CONNECTED);
		model.setSentMessages(0);
		model.setReceivedMessages(0);
		screen.updateConnectionStatus();
		screen.updateSentMessages();
		screen.updateReceivedMessages();
	}
	
	public void disconnected() {
		model.setConnectionStatus(ConnectionStatus.DISCONNECTED);
		screen.updateConnectionStatus();
	}
	
	public void messageSent() {
		model.incSentMessages();
		screen.updateSentMessages();
	}
	
	public void messageReceived() {
		model.incReceivedMessages();
		screen.updateReceivedMessages();
	}
	
	public void showMessage(String message) {
		screen.showMessage(message);
	}
	
}
