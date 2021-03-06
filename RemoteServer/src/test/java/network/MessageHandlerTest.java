package network;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import entity.Account;
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
		MessageHandler messageHandler = new MessageHandler(mock(ClientService.class), mock(AccountService.class), mock(BrowserService.class));

		messageHandler.handleMessage(getNoCommandMessage(), mock(Session.class));
	}
	
	@Test(expected=JsonParseException.class)
	public void expectIOException() throws Exception {
		MessageHandler messageHandler = new MessageHandler(mock(ClientService.class), mock(AccountService.class), mock(BrowserService.class));

		messageHandler.handleMessage(getUnparsableJsonMessage(), mock(Session.class));
	}
	
	@Test
	public void handleIdentify() throws Exception {
		Client client = getClient();
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.getClients()).thenReturn(new HashSet<Client>());
		
		messageHandler.handleMessage(getIdentifyMessage(client), client.getSession());
		
		Client identifiedClient = clientService.getClients().iterator().next();	
		assertEquals(client.getType(), identifiedClient.getType());
		assertEquals(client.getDeviceName(), identifiedClient.getDeviceName());
		assertEquals(client.getDeviceWidth(), identifiedClient.getDeviceWidth());
		assertEquals(client.getDeviceHeight(), identifiedClient.getDeviceHeight()); 
	}

	@Test
	public void handleCreateAccountAccessRequest() throws Exception {
		Client client = getClient();
		ClientService clientService = mock(ClientService.class);
		AccountService accountService = mock(AccountService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, accountService, mock(BrowserService.class));
		String username = "username";
		String password = "password";
		
		when(clientService.findClientBySession(client.getSession())).thenReturn(client);
		when(client.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		when(accountService.getAccount(username, password)).thenReturn(new Account());
		
		messageHandler.handleMessage(getCreateAccountAccessRequestMessage(username, password), client.getSession());
		
		verify(client.getSession().getAsyncRemote()).sendText(getCreateAccountAccessResponseMessage());
	}
	
	@Test
	public void handleCreateAnonymousAccessRequest() throws Exception {
		Client client = getClient();
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		String id = "123456789";
		String password = "1234";
		AnonymousAccess anonymousAccess = new AnonymousAccess(id, password);
		
		when(clientService.findClientBySession(client.getSession())).thenReturn(client);
		when(clientService.isAccessUnique(anonymousAccess)).thenReturn(true);
		when(client.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.handleMessage(getCreateAnonymousRequestAccess(id, password), client.getSession());
		
		verify(client.getSession().getAsyncRemote()).sendText(getCreateAnonymousAccessResponseMessage());
	}

	@Test
	public void handleRemoveAccountAccess() throws Exception {
		Client client = getClient();
		client.setAccount(new Account());
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.findClientBySession(client.getSession())).thenReturn(client);
		messageHandler.handleMessage(getRemoveAccountsAccessMessage(), client.getSession());
		
		assertEquals(null, client.getAccount());
	}
	
	@Test
	public void handleRemoveAnonymousAccess() throws Exception {
		Client client = getClient();
		client.setAnonymousAccess(new AnonymousAccess("123456789", "1234"));
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.findClientBySession(client.getSession())).thenReturn(client);
		messageHandler.handleMessage(getRemoveAnonymousAccessMessage(), client.getSession());
		
		assertEquals(null, client.getAnonymousAccess());
	}

	@Test
	public void handleConnectRequest() throws Exception {
		Client receiver = new Client("id1", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client sender = new Client("id2", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.findClientBySession(receiver.getSession())).thenReturn(receiver);
		when(clientService.findClientById(sender.getId())).thenReturn(sender);
		when(sender.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		when(receiver.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.handleMessage(getConnectRequestMessage(sender.getId()), receiver.getSession());
		
		assertEquals(1, sender.getReceivers().size());
		assertEquals(receiver, sender.getReceivers().iterator().next());
		verify(sender.getSession().getAsyncRemote()).sendText(getStartMessage());
		verify(receiver.getSession().getAsyncRemote()).sendText(getConnectResponseMessage());
	}

	@Test
	public void handleImage() throws Exception {
		Client receiver1 = new Client("id1", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client receiver2 = new Client("id2", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client sender = new Client("id3", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		sender.getReceivers().add(receiver1);
		sender.getReceivers().add(receiver2);
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.findClientBySession(sender.getSession())).thenReturn(sender);
		when(receiver1.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		when(receiver2.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.handleMessage(getImageMessage(), sender.getSession());
		
		verify(receiver1.getSession().getAsyncRemote()).sendText(getImageMessage());
		verify(receiver2.getSession().getAsyncRemote()).sendText(getImageMessage());
	}

	@Test
	public void handleMouseClick() throws Exception {
		Client receiver = new Client("id1", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client sender = new Client("id2", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		receiver.setSender(sender);
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.findClientBySession(receiver.getSession())).thenReturn(receiver);
		when(sender.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.handleMessage(getMouseClickEventMessage(), receiver.getSession());
		
		verify(sender.getSession().getAsyncRemote()).sendText(getMouseClickEventMessage());
	}

	@Test
	public void handleKeyEvent() throws Exception {
		Client receiver = new Client("id1", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client sender = new Client("id2", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		receiver.setSender(sender);
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.findClientBySession(receiver.getSession())).thenReturn(receiver);
		when(sender.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.handleMessage(getKeyEventMessage(), receiver.getSession());
		
		verify(sender.getSession().getAsyncRemote()).sendText(getKeyEventMessage());
	}

	@Test
	public void onCloseSender() throws Exception {
		Client receiver = new Client("id1", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client sender = new Client("id2", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		receiver.setSender(sender);
		sender.getReceivers().add(receiver);
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.removeSenderAndReturnReceiversToNotify(sender)).thenReturn(sender.getReceivers());
		when(receiver.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.onClose(sender);
		
		verify(receiver.getSession().getAsyncRemote()).sendText(getNotifyMessage());
	}
	
	@Test
	public void onCloseReceiver() throws Exception {
		Client receiver = new Client("id1", null, 0, 0, ClientType.RECEIVER, mock(Session.class));
		Client sender = new Client("id2", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		receiver.setSender(sender);
		sender.getReceivers().add(receiver);
		ClientService clientService = mock(ClientService.class);
		MessageHandler messageHandler = new MessageHandler(clientService, mock(AccountService.class), mock(BrowserService.class));
		
		when(clientService.removeReceiverAndReturnSenderToStopIfLastReceiver(receiver)).thenReturn(receiver.getSender());
		when(sender.getSession().getAsyncRemote()).thenReturn(mock(Async.class));
		
		messageHandler.onClose(receiver);
		
		verify(sender.getSession().getAsyncRemote()).sendText(getStopMessage());
	}
	
	private Client getClient() {
		Client client = new Client("id", "device", 100, 100, ClientType.SENDER, mock(Session.class));
		return client;
	}

	private String getNoCommandMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		return mapper.writeValueAsString(wrapper);
	}
	
	private String getUnparsableJsonMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		return mapper.writeValueAsString(wrapper).replace("{", "");
	}

	private String getIdentifyMessage(Client client) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		Identify identify = new Identify();
		identify.setType(client.getType().toString());
		identify.setDeviceName(client.getDeviceName());
		identify.setDeviceWidth(client.getDeviceWidth());
		identify.setDeviceHeight(client.getDeviceHeight());
		wrapper.setIdentify(identify);
		return mapper.writeValueAsString(wrapper);
	}

	private String getImageMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		Image image = new Image();
		image.setData("");
		wrapper.setImage(image);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAccountAccessRequestMessage(String username, String password) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		CreateAccountAccessRequest createAccountAccessRequest = new CreateAccountAccessRequest();
		createAccountAccessRequest.setUsername(username);
		createAccountAccessRequest.setPassword(password);
		wrapper.setCreateAccountAccessRequest(createAccountAccessRequest);
		return mapper.writeValueAsString(wrapper);
	}
	
	private String getCreateAnonymousRequestAccess(String id, String password) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		CreateAnonymousAccessRequest createAnonymousAccessRequest = new CreateAnonymousAccessRequest();
		createAnonymousAccessRequest.setNumericId(id);
		createAnonymousAccessRequest.setNumericPassword(password);
		wrapper.setCreateAnonymousAccessRequest(createAnonymousAccessRequest);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAccountAccessResponseMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		CreateAccountAccessResponse createAccountAccessResponse = new CreateAccountAccessResponse();
		createAccountAccessResponse.setSuccess(true);
		wrapper.setCreateAccountAccessResponse(createAccountAccessResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getCreateAnonymousAccessResponseMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		CreateAnonymousAccessResponse createAnonymousAccessResponse= new CreateAnonymousAccessResponse();
		createAnonymousAccessResponse.setSuccess(true);
		wrapper.setCreateAnonymousAccessResponse(createAnonymousAccessResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getRemoveAccountsAccessMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAccountAccess(new RemoveAccountAccess());
		return mapper.writeValueAsString(wrapper);
	}

	private String getRemoveAnonymousAccessMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setRemoveAnonymousAccess(new RemoveAnonymousAccess());
		return mapper.writeValueAsString(wrapper);
	}

	private String getConnectRequestMessage(String id) throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		ConnectRequest connectRequest = new ConnectRequest();
		connectRequest.setId(id);
		wrapper.setConnectRequest(connectRequest);
		return mapper.writeValueAsString(wrapper);
	}

	private String getConnectResponseMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		ConnectResponse connectResponse = new ConnectResponse();
		connectResponse.setSuccess(true);
		wrapper.setConnectResponse(connectResponse);
		return mapper.writeValueAsString(wrapper);
	}

	private String getStartMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStart(new Start());
		return mapper.writeValueAsString(wrapper);
	}

	private String getMouseClickEventMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setMouseClick(new MouseClick());
		return mapper.writeValueAsString(wrapper);
	}

	private String getKeyEventMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setKeyEvent(new KeyEvent());
		return mapper.writeValueAsString(wrapper);
	}

	private String getNotifyMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setNotify(new Notify());
		return mapper.writeValueAsString(wrapper);
	}

	private String getStopMessage() throws JsonProcessingException {
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setStop(new Stop());
		return mapper.writeValueAsString(wrapper);
	}
	
}
