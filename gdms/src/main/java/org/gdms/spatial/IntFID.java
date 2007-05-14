package org.gdms.spatial;


public class IntFID implements FID {
	private int i;

	public IntFID(int i) {
		this.i = i;
	}

	public boolean equals(Object o) {
		return ((IntFID) o).i == i;
	}

	public int hashCode() {
		return i;
	}

	public String toString() {
		return Integer.toString(i);
	}
}
