package ui.connection;

import model.ConnectionStatus;

public class ConnectionModel {

	private String address;
	private ConnectionStatus connectionStatus;
	private int sentMessages;
	private int receivedMessages;

	public ConnectionModel() {
		connectionStatus = ConnectionStatus.DISCONNECTED;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public ConnectionStatus getConnectionStatus() {
		return connectionStatus;
	}

	public void setConnectionStatus(ConnectionStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public int getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(int sentMessages) {
		this.sentMessages = sentMessages;
	}

	public int getReceivedMessages() {
		return receivedMessages;
	}

	public void setReceivedMessages(int receivedMessages) {
		this.receivedMessages = receivedMessages;
	}

	public void incSentMessages() {
		this.sentMessages++;
	}

	public void incReceivedMessages() {
		this.receivedMessages++;
	}
	
}
