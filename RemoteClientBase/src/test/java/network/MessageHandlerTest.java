package network;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
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

public class MessageHandlerTest {

	private ObjectMapper mapper;
	
	@Before
	public void setup() {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	@Test(expected=NoCommandException.class)
	public void expectNoCommandException() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		messageHandler.handleMessage(getNoCommandMessage());
	}
	
	@Test(expected=JsonParseException.class)
	public void expectIOException() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		messageHandler.handleMessage(getUnparsableJsonMessage());
	}
	
	@Test
	public void handleCreateAccountAccessResponse() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		boolean success = true;
		
		messageHandler.handleMessage(getCreateAccountAccessResponseMessage(success));

		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.accessPresenter).accountConnected();
	}
	
	@Test
	public void handleCreateAccountAccessResponseFailure() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);

		boolean success = false;
		
		messageHandler.handleMessage(getCreateAccountAccessResponseMessage(success));

		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.accessPresenter).accountDisconnected();
	}

	@Test
	public void handleCreateAnonymousAccessResponse() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);
	
		boolean success = true;
		messageHandler.handleMessage(getCreateAnonymousAccessResponseMessage(success));
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.accessPresenter).anonymousConnected();
	}
	
	@Test
	public void handleCreateAnonymousAccessResponseFailure() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.accessPresenter = mock(AccessPresenter.class);
	
		boolean success = false;
		messageHandler.handleMessage(getCreateAnonymousAccessResponseMessage(success));
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.accessPresenter).anonymousDisconnected();
	}

	@Test
	public void handleStart() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.capturer = mock(Capturer.class);
	
		messageHandler.handleMessage(getStartMessage());
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.capturer).startCapture();
	}

	@Test
	public void handleStop() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.capturer = mock(Capturer.class);
		
		messageHandler.handleMessage(getStopMessage());
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.capturer).stopCapture();
	}

	@Test
	public void handleMouseClick() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		MouseClick mouseClick = new MouseClick();
		mouseClick.setButton(2);
		mouseClick.setX(100);
		mouseClick.setY(100);
		messageHandler.handleMessage(getMouseClickMessage(mouseClick));
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.application).mouseClick(mouseClick);
	}

	@Test
	public void handleKeyEvent() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(Session.class));
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		KeyEvent keyEvent = new KeyEvent();
		keyEvent.setCharacter("a");
		keyEvent.setCode(1);
		keyEvent.setType(KeyEvent.TYPE_KEYDOWN);
		messageHandler.handleMessage(getKeyEventMessage(keyEvent));
	
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.application).keyEvent(keyEvent);
	}

	@Test
	public void handleFileListRequest() throws Exception {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		String id = "id";
		String path = "path";
		String parentPath = "path";
		List<FileInfo> fileInfos = new ArrayList<>();
		FileInfo fileInfo1 = new FileInfo();
		fileInfo1.setDirectory(false);
		fileInfo1.setName("file");
		fileInfo1.setPath(path);
		fileInfo1.setParentPath(parentPath);
		fileInfos.add(fileInfo1);
		FileInfo fileInfo2 = new FileInfo();
		fileInfo2.setDirectory(true);
		fileInfo2.setName("dir");
		fileInfo2.setPath(path);
		fileInfo2.setParentPath(parentPath);
		fileInfos.add(fileInfo2);
		
		when(messageHandler.application.getParentPath(path)).thenReturn(parentPath);
		when(messageHandler.application.listFileInfos(path)).thenReturn(fileInfos);
		
		messageHandler.handleMessage(getFileListRequestMessage(id, path));
		
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(messageHandler.application).getParentPath(path);
		verify(messageHandler.application).listFileInfos(path);
		verify(session).send(getFileListResponse(id, path, parentPath, fileInfos));
	}
	
	@Test
	public void handleFileDownloadRequest() throws Exception {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.serverConnection = mock(ServerConnection.class);
		messageHandler.application = mock(Application.class);
		
		String id = "id";
		String path = "path";
		String name = "name";
		String link = "link";
		String address = "address";
		String url = "http://" + address + "/remote/upload/";
		File file = new File("");
		
		when(messageHandler.serverConnection.getAddress()).thenReturn(address);
		when(messageHandler.application.getFile(path, name)).thenReturn(file);
		when(messageHandler.application.uploadFile(url, file)).thenReturn(link);

		messageHandler.handleMessage(getFileDownloadRequest(id, path, name));
		
		verify(messageHandler.connectionPresenter).messageReceived();
		verify(session).send(getFileDownloadResponse(id, link));
	}

	@Test
	public void sendIdentify() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		messageHandler.application = mock(Application.class);
		
		messageHandler.sendIndentify();
	
		verify(session).send(getIdentifyMessage());
		verify(messageHandler.connectionPresenter).messageSent();
	}

	@Test
	public void sendCreateAccountAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
		
		String username = "username";
		String password = "password";
		messageHandler.sendCreateAccountAccessRequest(username, password);
	
		verify(session).send(getCreateAccountAccessRequestMessage(username, password));
		verify(messageHandler.connectionPresenter).messageSent();
	}

	@Test
	public void sendCreateAnonymousAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
	
		String numericId = "1234";
		String numericPassword = "123 456 789";
		messageHandler.sendCreateAnonymousAccessRequest(numericId, numericPassword);
	
		verify(session).send(getCreateAnonymousAccessMessage(numericId, numericPassword));
		verify(messageHandler.connectionPresenter).messageSent();
	}

	@Test
	public void sendRemoveAccountAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
	
		messageHandler.sendRemoveAccountAccess();
	
		verify(session).send(getRemoveAccountAccessMessage());
		verify(messageHandler.connectionPresenter).messageSent();
	}

	@Test
	public void sendRemoveAnonymousAccess() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
	
		messageHandler.sendRemoveAnonymousAccess();
	
		verify(session).send(getRemoveAnonymousAccessMessage());
		verify(messageHandler.connectionPresenter).messageSent();
	}

	@Test
	public void sendImagePiece() throws JsonProcessingException {
		Session session = mock(Session.class);
		MessageHandler messageHandler = new MessageHandler(session);
		messageHandler.connectionPresenter = mock(ConnectionPresenter.class);
	
		ImagePiece imagePiece = new ImagePiece(new Rectangle(0, 0, 100, 100), "data");
		messageHandler.sendImagePiece(imagePiece);
	
		verify(session).send(getImageMessage(imagePiece));
		verify(messageHandler.connectionPresenter).messageSent();
	}

	private String getNoCommandMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		return mapper.writeValueAsString(wrapper);
	}

	private String getUnparsableJsonMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		return mapper.writeValueAsString(wrapper).replace("{", "");
	}

	private String getCreateAccountAccessResponseMessage(boolean success) throws JsonProcessingException {
		CreateAccountAccessResponse createAccountAccessResponse = new CreateAccountAccessResponse();
		createAccountAccessResponse.setSuccess(success);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAccountAccessResponse(createAccountAccessResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAnonymousAccessResponseMessage(boolean sucess) throws JsonProcessingException {
		CreateAnonymousAccessResponse createAnonymousAccessResponse = new CreateAnonymousAccessResponse();
		createAnonymousAccessResponse.setSuccess(sucess);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAnonymousAccessResponse(createAnonymousAccessResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getStartMessage() throws JsonProcessingException {
		Start start = new Start();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStart(start);
		return mapper.writeValueAsString(wrapper);
	}

	private String getStopMessage() throws JsonProcessingException {
		Stop stop = new Stop();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStop(stop);
		return mapper.writeValueAsString(wrapper);
	}

	private String getMouseClickMessage(MouseClick mouseClick) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMouseClick(mouseClick);
		return mapper.writeValueAsString(wrapper);
	}

	private String getKeyEventMessage(KeyEvent keyEvent) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setKeyEvent(keyEvent);
		return mapper.writeValueAsString(wrapper);
	}

	private String getIdentifyMessage() throws JsonProcessingException {
		Identify identify = new Identify();
		identify.setType("sender");
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setIdentify(identify);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAccountAccessRequestMessage(String username, String password) throws JsonProcessingException {
		CreateAccountAccessRequest createAccountAccessRequest = new CreateAccountAccessRequest();
		createAccountAccessRequest.setUsername(username);
		createAccountAccessRequest.setPassword(password);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAccountAccessRequest(createAccountAccessRequest);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAnonymousAccessMessage(String numericId, String numericPassword) throws JsonProcessingException {
		CreateAnonymousAccessRequest createAnonymousAccess = new CreateAnonymousAccessRequest();
		createAnonymousAccess.setNumericId(numericId);
		createAnonymousAccess.setNumericPassword(numericPassword);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAnonymousAccessRequest(createAnonymousAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getRemoveAccountAccessMessage() throws JsonProcessingException {
		RemoveAccountAccess removeAccountAccess = new RemoveAccountAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAccountAccess(removeAccountAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getRemoveAnonymousAccessMessage() throws JsonProcessingException {
		RemoveAnonymousAccess removeAnonymousAccess = new RemoveAnonymousAccess();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAnonymousAccess(removeAnonymousAccess);
		return mapper.writeValueAsString(wrapper);
	}

	private String getImageMessage(ImagePiece imagePiece) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		Image image = new Image();
		image.setData(imagePiece.getEncodedString());
		Rectangle area = imagePiece.getArea();
		image.setX(area.getX());
		image.setY(area.getY());
		image.setW(area.getWidth());
		image.setH(area.getHeight());
		wrapper.setImage(image);
		return mapper.writeValueAsString(wrapper);
	}

	private String getFileListRequestMessage(String id, String path) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		FileListRequest fileListRequest = new FileListRequest();
		fileListRequest.setId(id);
		fileListRequest.setPath(path);
		wrapper.setFileListRequest(fileListRequest);
		return mapper.writeValueAsString(wrapper);
	}

	private String getFileListResponse(String id, String path, String parentPath, List<FileInfo> fileInfos) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		FileListResponse fileListResponse = new FileListResponse();
		fileListResponse.setId(id);
		fileListResponse.setPath(path);
		fileListResponse.setParentPath(parentPath);
		fileListResponse.setFileInfos(fileInfos);
		wrapper.setFileListResponse(fileListResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getFileDownloadResponse(String id, String link) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
		fileDownloadResponse.setId(id);
		fileDownloadResponse.setLink(link);
		wrapper.setFileDownloadResponse(fileDownloadResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getFileDownloadRequest(String id, String path, String name) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();
		fileDownloadRequest.setId(id);
		fileDownloadRequest.setPath(path);
		fileDownloadRequest.setName(name);
		wrapper.setFileDownloadRequest(fileDownloadRequest);
		return mapper.writeValueAsString(wrapper);
	}
	

}
