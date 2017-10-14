package model.json;

public class CreateAnonymousAccess implements Message {

	private String numericId;
	
	private String numericPassword;

	public String getNumericId() {
		return numericId;
	}

	public void setNumericId(String numericId) {
		this.numericId = numericId;
	}

	public String getNumericPassword() {
		return numericPassword;
	}

	public void setNumericPassword(String numericPassword) {
		this.numericPassword = numericPassword;
	}
	
}
