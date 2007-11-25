package org.urbsat.kmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class Cluster {
	private List<Long> listOfDataPointsIndex = null;
	private int dimension;
	private DataSource inDs;
	private int cellIndexFieldId;

	public Cluster(final int dimension, final DataSource inDs,
			final int cellIndexFieldId) {
		this.dimension = dimension;
		this.inDs = inDs;
		this.cellIndexFieldId = cellIndexFieldId;
		listOfDataPointsIndex = new ArrayList<Long>();
	}

	public List<Long> getListOfDataPointsIndex() {
		return listOfDataPointsIndex;
	}

	public int size() {
		return listOfDataPointsIndex.size();
	}

	public DataPoint getCentroid() throws DriverException {
		final DataPoint centroid = new DataPoint(dimension);
		for (long dataPointIndex : listOfDataPointsIndex) {
			final Value[] dataPointValues = inDs.getRow(dataPointIndex);
			centroid.addDataPoint(new DataPoint(dataPointValues,
					cellIndexFieldId));
		}
		return centroid.divideBy(size());
	}

	public void addDataPointIndex(final Long dataPointIndex) {
		listOfDataPointsIndex.add(dataPointIndex);
	}

	public boolean isDifferentFrom(final Cluster cluster) {
		if (size() != cluster.size()) {
			return true;
		}
		Collections.sort(listOfDataPointsIndex);
		Collections.sort(cluster.getListOfDataPointsIndex());
		for (int i = 0; i < size(); i++) {
			if (listOfDataPointsIndex.get(i) != cluster
					.getListOfDataPointsIndex().get(i)) {
				return true;
			}
		}
		return false;
	}
}