package network;

import model.ImagePiece;

public interface ServerConnection {
	
	void connect(String address);
	
	void disconnect();
	
	boolean isConnected();
	
	void connectAnonymous(String id, String password);
	
	void disconnectAnonymous();
	
	void connectAccount(String username, String password);
	
	void disconnectAccount();
	
	void sendImagePiece(ImagePiece imagePiece);

}
