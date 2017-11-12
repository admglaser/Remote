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

import org.apache.log4j.Logger;

import model.Client;
import service.AccountService;
import service.BrowserService;
import service.ClientService;

@ServerEndpoint("/websocket")
public class WebsocketServer {

	@EJB
	private ClientService clientService;

	@EJB
	private AccountService accountService;

	@EJB
	private BrowserService browserService;

	private Logger logger = Logger.getLogger(WebsocketServer.class);
	
	@OnOpen
	public void onOpen(Session session) {
		logger.info("open " + session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		logger.info("close " + session.getId());
		Client client = getClientService().findClientBySession(session);
		new MessageHandler(getClientService(), getLoginService(), getBrowserService()).onClose(client);
	}

	@OnError
	public void onError(Throwable error) {
		logger.error("onError: " + error);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			new MessageHandler(getClientService(), getLoginService(), getBrowserService()).handleMessage(message, session);
		} catch (IOException e) {
			logger.error("Failed to process message: " + e.getMessage());
		} catch (NoCommandException e) {
			logger.error("Failed to get command from message.");
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
	
	@SuppressWarnings("unchecked")
	public BrowserService getBrowserService() {
		if (browserService == null) {
			BeanManager bm = CDI.current().getBeanManager();
			Bean<BrowserService> bean = (Bean<BrowserService>) bm.getBeans(BrowserService.class).iterator().next();
			CreationalContext<BrowserService> ctx = bm.createCreationalContext(bean);
			browserService = (BrowserService) bm.getReference(bean, BrowserService.class, ctx);
		}
		return browserService;
	}

}
