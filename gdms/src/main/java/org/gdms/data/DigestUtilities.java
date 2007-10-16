package org.gdms.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.gdms.driver.DriverException;

public class DigestUtilities {
	public static byte[] getDigest(DataSource ds)
			throws NoSuchAlgorithmException, DriverException {
		return getDigest(ds, ds.getRowCount());
	}

	public static boolean equals(byte[] digest1, byte[] digest2) {
		if (digest1.length != digest2.length) {
			return false;
		}
		for (int i = 0; i < digest2.length; i++) {
			if (digest1[i] != digest2[i]) {
				return false;
			}
		}

		return true;
	}

	public static byte[] getDigest(DataSource ds,
			long rowCount)
			throws DriverException, NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < ds.getFieldCount(); j++) {
				md5.update(ds.getFieldValue(i, j).getBytes());
			}
		}
		return md5.digest();
	}
}
