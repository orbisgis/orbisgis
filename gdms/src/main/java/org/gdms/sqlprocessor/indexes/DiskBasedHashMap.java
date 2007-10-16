package org.gdms.sqlprocessor.indexes;

import java.util.HashMap;

import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * Disk-based hashmap. The keys are Value objects and the values are
 * PhysicalDirections that contain those values. As the values
 * (PhysicalDirections) have constant length we store as many PhysicalDirections
 * as BUCKET_SIZE for the first bucket together at the beginning of the file.
 * After those ones we store the ones for the second bucket and so on. If the
 * there more PhysicalDirections than BUCKET_SIZE in a bucket we store a special
 * element pointing to the next set of BUCKET_SIZE PhysicalDirections.
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class DiskBasedHashMap {

	public static final int BUCKET_SIZE = 64;

	public static final int PHYSICAL_DIR = 0;

	public static final int REDIRECTION = 1;

	public HashMap<Value, PhysicalDirection> map = new HashMap<Value, PhysicalDirection>();

	private int fieldId;

	public DiskBasedHashMap(int fieldId) {
		this.fieldId = fieldId;
	}

	public void create(int numValues) {

	}

	public void insert(Value v, PhysicalDirection dir) {
		map.put(v, dir);
	}

	public PhysicalDirection[] getPosibleDirections(Value v) {
		return new PhysicalDirection[] { map.get(v) };
	}

	public boolean remove(PhysicalDirection dir) throws DriverException {
		return map.remove(dir.getFieldValue(fieldId)) != null;
	}

	public void setValue(Value oldValue, Value newValue, PhysicalDirection dir)
			throws DriverException {
		if (!remove(dir)) {
			throw new RuntimeException("PhisicalDirection is not at the index");
		}

		insert(newValue, dir);
	}

}
