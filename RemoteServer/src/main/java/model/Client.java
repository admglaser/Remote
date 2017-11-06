package model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.Session;

public class Client {

	private Session session;
	private String id;
	private ClientType type;
	private String deviceName;
	private int deviceWidth;
	private int deviceHeight;
	private Date connected;
	
	private User user;
	private AnonymousAccess anonymousAccess;
	
	private Set<Client> receivers;
	private Client sender;
	
	public Client(String id, String deviceName, int deviceWidth, int deviceHeight, ClientType type, Session session) {
		this.id = id;
		this.deviceName = deviceName;
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
		this.session = session;
		this.type = type;
		this.connected = new Date();
		if (type == ClientType.SENDER) {
			receivers = new HashSet<>();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Client) {
			Client client = (Client) obj;
			if (this.id.equals(client.getId())) {
				return true;
			}
		}
		return false;
	}

	public Session getSession() {
		return session;
	}

	public String getId() {
		return id;
	}

	public ClientType getType() {
		return type;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public int getDeviceWidth() {
		return deviceWidth;
	}

	public int getDeviceHeight() {
		return deviceHeight;
	}

	public Date getConnectedDate() {
		return connected;
	}

	public User getUser() {
		return user;
	}

	public AnonymousAccess getAnonymousAccess() {
		return anonymousAccess;
	}
	
	public Client getSender() {
		return sender;
	}

	public Set<Client> getReceivers() {
		return receivers;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setAnonymousAccess(AnonymousAccess anonymousAccess) {
		this.anonymousAccess = anonymousAccess;
	}

	public void setSender(Client sender) {
		this.sender = sender;
	}

}
