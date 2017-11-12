package model;

import model.message.ConnectRequest;
import model.message.ConnectResponse;
import model.message.CreateAccountAccessRequest;
import model.message.CreateAccountAccessResponse;
import model.message.CreateAnonymousAccessRequest;
import model.message.CreateAnonymousAccessResponse;
import model.message.FileDownloadRequest;
import model.message.FileDownloadResponse;
import model.message.FileListRequest;
import model.message.FileListResponse;
import model.message.Identify;
import model.message.Image;
import model.message.KeyEvent;
import model.message.MouseClick;
import model.message.Notify;
import model.message.RemoveAccountAccess;
import model.message.RemoveAnonymousAccess;
import model.message.Start;
import model.message.Stop;

public class MessageWrapper {

	private ConnectRequest connectRequest;

	private ConnectResponse connectResponse;

	private CreateAccountAccessRequest createAccountAccessRequest;

	private CreateAccountAccessResponse createAccountAccessResponse;

	private CreateAnonymousAccessRequest createAnonymousAccessRequest;

	private CreateAnonymousAccessResponse createAnonymousAccessResponse;

	private FileDownloadRequest fileDownloadRequest;

	private FileDownloadResponse fileDownloadResponse;

	private FileListRequest fileListRequest;
	
	private FileListResponse fileListResponse;

	private Identify identify;

	private Image image;

	private KeyEvent keyEvent;

	private MouseClick mouseClick;

	private Notify notify;

	private RemoveAccountAccess removeAccountAccess;

	private RemoveAnonymousAccess removeAnonymousAccess;

	private Start start;

	private Stop stop;

	public ConnectRequest getConnectRequest() {
		return connectRequest;
	}

	public void setConnectRequest(ConnectRequest connectRequest) {
		this.connectRequest = connectRequest;
	}

	public ConnectResponse getConnectResponse() {
		return connectResponse;
	}

	public void setConnectResponse(ConnectResponse connectResponse) {
		this.connectResponse = connectResponse;
	}

	public CreateAccountAccessRequest getCreateAccountAccessRequest() {
		return createAccountAccessRequest;
	}

	public void setCreateAccountAccessRequest(CreateAccountAccessRequest createAccountAccessRequest) {
		this.createAccountAccessRequest = createAccountAccessRequest;
	}

	public CreateAccountAccessResponse getCreateAccountAccessResponse() {
		return createAccountAccessResponse;
	}

	public void setCreateAccountAccessResponse(CreateAccountAccessResponse createAccountAccessResponse) {
		this.createAccountAccessResponse = createAccountAccessResponse;
	}

	public CreateAnonymousAccessRequest getCreateAnonymousAccessRequest() {
		return createAnonymousAccessRequest;
	}

	public void setCreateAnonymousAccessRequest(CreateAnonymousAccessRequest createAnonymousAccessRequest) {
		this.createAnonymousAccessRequest = createAnonymousAccessRequest;
	}

	public CreateAnonymousAccessResponse getCreateAnonymousAccessResponse() {
		return createAnonymousAccessResponse;
	}

	public void setCreateAnonymousAccessResponse(CreateAnonymousAccessResponse createAnonymousAccessResponse) {
		this.createAnonymousAccessResponse = createAnonymousAccessResponse;
	}

	public FileDownloadRequest getFileDownloadRequest() {
		return fileDownloadRequest;
	}

	public void setFileDownloadRequest(FileDownloadRequest fileDownloadRequest) {
		this.fileDownloadRequest = fileDownloadRequest;
	}

	public FileDownloadResponse getFileDownloadResponse() {
		return fileDownloadResponse;
	}

	public void setFileDownloadResponse(FileDownloadResponse fileDownloadResponse) {
		this.fileDownloadResponse = fileDownloadResponse;
	}

	public FileListRequest getFileListRequest() {
		return fileListRequest;
	}

	public void setFileListRequest(FileListRequest fileListRequest) {
		this.fileListRequest = fileListRequest;
	}

	public FileListResponse getFileListResponse() {
		return fileListResponse;
	}

	public void setFileListResponse(FileListResponse fileListResponse) {
		this.fileListResponse = fileListResponse;
	}

	public Identify getIdentify() {
		return identify;
	}

	public void setIdentify(Identify identify) {
		this.identify = identify;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public KeyEvent getKeyEvent() {
		return keyEvent;
	}

	public void setKeyEvent(KeyEvent keyEvent) {
		this.keyEvent = keyEvent;
	}

	public MouseClick getMouseClick() {
		return mouseClick;
	}

	public void setMouseClick(MouseClick mouseClick) {
		this.mouseClick = mouseClick;
	}

	public Notify getNotify() {
		return notify;
	}

	public void setNotify(Notify notify) {
		this.notify = notify;
	}

	public RemoveAccountAccess getRemoveAccountAccess() {
		return removeAccountAccess;
	}

	public void setRemoveAccountAccess(RemoveAccountAccess removeAccountAccess) {
		this.removeAccountAccess = removeAccountAccess;
	}

	public RemoveAnonymousAccess getRemoveAnonymousAccess() {
		return removeAnonymousAccess;
	}

	public void setRemoveAnonymousAccess(RemoveAnonymousAccess removeAnonymousAccess) {
		this.removeAnonymousAccess = removeAnonymousAccess;
	}

	public Start getStart() {
		return start;
	}

	public void setStart(Start start) {
		this.start = start;
	}

	public Stop getStop() {
		return stop;
	}

	public void setStop(Stop stop) {
		this.stop = stop;
	}

}
