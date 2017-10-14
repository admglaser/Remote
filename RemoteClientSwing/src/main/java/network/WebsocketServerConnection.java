package network;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import application.Application;
import capture.Capturer;
import main.Main;
import model.ImagePiece;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class WebsocketServerConnection implements ServerConnection {

	private WebsocketClientEndpoint clientEndPoint;
	
	@Override
	public void connect(String address) {
		try {
			clientEndPoint = new WebsocketClientEndpointWithInject();
			clientEndPoint.connect(new URI("ws://" + address + ":8080/remote/websocket"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void disconnect() {
		clientEndPoint.disconnect();
	}

	@Override
	public boolean isConnected() {
		return clientEndPoint != null && clientEndPoint.isConnected();
	}

	@Override
	public void connectAnonymous(String id, String password) {
		clientEndPoint.getMessageHandler().sendCreateAnonymousAccess(id, password);
	}

	@Override
	public void disconnectAnonymous() {
		clientEndPoint.getMessageHandler().sendRemoveAnonymousAccess();
	}

	@Override
	public void connectAccount(String username, String password) {
		clientEndPoint.getMessageHandler().sendCreateAccountAccess(username, password);
	}

	@Override
	public void disconnectAccount() {
		clientEndPoint.getMessageHandler().sendRemoveAccountAccess();
	}

	public void sendImagePiece(ImagePiece imagePiece) {
		clientEndPoint.getMessageHandler().sendImagePiece(imagePiece);
	}
	
	public class WebsocketClientEndpointWithInject extends WebsocketClientEndpoint {

		@Inject
		Application application;

		@Inject
		Capturer capturer;

		@Inject
		AccessPresenter accessPresenter;

		@Inject
		ConnectionPresenter connectionPresenter;
		
		public WebsocketClientEndpointWithInject() {
			Main.injector.inject(this);
			super.application = application;
			super.capturer = capturer;
			super.accessPresenter = accessPresenter;
			super.connectionPresenter = connectionPresenter;
		}
		
	}

}
