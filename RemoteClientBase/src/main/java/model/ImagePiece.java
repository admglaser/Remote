package model;

public class ImagePiece {

	private String encodedString;
	private Rectangle area;

	public ImagePiece(Rectangle area, String encodedString) {
		this.area = area;
		this.encodedString = encodedString;
	}

	public String getEncodedString() {
		return encodedString;
	}

	public void setEncodedString(String encodedString) {
		this.encodedString = encodedString;
	}

	public Rectangle getArea() {
		return area;
	}

	public void setArea(Rectangle area) {
		this.area = area;
	}

}
