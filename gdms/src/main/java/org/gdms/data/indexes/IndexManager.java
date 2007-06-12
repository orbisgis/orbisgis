package org.gdms.data.indexes;

import java.util.ArrayList;
import java.util.HashMap;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.hardcode.driverManager.DriverLoadException;

public class IndexManager {

	private DataSourceFactory dsf;

	private ArrayList<SourceIndex> indexes = new ArrayList<SourceIndex>();

	private HashMap<String, ArrayList<SourceIndex>> sourceIndex = new HashMap<String, ArrayList<SourceIndex>>();

	public IndexManager(DataSourceFactory dsf) {
		this.dsf = dsf;
	}

	/**
	 * Gets the DataSource indexes for the specified DataSource
	 *
	 * @param ds
	 * @return
	 * @throws DriverException
	 */
	public DataSourceIndex[] getDataSourceIndexes(DataSource ds) throws DriverException {
		ArrayList<SourceIndex> sourceIndexes = getIndexesFor(ds.getName());
		ArrayList<DataSourceIndex> ret = new ArrayList<DataSourceIndex>();
		for (SourceIndex index : sourceIndexes) {
			ret.add(index.getDataSourceIndex(ds));
		}

		return ret.toArray(new DataSourceIndex[1]);
	}

	/**
	 * Builds the specified index on the specified field of the datasource.
	 * Registers the built index to the source to be used in the next calls to
	 * getDataSourceIndex
	 *
	 * @param dsName
	 * @param fieldName
	 * @param indexId
	 * @throws IncompatibleTypesException
	 * @throws DriverLoadException
	 * @throws DriverException
	 * @throws NoSuchTableException
	 * @throws DataSourceCreationException
	 */
	public void buildIndex(String dsName, String fieldName, String indexId)
			throws IncompatibleTypesException, DriverLoadException,
			DriverException, NoSuchTableException, DataSourceCreationException {
		SourceIndex index = getIndex(indexId);
		index.buildIndex(dsf, dsName, fieldName);

		ArrayList<SourceIndex> sourceIndexes = getIndexesFor(dsName);
		sourceIndexes.add(index);
//TODO Don't use index right now		sourceIndex.put(dsName, sourceIndexes);
	}

	private ArrayList<SourceIndex> getIndexesFor(String dsName) {
		ArrayList<SourceIndex> sourceIndexes = sourceIndex.get(dsName);
		if (sourceIndexes == null) {
			sourceIndexes = new ArrayList<SourceIndex>();
		}
		return sourceIndexes;
	}

	private SourceIndex getIndex(String indexId) {
		for (SourceIndex index : indexes) {
			if (index.getId().equals(indexId)) {
				return index;
			}
		}

		return null;
	}

	/**
	 * Adds an index to the collection of indexes
	 *
	 * @param index
	 */
	public void addIndex(SourceIndex index) {
		indexes.add(index);
	}

}
