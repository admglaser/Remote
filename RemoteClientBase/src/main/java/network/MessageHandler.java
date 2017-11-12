package network;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import application.Application;
import capture.Capturer;
import model.FileInfo;
import model.ImagePiece;
import model.MessageWrapper;
import model.Rectangle;
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
import model.message.RemoveAccountAccess;
import model.message.RemoveAnonymousAccess;
import model.message.Start;
import model.message.Stop;
import ui.access.AccessPresenter;
import ui.connection.ConnectionPresenter;

public class MessageHandler {

	public static final String TYPE = "sender";

	Application application;

	ServerConnection serverConnection;
	
	Capturer capturer;

	AccessPresenter accessPresenter;

	ConnectionPresenter connectionPresenter;
	
	protected Session session;

	private Logger logger = Logger.getLogger(MessageHandler.class);
	
	public MessageHandler(Session session) {
		this.session = session;
	}

	public void handleMessage(String message) throws NoCommandException, IOException {
		connectionPresenter.messageReceived();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		MessageWrapper wrapper = mapper.readValue(message, MessageWrapper.class);

		CreateAccountAccessResponse createAccountAccessResponse = wrapper.getCreateAccountAccessResponse();
		if (createAccountAccessResponse != null) {
			handleCreateAccountAccessResponse(createAccountAccessResponse);
			return;
		}

		CreateAnonymousAccessResponse createAnonymousAccessResponse = wrapper.getCreateAnonymousAccessResponse();
		if (createAnonymousAccessResponse != null) {
			handleCreateAnonymousAccessResponse(createAnonymousAccessResponse);
			return;
		}

		Start start = wrapper.getStart();
		if (start != null) {
			handleStart(start);
			return;
		}

		Stop stop = wrapper.getStop();
		if (stop != null) {
			handleStop(stop);
			return;
		}

		MouseClick mouseClick = wrapper.getMouseClick();
		if (mouseClick != null) {
			handleMouseClick(mouseClick);
			return;
		}

		KeyEvent keyEvent = wrapper.getKeyEvent();
		if (keyEvent != null) {
			handleKeyEvent(keyEvent);
			return;
		}
		
		FileListRequest fileListRequest = wrapper.getFileListRequest();
		if (fileListRequest != null) {
			handleFileListRequest(fileListRequest);
			return;
		}
		
		FileDownloadRequest fileDownloadRequest = wrapper.getFileDownloadRequest();
		if (fileDownloadRequest != null) {
			handleFileDownloadRequest(fileDownloadRequest);
			return;
		}

		throw new NoCommandException();
	}

	private void handleCreateAccountAccessResponse(CreateAccountAccessResponse createAccountAccessResponse) {
		boolean success = createAccountAccessResponse.isSuccess();
		if (success) {
			accessPresenter.accountConnected();
		} else {
			accessPresenter.accountDisconnected();
		}
	}

	private void handleCreateAnonymousAccessResponse(CreateAnonymousAccessResponse createAnonymousAccessResponse) {
		boolean success = createAnonymousAccessResponse.isSuccess();
		if (success) {
			accessPresenter.anonymousConnected();
		} else {
			accessPresenter.anonymousDisconnected();
		}
	}

	private void handleStart(Start start) {
		capturer.startCapture();
	}

	private void handleStop(Stop stop) {
		capturer.stopCapture();
	}

	private void handleMouseClick(MouseClick mouseClick) {
		application.mouseClick(mouseClick);
	}

	private void handleKeyEvent(KeyEvent keyEvent) {
		application.keyEvent(keyEvent);
	}

	private void handleFileListRequest(FileListRequest fileListRequest) {
		String parentPath = application.getParentPath(fileListRequest.getPath());
		List<FileInfo> fileInfos = application.listFileInfos(fileListRequest.getPath());
		sendFileListResponse(fileListRequest.getId(), fileListRequest.getPath(), parentPath, fileInfos);
	}
	
	private void handleFileDownloadRequest(FileDownloadRequest fileDownloadRequest) {
		String link = null;
		try {
			String url = "http://" + serverConnection.getAddress() + "/remote/upload/";
			File file = application.getFile(fileDownloadRequest.getPath(), fileDownloadRequest.getName());
			link = application.uploadFile(url, file);
			sendFileDownloadResponse(fileDownloadRequest.getId(), fileDownloadRequest.getPath(), fileDownloadRequest.getName(), link, null);
		} catch (IOException e) {
			logger.error("Failed to upload file: " + e.getMessage());
			sendFileDownloadResponse(fileDownloadRequest.getId(), fileDownloadRequest.getPath(), fileDownloadRequest.getName(), link, e.getMessage());
		}
	}

	public void sendIndentify() {
		Identify identify = new Identify();
		identify.setType(TYPE);
		identify.setDeviceName(application.getDeviceName());
		identify.setDeviceWidth(application.getDeviceWidth());
		identify.setDeviceHeight(application.getDeviceHeight());
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setIdentify(identify);
		send(wrapper);
	}

	public void sendCreateAccountAccessRequest(String username, String password) {
		CreateAccountAccessRequest createAccountAccessRequest = new CreateAccountAccessRequest();
		createAccountAccessRequest.setUsername(username);
		createAccountAccessRequest.setPassword(password);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAccountAccessRequest(createAccountAccessRequest);
		send(wrapper);
	}

	public void sendCreateAnonymousAccessRequest(String numericId, String numericPassword) {
		CreateAnonymousAccessRequest createAnonymousAccess = new CreateAnonymousAccessRequest();
		createAnonymousAccess.setNumericId(numericId);
		createAnonymousAccess.setNumericPassword(numericPassword);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAnonymousAccessRequest(createAnonymousAccess);
		send(wrapper);
	}

	public void sendRemoveAccountAccess() {
		RemoveAccountAccess removeAccountAccess = new RemoveAccountAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAccountAccess(removeAccountAccess);
		send(wrapper);
	}

	public void sendRemoveAnonymousAccess() {
		RemoveAnonymousAccess removeAnonymousAccess = new RemoveAnonymousAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAnonymousAccess(removeAnonymousAccess);
		send(wrapper);

	}

	public void sendImagePiece(ImagePiece imagePiece) {
		Image image = new Image();
		image.setData(imagePiece.getEncodedString());
		Rectangle area = imagePiece.getArea();
		image.setX(area.getX());
		image.setY(area.getY());
		image.setW(area.getWidth());
		image.setH(area.getHeight());
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setImage(image);
		send(wrapper);
	}

	private void sendFileListResponse(String id, String path, String parentPath, List<FileInfo> fileInfos) {
		MessageWrapper wrapper = new MessageWrapper();
		FileListResponse fileListResponse = new FileListResponse();
		fileListResponse.setId(id);
		fileListResponse.setPath(path);
		fileListResponse.setParentPath(parentPath);
		fileListResponse.setFileInfos(fileInfos);
		wrapper.setFileListResponse(fileListResponse);
		send(wrapper);
	}

	private void sendFileDownloadResponse(String id, String path, String name, String link, String errorMessage) {
		MessageWrapper wrapper = new MessageWrapper();
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		fileDownloadResponse.setId(id);
		fileDownloadResponse.setLink(link);
		fileDownloadResponse.setErrorMessage(errorMessage);
		wrapper.setFileDownloadResponse(fileDownloadResponse);
		send(wrapper);
	}

	private void send(MessageWrapper wrapper) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			String string = mapper.writeValueAsString(wrapper);
			session.send(string);
			connectionPresenter.messageSent();
		} catch (Exception e) {
			logger.error("Failed to send message: " + e.getMessage());
		}
	}

}
