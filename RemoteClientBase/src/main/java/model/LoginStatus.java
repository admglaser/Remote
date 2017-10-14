package model;

public enum LoginStatus {
	
	DISCONNECTED("Not logged in", "Login"), 
	WAITING("Connecting..", "Login"), 
	CONNECTED("Logged in", "Logout");
	
	private String status;
	private String action;

	private LoginStatus(String status, String action) {
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