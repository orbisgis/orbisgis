package org.orbisgis;

public class CollectionUtils {

	public static boolean contains(Object[] collection, Object testObject) {
		for (Object object : collection) {
			if (object == testObject) {
				return true;
			}
		}

		return false;
	}

	public static String getCommaSeparated(Object[] array) {
		StringBuilder ret = new StringBuilder("");
		String separator = "";
		for (Object object : array) {
			ret.append(separator).append(object);
			separator=", ";
		}

		return ret.toString();
	}

}
