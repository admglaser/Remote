package network;

import java.io.IOException;

import javax.ejb.EJB;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import model.Client;
import service.AccountService;
import service.ClientService;

@ServerEndpoint("/websocket")
public class WebsocketServer {

	@EJB
	private ClientService clientService;

	@EJB
	private AccountService accountService;

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("open " + session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("close");
		ClientService clientService = getClientService();
		AccountService accountService = getLoginService();
		Client client = clientService.findClientBySession(session);
		new MessageHandler(clientService, accountService).onClose(client);
	}

	@OnError
	public void onError(Throwable error) {
		System.out.println("onError: " + error);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			new MessageHandler(getClientService(), getLoginService()).handleMessage(message, session);
		} catch (IOException e) {
			System.out.println("Failed to process message: " + e.getMessage());
		} catch (NoCommandException e) {
			System.out.println("Failed to get command from message.");
		}
	}

	@SuppressWarnings("unchecked")
	public ClientService getClientService() {
		if (clientService == null) {
			BeanManager bm = CDI.current().getBeanManager();
			Bean<ClientService> bean = (Bean<ClientService>) bm.getBeans(ClientService.class).iterator().next();
			CreationalContext<ClientService> ctx = bm.createCreationalContext(bean);
			clientService = (ClientService) bm.getReference(bean, ClientService.class, ctx);
		}
		return clientService;
	}

	@SuppressWarnings("unchecked")
	public AccountService getLoginService() {
		if (accountService == null) {
			BeanManager bm = CDI.current().getBeanManager();
			Bean<AccountService> bean = (Bean<AccountService>) bm.getBeans(AccountService.class).iterator().next();
			CreationalContext<AccountService> ctx = bm.createCreationalContext(bean);
			accountService = (AccountService) bm.getReference(bean, AccountService.class, ctx);
		}
		return accountService;
	}

}
