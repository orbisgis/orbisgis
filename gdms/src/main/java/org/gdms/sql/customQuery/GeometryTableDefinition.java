package org.gdms.sql.customQuery;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.driver.DriverException;

public class GeometryTableDefinition implements TableDefinition {

	public boolean isValid(Metadata metadata) throws DriverException {
		return MetadataUtilities.isGeometry(metadata);
	}

	public String getDescription() {
		return "A geometry table is needed";
	}
}
