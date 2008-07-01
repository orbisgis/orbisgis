package org.gdms.sql.customQuery;

import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

public interface TableDefinition {

	TableDefinition SPATIAL = new GeometryTableDefinition();
	TableDefinition ANY = new AnyTableDefinition();

	/**
	 * Returns true if the specified metadata is valid for the custom query
	 *
	 * @param metadata
	 * @return
	 * @throws DriverException
	 */
	boolean isValid(Metadata metadata) throws DriverException;

	/**
	 * Gets the human readable description of this table definition
	 *
	 * @return
	 */
	String getDescription();

}
