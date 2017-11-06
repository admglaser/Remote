package ui;

import network.ServerConnection;

public abstract class Presenter<S, M> {
    
	protected S screen;
	protected M model;
	protected ServerConnection serverConnection;

    public void setScreen(S screen) {
        this.screen = screen;
    }

    public void setModel(M model) {
    	this.model = model;
    }
    
    public void setServerConnection(ServerConnection serverConnection) {
    	this.serverConnection = serverConnection;
    }
    
    public S getScreen() {
    	return screen;
    }
    
    public M getModel() {
    	return model;
    }

	public ServerConnection getServerConnection() {
		return serverConnection;
	}

}

