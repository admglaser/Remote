package ui;

import network.ServerConnection;

public abstract class Presenter<S, M> {
    
	protected S screen;
	protected M model;
	protected ServerConnection serverConnection;

    public void attachScreen(S screen) {
        this.screen = screen;
    }

    public void attachModel(M model) {
    	this.model = model;
    }
    
    public void attachServerConnection(ServerConnection serverConnection) {
    	this.serverConnection = serverConnection;
    }
    
    public M getModel() {
    	return model;
    }
    
    
}

