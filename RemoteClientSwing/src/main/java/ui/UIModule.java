package ui;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

@Module
public class UIModule {

	@Singleton
	@Provides
	public AccessPresenter provideAccessPresenter() {
		return new AccessPresenter();
	}

	@Singleton
	@Provides
	public ConnectionPresenter provideConnectionPresenter() {
		return new ConnectionPresenter();
	}

}
