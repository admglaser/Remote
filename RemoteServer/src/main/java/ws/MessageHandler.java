package ws;

import java.util.Collection;
import java.util.UUID;

import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.AnonymousAccess;
import model.Client;
import model.ClientType;
import model.User;
import model.json.Connect;
import model.json.ConnectWrapper;
import model.json.CreateAccountAccess;
import model.json.CreateAccountAccessWrapper;
import model.json.CreateAnonymousAccess;
import model.json.CreateAnonymousAccessWrapper;
import model.json.Identify;
import model.json.IdentifyWrapper;
import model.json.Image;
import model.json.ImageWrapper;
import model.json.KeyEvent;
import model.json.KeyEventWrapper;
import model.json.MessageWrapper;
import model.json.MouseClick;
import model.json.MouseClickWrapper;
import model.json.RemoveAccountAccess;
import model.json.RemoveAccountAccessWrapper;
import model.json.RemoveAnonymousAccess;
import model.json.RemoveAnonymousAccessWrapper;
import model.json.Start;
import model.json.StartWrapper;
import model.json.Stop;
import model.json.StopWrapper;
import model.json.VerifyConnect;
import model.json.VerifyConnectWrapper;
import model.json.VerifyCreateAccountAccess;
import model.json.VerifyCreateAccountAccessWrapper;
import model.json.VerifyCreateAnonymousAccess;
import model.json.VerifyCreateAnonymousAccessWrapper;
import service.ClientService;
import service.LoginService;

public class MessageHandler {

	private ClientService clientService;
	private LoginService loginService;
	private Client client;
	private Session session;

	public MessageHandler(ClientService clientService, LoginService loginService) {
		this.clientService = clientService;
		this.loginService = loginService;
	}

	public void handleMessage(String message, Session session) {
		this.session = session;
		this.client = clientService.getClients().getClient(session);
		try {

			ObjectMapper mapper = new ObjectMapper();

			try {
				ImageWrapper imageWrapper = mapper.readValue(message, ImageWrapper.class);
				parseImage(imageWrapper.getImage());
				return;
			} catch (Exception e) {
			}
			
			System.out.println(message);

			try {
				ConnectWrapper connectWrapper = mapper.readValue(message, ConnectWrapper.class);
				parseConnect(connectWrapper.getConnect());
				return;
			} catch (Exception e) {
			}

			try {
				IdentifyWrapper identifyWrapper = mapper.readValue(message, IdentifyWrapper.class);
				parseIdentify(identifyWrapper.getIdentify());
				return;
			} catch (Exception e) {
			}

			try {
				CreateAnonymousAccessWrapper createAnonymousAccessWrapper = mapper.readValue(message, CreateAnonymousAccessWrapper.class);
				parseCreateAnonymousAccess(createAnonymousAccessWrapper.getCreateAnonymousAccess());
				return;
			} catch (Exception e) {
			}

			try {
				CreateAccountAccessWrapper createAccountAccessWrapper = mapper.readValue(message, CreateAccountAccessWrapper.class);
				parseCreateAccountAccess(createAccountAccessWrapper.getCreateAccountAccess());
				return;
			} catch (Exception e) {
				System.out.println(e);
			}

			try {
				RemoveAnonymousAccessWrapper removeAnonymousAccessWrapper = mapper.readValue(message, RemoveAnonymousAccessWrapper.class);
				parseRemoveAnonymousAccess(removeAnonymousAccessWrapper.getRemoveAnonymousAccess());
				return;
			} catch (Exception e) {
			}
			
			try {
				RemoveAccountAccessWrapper removeAccountAccessWrapper = mapper.readValue(message, RemoveAccountAccessWrapper.class);
				parseRemoveAccountAccess(removeAccountAccessWrapper.getRemoveAccountAccess());
				return;
			} catch (Exception e) {
			}

			try {
				MouseClickWrapper mouseClickWrapper = mapper.readValue(message, MouseClickWrapper.class);
				parseMouseClick(mouseClickWrapper.getMouseClick());
				return;
			} catch (Exception e) {
			}
			
			try {
				KeyEventWrapper keyEventWrapper = mapper.readValue(message, KeyEventWrapper.class);
				parseKeyEvent(keyEventWrapper.getKeyEvent());
				return;
			} catch (Exception e) {
			}

			throw new Exception();
		} catch (Exception e) {
			System.out.println("Failed to parse message as json: " + message);
		}
	}

	private void parseKeyEvent(KeyEvent keyEvent) {
		Client sender = clientService.getClientPairings().getSender(client);
		sendKeyEvent(sender, keyEvent);
	}

	private void parseMouseClick(MouseClick mouseClick) {
		Client sender = clientService.getClientPairings().getSender(client);
		sendMouseClick(sender, mouseClick);
	}

	private void parseImage(Image image) {
		for (Client receiver : clientService.getClientPairings().getReceivers(client)) {
			sendImage(receiver, image);
		}
	}

