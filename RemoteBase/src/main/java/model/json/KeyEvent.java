package model.json;

public class KeyEvent implements Message {
	
	public static final int TYPE_KEYDOWN = 0;
	public static final int TYPE_KEYUP = 1;
	public static final int TYPE_KEYPRESS = 2;
	
	private int type;
	private int code;
	private String character;

	public int getType() {
		return type;
	}

	public void setType(int tpye) {
		this.type = tpye;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

}
