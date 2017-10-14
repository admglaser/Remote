package model;

public class AnonymousAccess {

	private String numericId;
	private String numericPassword;
	
	public AnonymousAccess(String numericId, String numericPassword) {
		this.numericId = numericId;
		this.numericPassword = numericPassword;
	}

	public String getNumericId() {
		return numericId;
	}

	public String getNumericPassword() {
		return numericPassword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numericId == null) ? 0 : numericId.hashCode());
		result = prime * result + ((numericPassword == null) ? 0 : numericPassword.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnonymousAccess other = (AnonymousAccess) obj;
		if (numericId == null) {
			if (other.numericId != null)
				return false;
		} else if (!numericId.equals(other.numericId))
			return false;
		if (numericPassword == null) {
			if (other.numericPassword != null)
				return false;
		} else if (!numericPassword.equals(other.numericPassword))
			return false;
		return true;
	}
	
}
