package org.gdms.sql.customQuery;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.driver.DriverException;

public class SpatialTableDefinition implements TableDefinition {
	public boolean isValid(Metadata metadata) throws DriverException {
		return MetadataUtilities.isGeometry(metadata)
				|| MetadataUtilities.isRaster(metadata);
	}

	public String getDescription() {
		return "A geometry or a raster table is needed";
	}
}