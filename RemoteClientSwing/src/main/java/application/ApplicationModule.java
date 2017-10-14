package application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

	@Singleton
	@Provides
	public Application provideFrame() {
		return new SwingApplication();
	}

}
