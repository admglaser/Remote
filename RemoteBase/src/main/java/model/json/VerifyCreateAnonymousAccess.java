package model.json;

public class VerifyCreateAnonymousAccess implements Message {
	
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
