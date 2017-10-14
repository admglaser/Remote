package model.json;

public class RemoveAnonymousAccess implements Message {

	private boolean remove;

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

}
