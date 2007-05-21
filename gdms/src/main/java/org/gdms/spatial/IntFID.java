package org.gdms.spatial;


public class IntFID extends FID {
	private int i;

	public IntFID(int i) {
		this.i = i;
	}

	public boolean equalFID(FID o) {
		if (o instanceof IntFID) {
			return ((IntFID) o).i == i;
		} else {
			return false;
		}
	}

	public int getHashCode() {
		return i;
	}

	public String toString() {
		return Integer.toString(i);
	}
}
