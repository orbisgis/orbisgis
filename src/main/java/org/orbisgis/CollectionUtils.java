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

}
