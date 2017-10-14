package application;

import model.json.KeyEvent;
import model.json.MouseClick;

public interface Application {

	void showMessage(String message);
	
	void mouseClick(MouseClick mouseClick);

	void keyEvent(KeyEvent keyEvent);

	String getDeviceName();
	
	int getDeviceWidth();

	int getDeviceHeight();

}
