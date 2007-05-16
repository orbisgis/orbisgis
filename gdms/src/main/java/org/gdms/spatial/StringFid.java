package org.gdms.spatial;


public class StringFid extends FID {

	private Object fid;

	public StringFid(String fid) {
		this.fid = fid;
	}

	@Override
	public boolean equalFID(FID obj) {
		return ((StringFid) obj).fid.equals(fid);
	}

	@Override
	public int getHashCode() {
		return fid.hashCode();
	}

}
