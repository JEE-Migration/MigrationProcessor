package uniandes.migration.util;


public class MethodKey {
	
	private final String type;
	private final String signature;
	
	public MethodKey(String type, String signature) {
		this.type = type;
		this.signature = signature;
	}

	public String getType() {
		return type;
	}

	public String getSignature() {
		return signature;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((signature == null) ? 0 : signature.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		MethodKey other = (MethodKey) obj;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MethodKey [type=" + type + ", signature=" + signature + "]";
	}
	


}
