package network;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import javax.websocket.Session;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import entity.Account;
import model.AnonymousAccess;
import model.Client;
import model.ClientType;
import model.MessageWrapper;
import model.message.Connect;
import model.message.CreateAccountAccess;
import model.message.CreateAnonymousAccess;
import model.message.Identify;
import model.message.Image;
import model.message.KeyEvent;
import model.message.MouseClick;
import model.message.Notify;
import model.message.RemoveAccountAccess;
import model.message.RemoveAnonymousAccess;
import model.message.Start;
import model.message.Stop;
import model.message.VerifyConnect;
import model.message.VerifyCreateAccountAccess;
import model.message.VerifyCreateAnonymousAccess;
import service.AccountService;
import service.ClientService;

public class MessageHandler {

	protected ClientService clientService;
	protected AccountService accountService;
	protected Client client;
	protected Session session;

	public MessageHandler(ClientService clientService, AccountService accountService) {
		this.clientService = clientService;
		this.accountService = accountService;
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
			parseImage(image);
			return;
		}

		System.out.println("onMessage: " + message);

		Connect connect = wrapper.getConnect();
		if (connect != null) {
			parseConnect(connect);
			return;
		}

		Identify identify = wrapper.getIdentify();
		if (identify != null) {
			parseIdentify(identify);
			return;
		}

		CreateAccountAccess createAccountAccess = wrapper.getCreateAccountAccess();
		if (createAccountAccess != null) {
			parseCreateAccountAccess(createAccountAccess);
			return;
		}

		CreateAnonymousAccess createAnonymousAccess = wrapper.getCreateAnonymousAccess();
		if (createAnonymousAccess != null) {
			parseCreateAnonymousAccess(createAnonymousAccess);
			return;
		}

		RemoveAccountAccess removeAccountAccess = wrapper.getRemoveAccountAccess();
		if (removeAccountAccess != null) {
			parseRemoveAccountAccess(removeAccountAccess);
			return;
		}

		RemoveAnonymousAccess removeAnonymousAccess = wrapper.getRemoveAnonymousAccess();
		if (removeAnonymousAccess != null) {
			parseRemoveAnonymousAccess(removeAnonymousAccess);
			return;
		}

		MouseClick mouseClick = wrapper.getMouseClick();
		if (mouseClick != null) {
			parseMouseClick(mouseClick);
			return;
		}

		KeyEvent keyEvent = wrapper.getKeyEvent();
		if (keyEvent != null) {
			parseKeyEvent(keyEvent);
			return;
		}

		throw new NoCommandException();
	}

	private void parseIdentify(Identify identify) {
		String id = UUID.randomUUID().toString();
		String deviceName = identify.getDeviceName();
		int deviceWidth = identify.getDeviceWidth();
		int deviceHeight = identify.getDeviceHeight();
		String type = identify.getType();
		client = new Client(id, deviceName, deviceWidth, deviceHeight, ClientType.fromString(type), session);
		clientService.getClients().add(client);
	
		System.out.println("clients: " + clientService.getClients().size());
	}

	private void parseCreateAccountAccess(CreateAccountAccess createAccountAccess) {
		String username = createAccountAccess.getUsername();
		String password = createAccountAccess.getPassword();
	
		Account user = accountService.getAccount(username, password);
		if (user == null) {
			sendVerifyCreateAccountAccess(client, false);
		} else {
			sendVerifyCreateAccountAccess(client, true);
			client.setAccount(user);
		}
	
		System.out.println("clients: " + clientService.getClients().size());
	}

	private void parseCreateAnonymousAccess(CreateAnonymousAccess createAnonymousAccess) {
		String numericId = createAnonymousAccess.getNumericId();
		String numericPassword = createAnonymousAccess.getNumericPassword();
		AnonymousAccess anonymousAccess = new AnonymousAccess(numericId, numericPassword);
		if (clientService.isAccessUnique(anonymousAccess)) {
			sendVerifyCreateAnonymousAccess(client, true);
			client.setAnonymousAccess(anonymousAccess);
		} else {
			sendVerifyCreateAnonymousAccess(client, false);
		}
	
		System.out.println("clients: " + clientService.getClients().size());
	}

	private void parseRemoveAccountAccess(RemoveAccountAccess removeAccountAccess) {
		client.setAccount(null);
	
		System.out.println("clients: " + clientService.getClients().size());
	}

	private void parseRemoveAnonymousAccess(RemoveAnonymousAccess removeAnonymousAccess) {
		client.setAnonymousAccess(null);
	
		System.out.println("clients: " + clientService.getClients().size());
	}

	private void parseConnect(Connect connect) {
		String senderId = connect.getId();
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
		sendVerifyConnect(client);
	
		System.out.println("clients: " + clientService.getClients().size());
	}

	private void parseImage(Image image) {
		for (Client receiver : client.getReceivers()) {
			sendImage(receiver, image);
		}
	}

	private void parseMouseClick(MouseClick mouseClick) {
		Client sender = client.getSender();
		sendMouseClick(sender, mouseClick);
	}
	
	private void parseKeyEvent(KeyEvent keyEvent) {
		Client sender = client.getSender();
		sendKeyEvent(sender, keyEvent);
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

		System.out.println("clients: " + clientService.getClients().size());
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

	private void sendVerifyCreateAccountAccess(Client client, boolean success) {
		VerifyCreateAccountAccess verifyCreateAccountAccess = new VerifyCreateAccountAccess();
		verifyCreateAccountAccess.setSuccess(success);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setVerifyCreateAccountAccess(verifyCreateAccountAccess);
		send(client, wrapper);
	}

	private void sendVerifyCreateAnonymousAccess(Client client, boolean success) {
		VerifyCreateAnonymousAccess verifyCreateAnonymousAccess = new VerifyCreateAnonymousAccess();
		verifyCreateAnonymousAccess.setSuccess(success);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setVerifyCreateAnonymousAccess(verifyCreateAnonymousAccess);
		send(client, wrapper);
	}

	private void sendVerifyConnect(Client client) {
		VerifyConnect verifyConnect = new VerifyConnect();
		verifyConnect.setSuccess(true);
		MessageWrapper wrapper = new MessageWrapper();
		wrapper.setVerifyConnect(verifyConnect);
		send(client, wrapper);
	}

	public void send(Client client, MessageWrapper wrapper) {
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
