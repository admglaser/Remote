package model;

public enum ConnectionStatus {
	
	DISCONNECTED("Disconnected", "Connect"), 
	WAITING("Connecting..", "Connecting"), 
	CONNECTED("Connected", "Disconnect");
	
	private String status;
	private String action;

	private ConnectionStatus(String status, String action) {
		this.status = status;
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}