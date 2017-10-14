package ui.access;

public interface AccessScreen {

	void updateAnonymousId();

	void updateAnonymousPassword();

	void updateAnonymousConnectionStatus();

	void updateAccountConnectionStatus();

	void showMessage(String message);

}
