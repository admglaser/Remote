package capture;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CaptureModule {

	@Singleton
	@Provides
	public Capturer provideCapturer() {
		return new FullCapturer();
	}

}
