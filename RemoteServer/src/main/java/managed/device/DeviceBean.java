package managed.device;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import util.Constants;
import util.FacesUtils;

@ManagedBean
@ViewScoped
public class DeviceBean {

	public void connect() throws IOException {
		FacesUtils.redirect(Constants.PAGE_VIEWER);
	}

}
