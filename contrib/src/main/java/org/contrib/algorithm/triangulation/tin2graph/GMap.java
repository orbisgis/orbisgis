package org.contrib.algorithm.triangulation.tin2graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;

public class GMap<T extends GNode> {
	private Map<Integer, T> bag;

	public GMap() {
		bag = new HashMap<Integer, T>();
	}

	public boolean containsKey(final Integer key) {
		return bag.containsKey(key);
	}

	public T get(final Integer key) {
		return bag.get(key);
	}

	public Set<Integer> keySet() {
		return bag.keySet();
	}

	public T put(final Integer key, final T value) {
		return bag.put(key, value);
	}

	public int size() {
		return bag.size();
	}

	public void store(final Class<? extends GNode> typeOfGNode,
			final DataSourceFactory dsf, final String dataSourceName)
			throws DriverException, DriverLoadException, NoSuchTableException,
			DataSourceCreationException, NonEditableDataSourceException,
			InstantiationException, IllegalAccessException {
		final String dsName = dataSourceName.concat("-"
				+ typeOfGNode.getSimpleName());
		final ObjectMemoryDriver driver = new ObjectMemoryDriver(
				((GNode) typeOfGNode.newInstance()).getMetadata());
		dsf.getSourceManager().register(dsName, driver);

		final DataSource dataSource = dsf.getDataSource(dsName);
		dataSource.open();
		for (Integer gid : bag.keySet()) {
			bag.get(gid).store(gid, dataSource);
		}
		dataSource.commit();
		dataSource.close();
	}
}