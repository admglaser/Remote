package ui.connection;

public interface ConnectionScreen {

	void showMessage(String message);

	void updateConnectionStatus();
	
	void updateSentMessages();
	
	void updateReceivedMessages();

}
