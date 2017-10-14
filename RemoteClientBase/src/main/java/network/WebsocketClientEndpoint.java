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
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class WebsocketClientEndpoint extends Endpoint implements javax.websocket.MessageHandler.Whole<String> {

    Application application;

    Capturer capturer;

    AccessPresenter accessPresenter;

    ConnectionPresenter connectionPresenter;

    private Session session;
    private MessageHandler messageHandler;

    public void connect(URI endpointURI) {
    	 try {
             ClientManager clientManager = ClientManager.createClient();
             clientManager.connectToServer(this, ClientEndpointConfig.Builder.create().build(), endpointURI);
         } catch (Exception e) {
             connectionPresenter.connected(false);
             connectionPresenter.showMessage("Cannot connect to server.");
         }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        session.addMessageHandler(this);

        this.session = session;
        System.out.println("onOpen: " + session.getId());
        connectionPresenter.connected(true);
        application.showMessage("Connected to server!");

        messageHandler = new MessageHandler(session);
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
        connectionPresenter.connected(false);
        accessPresenter.accountConnected(false);
        accessPresenter.anonymousConnected(false);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("onMessage: " + message);
        messageHandler.handleMessage(message);
    }

    public void disconnect() {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

}