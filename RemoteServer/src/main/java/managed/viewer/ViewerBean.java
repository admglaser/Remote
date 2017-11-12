package managed.viewer;

import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import managed.SessionBean;
import util.Constants;

@ManagedBean
@SessionScoped
public class ViewerBean {
	
	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;
	
	private String websocketAddress;
	
	public ViewerBean() {
		websocketAddress = Constants.SERVER_ADDRESS + "/remote/websocket";
	}
	
	@PreDestroy
	public void destruct() {
		sessionBean.setClient(null);
	}

	public String getWebsocketAddress() {
		return websocketAddress;
	}

	public void setWebsocketAddress(String websocketAddress) {
		this.websocketAddress = websocketAddress;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}