	private void parseConnect(Connect connect) {
		String id = connect.getId();
		Client senderClient = clientService.getClients().getClient(id);
		boolean sendStartToSender = false;
		if (!clientService.isSenderCapturing(senderClient)) {
			sendStartToSender = true;
		}
		clientService.getClientPairings().addPairing(senderClient, client);
		if (sendStartToSender) {
			sendStart(senderClient);
		}
		sendVerifyConnect(client);

		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

	private void parseCreateAnonymousAccess(CreateAnonymousAccess createAnonymousAccess) {
		String numericId = createAnonymousAccess.getNumericId();
		String numericPassword = createAnonymousAccess.getNumericPassword();
		AnonymousAccess anonymousAccess = new AnonymousAccess(numericId, numericPassword);
		if (clientService.getClients().isAnonymousAccessUnique(anonymousAccess)) {
			sendVerifyCreateAnonymousAccess(client, true);
			clientService.getClients().addAnonymousAccess(client, anonymousAccess);
		} else {
			sendVerifyCreateAnonymousAccess(client, false);
		}

		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

	private void parseCreateAccountAccess(CreateAccountAccess createAccountAccess) {
		String username = createAccountAccess.getUsername();
		String password = createAccountAccess.getPassword();
	
		User user = loginService.getUser(username, password);
		if (user == null) {
			sendVerifyCreateAccountAccess(client, false);
		} else {
			sendVerifyCreateAccountAccess(client, true);
			clientService.getClients().addAccountAccess(client, user);
		}
	
		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

	private void parseRemoveAnonymousAccess(RemoveAnonymousAccess removeAnonymousAccess) {
		if (removeAnonymousAccess.isRemove()) {
			clientService.getClients().removeAnonymousAccess(client);
		}

		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

	private void parseRemoveAccountAccess(RemoveAccountAccess removeAccountAccess) {
		if (removeAccountAccess.isRemove()) {
			clientService.getClients().removeAccountAccess(client);
		}
	
		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

	private void parseIdentify(Identify identify) {
		String id = UUID.randomUUID().toString();
		String deviceName = identify.getDeviceName();
		int deviceWidth = identify.getDeviceWidth();
		int deviceHeight = identify.getDeviceHeight();
		String type = identify.getType();
		client = new Client(id, deviceName, deviceWidth, deviceHeight, ClientType.fromString(type), session);
		clientService.getClients().addClient(client);

		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

	public void sendNotify(Client client) {
		// TODO Auto-generated method stub
	}

	public void sendStop(Client client) {
		Stop stop = new Stop();
		stop.setStop(true);
		StopWrapper stopWrapper = new StopWrapper();
		stopWrapper.setStop(stop);
		send(client, stopWrapper);
	}

	private void sendStart(Client client) {
		Start start = new Start();
		start.setStart(true);
		StartWrapper startWrapper = new StartWrapper();
		startWrapper.setStart(start);
		send(client, startWrapper);
	}

	private void sendKeyEvent(Client client, KeyEvent keyEvent) {
		KeyEventWrapper keyEventWrapper = new KeyEventWrapper();
		keyEventWrapper.setKeyEvent(keyEvent);
		send(client, keyEventWrapper);
	}

	private void sendMouseClick(Client client, MouseClick mouseClick) {
		MouseClickWrapper mouseClickWrapper = new MouseClickWrapper();
		mouseClickWrapper.setMouseClick(mouseClick);
		send(client, mouseClickWrapper);
	}

	private void sendImage(Client client, Image image) {
		ImageWrapper imageWrapper = new ImageWrapper();
		imageWrapper.setImage(image);
		send(client, imageWrapper);
	}

	private void sendVerifyCreateAccountAccess(Client client, boolean success) {
		VerifyCreateAccountAccess verifyCreateAccountAccess = new VerifyCreateAccountAccess();
		verifyCreateAccountAccess.setSuccess(success);
		VerifyCreateAccountAccessWrapper verifyCreateAccountAccessWrapper = new VerifyCreateAccountAccessWrapper();
		verifyCreateAccountAccessWrapper.setVerifyCreateAccountAccess(verifyCreateAccountAccess);
		send(client, verifyCreateAccountAccessWrapper);
	}

	private void sendVerifyCreateAnonymousAccess(Client client, boolean success) {
		VerifyCreateAnonymousAccess verifyCreateAnonymousAccess = new VerifyCreateAnonymousAccess();
		verifyCreateAnonymousAccess.setSuccess(success);
		VerifyCreateAnonymousAccessWrapper verifyCreateAnonymousAccessWrapper = new VerifyCreateAnonymousAccessWrapper();
		verifyCreateAnonymousAccessWrapper.setVerifyCreateAnonymousAccess(verifyCreateAnonymousAccess);
		send(client, verifyCreateAnonymousAccessWrapper);
	}

	private void sendVerifyConnect(Client client) {
		VerifyConnect verifyConnect = new VerifyConnect();
		verifyConnect.setSuccess(true);
		VerifyConnectWrapper verifyConnectWrapper = new VerifyConnectWrapper();
		verifyConnectWrapper.setVerifyConnect(verifyConnect);
		send(client, verifyConnectWrapper);
	}

	private void send(Client client, MessageWrapper messageWrapper) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String string = objectMapper.writeValueAsString(messageWrapper);
			client.getSession().getAsyncRemote().sendText(string);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void onClose(Client client) {
		if (client.getType() == ClientType.RECEIVER) {
			Client senderToStop = clientService.removeReceiverAndReturnSenderToStop(client);
			if (senderToStop != null) {
				sendStop(senderToStop);
			}
		} else if (client.getType() == ClientType.SENDER) {
			Collection<Client> receiversToNotifiy = clientService.removeSender(client);
			if (receiversToNotifiy.size() > 0) {
				for (Client receiverToNofity : receiversToNotifiy) {
					sendNotify(receiverToNofity);
				}
			}
		}

		System.out.println("clients: " + clientService.getClients().getClients().size());
	}

}
