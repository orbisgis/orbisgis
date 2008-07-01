package org.gdms.sql.customQuery;

import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;

public class AnyTableDefinition implements TableDefinition {

	public boolean isValid(Metadata metadata) throws DriverException {
		return true;
	}

	public String getDescription() {
		return "Any table is valid";
	}

}
