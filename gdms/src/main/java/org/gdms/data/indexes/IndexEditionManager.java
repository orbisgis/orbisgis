package org.gdms.data.indexes;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;

public class IndexEditionManager {

	private DataSource ds;

	private IndexManager im;

	private boolean modified;

	private DataSourceIndex[] modifiedIndexes;

	public IndexEditionManager(DataSourceFactory dsf, DataSource ds) {
		this.im = dsf.getIndexManager();
		this.ds = ds;
	}

	public void open() {
		modifiedIndexes = null;
	}

	public void commit() {
		if (modified) {
			im.indexesChanged(ds.getName());
		}
	}

	public DataSourceIndex[] getDataSourceIndexes() throws IndexException {
		if (modified) {
			return getModifiedIndexes();
		} else {
			try {
				return im.getIndexes(ds.getName());
			} catch (NoSuchTableException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private DataSourceIndex[] getModifiedIndexes() throws IndexException {
		if (modifiedIndexes == null) {
			try {
				DataSourceIndex[] toClone = im.getIndexes(ds.getName());
				modifiedIndexes = new DataSourceIndex[toClone.length];
				for (int i = 0; i < toClone.length; i++) {
					modifiedIndexes[i] = toClone[i].cloneIndex(ds);
				}
			} catch (NoSuchTableException e) {
				throw new RuntimeException(e);
			}
		}

		return modifiedIndexes;
	}

	public void modifiedSource() {
		this.modified = true;
	}
}
