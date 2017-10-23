package application;

import model.message.KeyEvent;
import model.message.MouseClick;

public interface Application {

	void showMessage(String message);
	
	void mouseClick(MouseClick mouseClick);

	void keyEvent(KeyEvent keyEvent);

	String getDeviceName();
	
	int getDeviceWidth();

	int getDeviceHeight();

}
