package model;

public enum ClientType {

	RECEIVER, SENDER;

	public static ClientType fromString(String type) {
		switch (type) {
		case "sender":
			return SENDER;
		case "receiver":
			return RECEIVER;
		default:
			throw new RuntimeException("Unknown client type: " + type);
		}
	}
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
	
}
