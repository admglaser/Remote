package network;

public class WebsocketSession implements Session {

	private javax.websocket.Session session;

	public WebsocketSession(javax.websocket.Session session) {
		this.session = session;
	}
	
	@Override
	public void send(String message) {
		session.getAsyncRemote().sendText(message);
	}

}
