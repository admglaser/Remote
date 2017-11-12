package network;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;

import application.Application;
import capture.Capturer;
import model.ImagePiece;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class WebsocketClientEndpoint extends Endpoint
		implements javax.websocket.MessageHandler.Whole<String>, ServerConnection {

	private Application application;

	private Capturer capturer;

	private AccessPresenter accessPresenter;

	private ConnectionPresenter connectionPresenter;

	private Session session;
	private MessageHandler messageHandler;
	private String address;

	private Logger logger = Logger.getLogger(WebsocketClientEndpoint.class);

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
		this.address = address;
		final WebsocketClientEndpoint pointer = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URI endpointURI = new URI("ws://" + address + "/remote/websocket");
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
		logger.info("onOpen: " + session.getId());
		
		connectionPresenter.connected();
		application.showMessage("Connected to server!");
		messageHandler = new MessageHandler(new WebsocketSession(session));
		messageHandler.application = application;
		messageHandler.serverConnection = this;
		messageHandler.capturer = capturer;
		messageHandler.accessPresenter = accessPresenter;
		messageHandler.connectionPresenter = connectionPresenter;
		messageHandler.sendIndentify();
	}

	@Override
	public void onClose(Session userSession, CloseReason reason) {
		logger.info("onClose: " + reason);

		application.showMessage("Disconnected from server!");
		capturer.stopCapture();
		connectionPresenter.disconnected();
		accessPresenter.accountDisconnected();
		accessPresenter.anonymousDisconnected();
	}

	@Override
	public void onMessage(String message) {
		logger.info("onMessage: " + message);

		try {
			messageHandler.handleMessage(message);
		} catch (IOException e) {
			logger.error("Failed to process message: " + e.getMessage());
		} catch (NoCommandException e) {
			logger.info("Failed to get command from message.");
		}
	}

	public void disconnect() {
		try {
			session.close();
		} catch (IOException e) {
			logger.error("Failed to close session: " + e.getMessage());
		}
	}

	public boolean isConnected() {
		return session != null && session.isOpen();
	}

	public void connectAnonymous(String id, String password) {
		if (messageHandler != null) {
			messageHandler.sendCreateAnonymousAccessRequest(id, password);
		}
	}

	public void disconnectAnonymous() {
		if (messageHandler != null) {
			messageHandler.sendRemoveAnonymousAccess();
		}
	}

	public void connectAccount(String username, String password) {
		if (messageHandler != null) {
			messageHandler.sendCreateAccountAccessRequest(username, password);
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

	public String getAddress() {
		return address;
	}

}