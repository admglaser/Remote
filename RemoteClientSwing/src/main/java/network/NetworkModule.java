package network;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NetworkModule {

	@Singleton
	@Provides
	public ServerConnection provideServerConnection() {
		return new WebsocketClientEndpointWithInject();
	}

}
