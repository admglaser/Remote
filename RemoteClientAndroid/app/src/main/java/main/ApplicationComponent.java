package main;

import javax.inject.Singleton;

import application.ApplicationModule;
import capture.CaptureModule;
import capture.FullCapturer;
import dagger.Component;
import network.NetworkModule;
import ui.UIModule;
import ui.access.AccessFragment;
import ui.connection.ConnectionFragment;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        CaptureModule.class,
        UIModule.class,
        NetworkModule.class
})
public interface ApplicationComponent {

    void inject(FullCapturer fullCapturer);

    void inject(network.WebsocketServerConnection.WebsocketClientEndpointWithInject websocketClientEndpointWithInject);

    void inject(ConnectionFragment connectionFragment);

    void inject(AccessFragment accessFragment);

}
