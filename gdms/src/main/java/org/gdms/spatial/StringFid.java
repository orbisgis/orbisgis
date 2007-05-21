package org.gdms.spatial;


public class StringFid extends FID {

	private Object fid;

	public StringFid(String fid) {
		this.fid = fid;
	}

	@Override
	public boolean equalFID(FID obj) {
		if (obj instanceof StringFid) {
			return ((StringFid) obj).fid.equals(fid);
		} else {
			return false;
		}
	}

	@Override
	public int getHashCode() {
		return fid.hashCode();
	}

}
