package service;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import managed.browser.BrowserBean;

@Startup
@Singleton
public class BrowserService {

	private Map<String, BrowserBean> browserBeansById;

	public BrowserService() {
		browserBeansById = new HashMap<>();
	}

	public void addBrowserBean(String id, BrowserBean browserBean) {
		browserBeansById.put(id, browserBean);
	}

	public BrowserBean getBrowserBeanById(String id) {
		return browserBeansById.get(id);
	}

	public void removeBrowserBean(String id) {
		browserBeansById.remove(id);
	}

}
