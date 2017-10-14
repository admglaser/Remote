package managed.viewer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import util.Constants;

@ManagedBean
@ViewScoped
public class ViewerBean {
	
	private String websocketAddress;
	
	public ViewerBean() {
		websocketAddress = Constants.SERVER_ADDRESS + ":8080/remote/websocket";
	}

	public String getWebsocketAddress() {
		return websocketAddress;
	}

	public void setWebsocketAddress(String websocketAddress) {
		this.websocketAddress = websocketAddress;
	}

}
