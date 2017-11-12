package application;

import java.io.File;
import java.io.IOException;
import java.util.List;

import model.FileInfo;
import model.message.KeyEvent;
import model.message.MouseClick;

public interface Application {

	void showMessage(String message);
	
	void mouseClick(MouseClick mouseClick);

	void keyEvent(KeyEvent keyEvent);

	String getDeviceName();
	
	int getDeviceWidth();

	int getDeviceHeight();

	List<FileInfo> listFileInfos(String path);

	String getParentPath(String path);

	File getFile(String path, String name);

	String uploadFile(String url, File file) throws IOException;

}
