package model.message;

public class Identify {

	private String type;
	private String deviceName;
	private int deviceWidth;
	private int deviceHeight;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getDeviceWidth() {
		return deviceWidth;
	}

	public void setDeviceWidth(int deviceWidth) {
		this.deviceWidth = deviceWidth;
	}

	public int getDeviceHeight() {
		return deviceHeight;
	}

	public void setDeviceHeight(int deviceHeight) {
		this.deviceHeight = deviceHeight;
	}
	
}
