package model.message;

public class KeyEvent {
	
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyEvent other = (KeyEvent) obj;
		if (character == null) {
			if (other.character != null)
				return false;
		} else if (!character.equals(other.character))
			return false;
		if (code != other.code)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
}
