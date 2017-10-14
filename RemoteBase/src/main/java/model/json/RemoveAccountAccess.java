package model.json;

public class RemoveAccountAccess implements Message {

	private boolean remove;

	public boolean isRemove() {
		return remove;
	}

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

}
