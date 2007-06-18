package org.gdms.data.indexes;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.hardcode.driverManager.DriverLoadException;

public class IndexResolver {

	private IndexManager im;

	private DataSourceIndex[] indexes;

	private DataSource ds;

	public IndexResolver(IndexManager im, DataSource ds) {
		this.im = im;
		this.ds = ds;
	}

	public void invalidateIndexes() {
		this.indexes = null;
	}

	public DataSourceIndex[] getDataSourceIndexes() throws DriverException {
		if (indexes != null) {
			return indexes;
		} else {
			return new DataSourceIndex[0];
		}
	}

	public void openIndexes() throws DriverException {
		indexes = im.getDataSourceIndexes(ds);
		for (DataSourceIndex index : indexes) {
			index.setDataSource(ds);
		}
	}

	public void commitIndexChanges() throws IncompatibleTypesException,
			DriverLoadException, DriverException, NoSuchTableException,
			DataSourceCreationException {
		if (indexes.length > 0) {
			im.setDataSourceIndexes(ds.getName(), indexes);
			indexes = null;
		}
	}
}
