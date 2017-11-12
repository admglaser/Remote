package network;

import javax.inject.Inject;

import application.Application;
import capture.Capturer;
import main.Main;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class WebsocketClientEndpointWithInject extends WebsocketClientEndpoint {
	
	@Inject
	Application application;

	@Inject
	Capturer capturer;

	@Inject
	AccessPresenter accessPresenter;

	@Inject
	ConnectionPresenter connectionPresenter;

	
	@Override
	public void connect(String address) {
		Main.injector.inject(this);
		super.setApplication(application);
		super.setCapturer(capturer);
		super.setAccessPresenter(accessPresenter);
		super.setConnectionPresenter(connectionPresenter);
		super.connect(address);
	}

}
