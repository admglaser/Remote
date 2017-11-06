package network;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import application.Application;
import capture.Capturer;
import model.ImagePiece;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class WebsocketClientEndpoint extends Endpoint implements javax.websocket.MessageHandler.Whole<String>, ServerConnection {

	private Application application;

	private Capturer capturer;

	private AccessPresenter accessPresenter;

	private ConnectionPresenter connectionPresenter;

	private Session session;
	private MessageHandler messageHandler;

	public void setApplication(Application application) {
		this.application = application;
	}

	public void setCapturer(Capturer capturer) {
		this.capturer = capturer;
	}

	public void setAccessPresenter(AccessPresenter accessPresenter) {
		this.accessPresenter = accessPresenter;
	}

	public void setConnectionPresenter(ConnectionPresenter connectionPresenter) {
		this.connectionPresenter = connectionPresenter;
	}

	public void connect(final String address) {
		final WebsocketClientEndpoint pointer = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URI endpointURI = new URI("ws://" + address + ":8080/remote/websocket");
					ClientManager clientManager = ClientManager.createClient();
					clientManager.connectToServer(pointer, ClientEndpointConfig.Builder.create().build(), endpointURI);
				} catch (Exception e) {
					connectionPresenter.disconnected();
					connectionPresenter.showMessage("Cannot connect to server.");
				}
			}
		}).start();
	}

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(this);

		this.session = session;
		System.out.println("onOpen: " + session.getId());
		connectionPresenter.connected();
		application.showMessage("Connected to server!");

		messageHandler = new MessageHandler(new WebsocketSession(session));
		messageHandler.application = application;
		messageHandler.capturer = capturer;
		messageHandler.accessPresenter = accessPresenter;
		messageHandler.connectionPresenter = connectionPresenter;

		messageHandler.sendIndentify();
	}

	@Override
	public void onClose(Session userSession, CloseReason reason) {
		System.out.println("onClose: " + reason);
		application.showMessage("Disconnected from server!");
		capturer.stopCapture();
		connectionPresenter.disconnected();
		accessPresenter.accountDisconnected();
		accessPresenter.anonymousDisconnected();
	}

	@Override
	public void onMessage(String message) {
		System.out.println("onMessage: " + message);
		
		try {
			messageHandler.handleMessage(message);
		} catch (IOException e) {
			System.out.println("Failed to process message: " + e.getMessage());
		} catch (NoCommandException e) {
			System.out.println("Failed to get command from message.");
		}
	}

	public void disconnect() {
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return session != null && session.isOpen();
	}

	public void connectAnonymous(String id, String password) {
		if (messageHandler != null) {
			messageHandler.sendCreateAnonymousAccess(id, password);
		}
	}

	public void disconnectAnonymous() {
		if (messageHandler != null) {
			messageHandler.sendRemoveAnonymousAccess();
		}
	}

	public void connectAccount(String username, String password) {
		if (messageHandler != null) {
			messageHandler.sendCreateAccountAccess(username, password);
		}
	}

	public void disconnectAccount() {
		if (messageHandler != null) {
			messageHandler.sendRemoveAccountAccess();
		}
	}

	public void sendImagePiece(ImagePiece imagePiece) {
		if (messageHandler != null) {
			messageHandler.sendImagePiece(imagePiece);
		}
	}

}