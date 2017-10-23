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
        super.attachApplication(application);
        super.attachCapturer(capturer);
        super.attachAccessPresenter(accessPresenter);
        super.attachConnectionPresenter(connectionPresenter);
        super.connect(address);
    }

}
