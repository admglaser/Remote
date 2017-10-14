package main;

public class Main extends android.app.Application{

	public static ApplicationComponent injector;
	public static Main instance;

	public Main() {
		super();
		instance = this;
		injector = DaggerApplicationComponent.builder().build();
	}

}
