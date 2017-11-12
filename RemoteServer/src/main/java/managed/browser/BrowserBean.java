package managed.browser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import managed.SessionBean;
import model.FileInfo;
import network.MessageHandler;
import service.AccountService;
import service.BrowserService;
import service.ClientService;
import util.FacesUtils;

@ManagedBean
@SessionScoped
public class BrowserBean {

	@ManagedProperty(value = "#{sessionBean}")
	private SessionBean sessionBean;

	@EJB
	ClientService clientService;

	@EJB
	AccountService accountService;

	@EJB
	BrowserService browserService;

	private String path;
	private String parentPath;
	private List<FileInfo> files;
	private UIComponent table;
	
	private String id;
	private boolean responseArrived;
	private String responseDownloadLink;
	private String responseErrorMessage;

	private Logger logger = Logger.getLogger(BrowserBean.class);

	private MessageHandler messageHandler;

	@PostConstruct
	public void init() {
		this.messageHandler = new MessageHandler(clientService, accountService, browserService);
		this.files = new ArrayList<>();

		id = UUID.randomUUID().toString();
		browserService.addBrowserBean(id, this);
		
		try {
			goToHome();
			FacesUtils.reload();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void destruct() {
		browserService.removeBrowserBean(id);
	}
	
	public void goToHome() throws IOException, InterruptedException {
		messageHandler.sendFileListRequest(sessionBean.getClient(), id, null);
		waitForResponse(5000);
	}

	public void goToParent() throws IOException, InterruptedException {
		new MessageHandler(clientService, accountService, browserService).sendFileListRequest(sessionBean.getClient(), id, parentPath);
		waitForResponse(5000);
	}

	public void fileAction(FileInfo fileInfo) throws InterruptedException, IOException {
		if (fileInfo.isDirectory()) {
			messageHandler.sendFileListRequest(sessionBean.getClient(), id, fileInfo.getPath());
			waitForResponse(5000);
		} else {
			messageHandler.sendFileDownloadRequest(sessionBean.getClient(), id, path, fileInfo.getName());
			if (waitForResponse(60000)) {
				if (responseDownloadLink != null) {
					FacesUtils.redirect(responseDownloadLink);
				} else {
					FacesUtils.addMessage(responseErrorMessage, FacesMessage.SEVERITY_ERROR, null);
				}
			} else {
				FacesUtils.addMessage("Unknown error.", FacesMessage.SEVERITY_ERROR, null);
			}
		}
	}

	public String printFileSize(FileInfo fileInfo) {
		long size = fileInfo.getSize();
		if (size == 0) {
			return "";
		}
		if (size < 1024) {
			return size + " B";
		}
		else if (size < 1024*1024) {
			return size/1024 + " K";
		}
		else if (size < 1024*1024*1024) {
			return size/1024/1024 + " MB";
		}
		return size/1024/1024/1024 + " GB";
	}

	public void responseArrived() {
		responseArrived = true;
	}

	public void setResponseDownloadLink(String responseDownloadLink) {
		this.responseDownloadLink = responseDownloadLink;
	}
	
	public void setResponseErrorMessage(String errorMessage) {
		responseErrorMessage = errorMessage;
	}

	private boolean waitForResponse(long timeOutMs) throws IOException, InterruptedException {
		responseArrived = false;
		long time = System.currentTimeMillis();
		while (System.currentTimeMillis() - time < timeOutMs) {
			if (responseArrived) {
				responseArrived = false;
				return true;
			}
			Thread.sleep(100);
		}
		logger.error("Timeout has occured while waiting for response.");
		return false;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public List<FileInfo> getFiles() {
		return files;
	}

	public void setFiles(List<FileInfo> fileInfos) {
		this.files = fileInfos;
	}

	public UIComponent getTable() {
		return table;
	}

	public void setTable(UIComponent table) {
		this.table = table;
	}

}
