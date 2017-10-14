package ui.connection;

import model.ConnectionStatus;
import ui.Presenter;

public class ConnectionPresenter extends Presenter<ConnectionScreen, ConnectionModel> {

	public ConnectionPresenter() {
		ConnectionModel model = new ConnectionModel();
		attachModel(model);
	}

	public void connect() {
		model.setConnectionStatus(ConnectionStatus.WAITING);
		screen.updateConnectionStatus();
		new Thread(new Runnable() {
			@Override
			public void run() {
				serverConnection.connect(model.getAddress());
			}
		}).start();
	}

	public void disconnect() {
		serverConnection.disconnect();
	}

	public void connected(boolean connected) {
		ConnectionStatus connectionStatus = connected ? ConnectionStatus.CONNECTED : ConnectionStatus.DISCONNECTED;
		model.setConnectionStatus(connectionStatus);
		screen.updateConnectionStatus();
		if (connected) {
			model.setSentMessages(0);
			model.setReceivedMessages(0);
			screen.updateSentMessages();
			screen.updateReceivedMessages();
		} 
	}

	public void showMessage(String message) {
		screen.showMessage(message);
	}
	
	public void messageSent() {
		model.incSentMessages();
		screen.updateSentMessages();
	}
	
	public void messageReceived() {
		model.incReceivedMessages();
		screen.updateReceivedMessages();
	}
	
}
