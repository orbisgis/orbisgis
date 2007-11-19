package org.urbsat.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	private List<DataPoint> listOfDataPoints = null;

	// constructor
	public Cluster() {
		listOfDataPoints = new ArrayList<DataPoint>();
	}

	// getters & setters
	public List<DataPoint> getListOfDataPoints() {
		return listOfDataPoints;
	}

	// public void setListOfDataPoints(List<DataPoint> listOfDataPoints) {
	// this.listOfDataPoints = listOfDataPoints;
	// }

	// public methods
	public int size() {
		return listOfDataPoints.size();
	}

	public int getDimension() {
		return listOfDataPoints.get(0).getDimension();
	}

	public DataPoint getCentroid() {
		DataPoint centroid = new DataPoint(getDimension());
		for (DataPoint dataPoint : listOfDataPoints) {
			centroid.addDataPoint(dataPoint);
		}
		return centroid.divideBy(size());
	}

	public void addPoint(final DataPoint dataPoint) {
		listOfDataPoints.add(dataPoint);
	}
}