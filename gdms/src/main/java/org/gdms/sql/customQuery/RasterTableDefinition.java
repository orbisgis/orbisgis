package org.gdms.sql.customQuery;

import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.driver.DriverException;

public class RasterTableDefinition implements TableDefinition {

	public boolean isValid(Metadata metadata) throws DriverException {
		return MetadataUtilities.isRaster(metadata);
	}

	public String getDescription() {
		return "A raster table is needed";
	}
}
