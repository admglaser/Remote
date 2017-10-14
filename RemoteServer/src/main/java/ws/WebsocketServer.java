package ws;

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
import service.ClientService;
import service.LoginService;

@ServerEndpoint("/websocket")
public class WebsocketServer {

	@EJB
	private ClientService clientService;
	
	@EJB
	private LoginService loginService;

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("open " + session.getId());
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("close");
		ClientService clientService = getClientService();
		LoginService loginService = getLoginService();
		Client client = clientService.getClients().getClient(session);
		new MessageHandler(clientService, loginService).onClose(client);
	}

	@OnError
	public void onError(Throwable error) {
		System.out.println("onError: " + error);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		new MessageHandler(getClientService(), getLoginService()).handleMessage(message, session);
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
	public LoginService getLoginService() {
		if (loginService == null) {
			BeanManager bm = CDI.current().getBeanManager();
			Bean<LoginService> bean = (Bean<LoginService>) bm.getBeans(LoginService.class).iterator().next();
			CreationalContext<LoginService> ctx = bm.createCreationalContext(bean);
			loginService = (LoginService) bm.getReference(bean, LoginService.class, ctx);
		}
		return loginService;
	}

}
