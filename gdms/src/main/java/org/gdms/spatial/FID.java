package org.gdms.spatial;

public abstract class FID {

	public final boolean equals(Object obj) {
		return equalFID((FID) obj);
	}

	public abstract boolean equalFID(FID obj);

	public final int hashCode() {
		return getHashCode();
	}

	public abstract int getHashCode();
}
