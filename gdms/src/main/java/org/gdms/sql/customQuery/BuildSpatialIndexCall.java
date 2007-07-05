package org.gdms.sql.customQuery;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.SpatialIndex;
import org.gdms.data.values.Value;

public class BuildSpatialIndexCall implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {
		if (values.length != 2) {
			throw new ExecutionException("Usage:\n" +
					"call BuildSpatialIndex ('sourceName', 'spatialFieldName');");
		}

		try {
			dsf.getIndexManager().buildIndex(values[0].toString(),
					values[1].toString(), SpatialIndex.SPATIAL_INDEX);
		} catch (IndexException e) {
			throw new ExecutionException(e);
		} catch (NoSuchTableException e) {
			throw new ExecutionException(e);
		}

		return null;
	}

	public String getName() {
		return "BuildSpatialIndex";
	}

}
