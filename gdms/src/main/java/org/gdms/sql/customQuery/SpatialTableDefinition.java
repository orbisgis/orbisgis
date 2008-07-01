package org.gdms.sql.customQuery;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.driver.DriverException;

public class SpatialTableDefinition implements TableDefinition {

	public boolean isValid(Metadata metadata) throws DriverException {
		return MetadataUtilities.isGeomety(metadata);
	}

	public String getDescription() {
		return "A spatial table is needed";
	}
}
