package org.gdms.sql.function;

import org.gdms.data.types.Type;

public interface ArgumentValidator {

	/**
	 * Returns true if the specified type is valid as argument. False otherwise
	 *
	 * @param type
	 * @return
	 */
	public boolean isValid(Type type);
}
