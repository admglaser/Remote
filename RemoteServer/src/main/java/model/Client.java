package model;

import java.util.Date;

import javax.websocket.Session;

public class Client {

	private Session session;
	private String id;
	private ClientType type;
	private String deviceName;
	private int deviceWidth;
	private int deviceHeight;
	private Date connected;
	
	public Session getSession() {
		return session;
	}

	public Client(String id, String deviceName, int deviceWidth, int deviceHeight, ClientType type, Session session) {
		this.id = id;
		this.deviceName = deviceName;
		this.deviceWidth = deviceWidth;
		this.deviceHeight = deviceHeight;
		this.session = session;
		this.type = type;
		this.connected = new Date();
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

}
