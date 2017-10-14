package main;

import javax.inject.Inject;

import application.Application;

public class Main {
	
	@Inject
	Application application;
	
	public static ApplicationComponent injector;
 
	public Main() {
		injector = DaggerApplicationComponent.builder().build();
		injector.inject(this);
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
}
