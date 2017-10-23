package main;

import javax.inject.Singleton;

import application.ApplicationModule;
import capture.AdaptiveCapturer;
import capture.CaptureModule;
import dagger.Component;
import network.NetworkModule;
import network.WebsocketClientEndpointWithInject;
import ui.UIModule;
import ui.access.AccessPanel;
import ui.connection.ConnectionPanel;

@Singleton
@Component(modules = { ApplicationModule.class, CaptureModule.class, UIModule.class, NetworkModule.class })
public interface ApplicationComponent {

	void inject(Main main);

	void inject(AdaptiveCapturer capturer);

	void inject(ConnectionPanel connectionScreen);

	void inject(AccessPanel accessScreen);

	void inject(WebsocketClientEndpointWithInject websocketServerConnection);

}
