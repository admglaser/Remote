package model.json;

public class CreateAccountAccessWrapper implements MessageWrapper {

	private CreateAccountAccess createAccountAccess;

	public CreateAccountAccess getCreateAccountAccess() {
		return createAccountAccess;
	}

	public void setCreateAccountAccess(CreateAccountAccess createAccountAccess) {
		this.createAccountAccess = createAccountAccess;
	}
	
}
