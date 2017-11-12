package network;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import javax.websocket.Session;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import entity.Account;
import managed.browser.BrowserBean;
import model.AnonymousAccess;
import model.Client;
import model.ClientType;
import model.MessageWrapper;
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
import service.AccountService;
import service.BrowserService;
import service.ClientService;

public class MessageHandler {

	protected ClientService clientService;
	protected AccountService accountService;
	protected BrowserService browserService;
	
	protected Client client;
	protected Session session;
	
	private Logger logger  = Logger.getLogger(MessageHandler.class);

	public MessageHandler(ClientService clientService, AccountService accountService, BrowserService browserService) {
		this.clientService = clientService;
		this.accountService = accountService;
		this.browserService = browserService;
	}

	public void handleMessage(String message, Session session) throws NoCommandException, IOException {
		this.session = session;
		this.client = clientService.findClientBySession(session);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		MessageWrapper wrapper = mapper.readValue(message, MessageWrapper.class);
		
		Image image = wrapper.getImage();
		if (image != null) {
			handleImage(image);
			return;
		}

		logger.info("message: " + message);
		
		ConnectRequest connectRequest = wrapper.getConnectRequest();
		if (connectRequest != null) {
			handleConnectRequest(connectRequest);
			return;
		}

		Identify identify = wrapper.getIdentify();
		if (identify != null) {
			handleIdentify(identify);
			return;
		}

		CreateAccountAccessRequest createAccountAccessRequest = wrapper.getCreateAccountAccessRequest();
		if (createAccountAccessRequest != null) {
			handleCreateAccountAccessRequest(createAccountAccessRequest);
			return;
		}

		CreateAnonymousAccessRequest createAnonymousAccessRequest = wrapper.getCreateAnonymousAccessRequest();
		if (createAnonymousAccessRequest != null) {
			handleCreateAnonymousAccess(createAnonymousAccessRequest);
			return;
		}

		RemoveAccountAccess removeAccountAccess = wrapper.getRemoveAccountAccess();
		if (removeAccountAccess != null) {
			handleRemoveAccountAccess(removeAccountAccess);
			return;
		}

		RemoveAnonymousAccess removeAnonymousAccess = wrapper.getRemoveAnonymousAccess();
		if (removeAnonymousAccess != null) {
			handleRemoveAnonymousAccess(removeAnonymousAccess);
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

		FileListResponse fileListResponse = wrapper.getFileListResponse();
		if (fileListResponse != null) {
			handleFileListResponse(fileListResponse);
			return;
		}
		
		FileDownloadResponse fileDownloadResponse = wrapper.getFileDownloadResponse();
		if (fileDownloadResponse != null) {
			handleFileDownloadResponse(fileDownloadResponse);
			return;
		}
		
		throw new NoCommandException();
	}

	private void handleIdentify(Identify identify) {
		String id = UUID.randomUUID().toString();
		String deviceName = identify.getDeviceName();
		int deviceWidth = identify.getDeviceWidth();
		int deviceHeight = identify.getDeviceHeight();
		String type = identify.getType();
		client = new Client(id, deviceName, deviceWidth, deviceHeight, ClientType.fromString(type), session);
		clientService.getClients().add(client);
	}

	private void handleCreateAccountAccessRequest(CreateAccountAccessRequest createAccountAccessRequest) {
		String username = createAccountAccessRequest.getUsername();
		String password = createAccountAccessRequest.getPassword();
	
		Account user = accountService.getAccount(username, password);
		if (user == null) {
			sendCreateAccountAccessResponse(client, false);
		} else {
			sendCreateAccountAccessResponse(client, true);
			client.setAccount(user);
		}
	}

	private void handleCreateAnonymousAccess(CreateAnonymousAccessRequest createAnonymousAccessRequest) {
		String numericId = createAnonymousAccessRequest.getNumericId();
		String numericPassword = createAnonymousAccessRequest.getNumericPassword();
		AnonymousAccess anonymousAccess = new AnonymousAccess(numericId, numericPassword);
		if (clientService.isAccessUnique(anonymousAccess)) {
			sendVerifyCreateAnonymousAccess(client, true);
			client.setAnonymousAccess(anonymousAccess);
		} else {
			sendVerifyCreateAnonymousAccess(client, false);
		}
	}

	private void handleRemoveAccountAccess(RemoveAccountAccess removeAccountAccess) {
		client.setAccount(null);
	}

	private void handleRemoveAnonymousAccess(RemoveAnonymousAccess removeAnonymousAccess) {
		client.setAnonymousAccess(null);
	}

	private void handleConnectRequest(ConnectRequest connectRequest) {
		String senderId = connectRequest.getId();
		Client senderClient = clientService.findClientById(senderId);
		boolean sendStartToSender = false;
		if (senderClient.getReceivers().size() == 0) {
			sendStartToSender = true;
		}
		client.setSender(senderClient);
		senderClient.getReceivers().add(client);
		if (sendStartToSender) {
			sendStart(senderClient);
		}
		sendConnectResponse(client);
	}

	private void handleImage(Image image) {
		for (Client receiver : client.getReceivers()) {
			sendImage(receiver, image);
		}
	}

	private void handleMouseClick(MouseClick mouseClick) {
		Client sender = client.getSender();
		sendMouseClick(sender, mouseClick);
	}
	
	private void handleKeyEvent(KeyEvent keyEvent) {
		Client sender = client.getSender();
		sendKeyEvent(sender, keyEvent);
	}

	private void handleFileListResponse(FileListResponse fileListResponse) {
		BrowserBean browserBean = browserService.getBrowserBeanById(fileListResponse.getId());
		browserBean.setPath(fileListResponse.getPath());
		browserBean.setParentPath(fileListResponse.getParentPath());
		browserBean.setFiles(fileListResponse.getFileInfos());
		browserBean.responseArrived();
	}
	
	private void handleFileDownloadResponse(FileDownloadResponse fileDownloadResponse) {
		BrowserBean browserBean = browserService.getBrowserBeanById(fileDownloadResponse.getId());
		browserBean.setResponseDownloadLink(fileDownloadResponse.getLink());
		browserBean.setResponseErrorMessage(fileDownloadResponse.getErrorMessage());
		browserBean.responseArrived();
	}

	public void sendNotify(Client client) {
		Notify notify = new Notify();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setNotify(notify);
		send(client, wrapper);
	}

	public void sendStop(Client client) {
		Stop stop = new Stop();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStop(stop);
		send(client, wrapper);
	}
	
	public void sendFileListRequest(Client client, String id, String path) {
		MessageWrapper wrapper = new MessageWrapper();
		FileListRequest fileListRequest = new FileListRequest();
		fileListRequest.setId(id);
		fileListRequest.setPath(path);
		wrapper.setFileListRequest(fileListRequest);
		send(client, wrapper);
	}
	
	public void sendFileDownloadRequest(Client client, String id, String path, String name) {
		MessageWrapper wrapper = new MessageWrapper();
		FileDownloadRequest fileDownloadRequest = new FileDownloadRequest();
		fileDownloadRequest.setId(id);
		fileDownloadRequest.setPath(path);
		fileDownloadRequest.setName(name);
		wrapper.setFileDownloadRequest(fileDownloadRequest);
		send(client, wrapper);
	}

	public void onClose(Client client) {
		if (client.getType() == ClientType.RECEIVER) {
			Client senderToStop = clientService.removeReceiverAndReturnSenderToStopIfLastReceiver(client);
			if (senderToStop != null) {
				sendStop(senderToStop);
			}
		} else if (client.getType() == ClientType.SENDER) {
			Collection<Client> receiversToNotifiy = clientService.removeSenderAndReturnReceiversToNotify(client);
			if (receiversToNotifiy.size() > 0) {
				for (Client receiverToNofity : receiversToNotifiy) {
					sendNotify(receiverToNofity);
				}
			}
		}
	}

	private void sendStart(Client client) {
		Start start = new Start();
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStart(start);
		send(client, wrapper);
	}

	private void sendKeyEvent(Client client, KeyEvent keyEvent) {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setKeyEvent(keyEvent);
		send(client, wrapper);
	}

	private void sendMouseClick(Client client, MouseClick mouseClick) {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMouseClick(mouseClick);
		send(client, wrapper);
	}

	private void sendImage(Client client, Image image) {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setImage(image);
		send(client, wrapper);
	}

	private void sendCreateAccountAccessResponse(Client client, boolean success) {
		CreateAccountAccessResponse createAccountAccessResponse = new CreateAccountAccessResponse();
		createAccountAccessResponse.setSuccess(success);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAccountAccessResponse(createAccountAccessResponse);
		send(client, wrapper);
	}

	private void sendVerifyCreateAnonymousAccess(Client client, boolean success) {
		CreateAnonymousAccessResponse createAnonymousAccessResponse = new CreateAnonymousAccessResponse();
		createAnonymousAccessResponse.setSuccess(success);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setCreateAnonymousAccessResponse(createAnonymousAccessResponse);
		send(client, wrapper);
	}

	private void sendConnectResponse(Client client) {
		ConnectResponse connectResponse = new ConnectResponse();
		connectResponse.setSuccess(true);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setConnectResponse(connectResponse);
		send(client, wrapper);
	}

	private void send(Client client, MessageWrapper wrapper) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			String string = mapper.writeValueAsString(wrapper);
			client.getSession().getAsyncRemote().sendText(string);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
