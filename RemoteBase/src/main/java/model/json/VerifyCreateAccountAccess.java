package model.json;

public class VerifyCreateAccountAccess implements Message {
	
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
