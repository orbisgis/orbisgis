package org.urbsat.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private List<Long> listOfDataPoints = null;
	private int dimension;

	Cluster(final int dimension) {
		this.dimension = dimension;
		listOfDataPoints = new ArrayList<Long>();
	}

	List<Long> getListOfDataPoints() {
		return listOfDataPoints;
	}

	int size() {
		return listOfDataPoints.size();
	}

	public DataPoint getCentroid() {
		DataPoint centroid = new DataPoint(dimension);
		for (long dataPointIndex : listOfDataPoints) {
			centroid.addDataPoint(dataPointIndex);
		}
		return centroid.divideBy(size());
	}

	public void addPoint(final Long dataPointIndex) {
		listOfDataPoints.add(dataPointIndex);
	}
}