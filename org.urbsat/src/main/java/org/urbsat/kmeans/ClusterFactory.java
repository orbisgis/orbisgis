package org.urbsat.kmeans;

import java.util.Arrays;
import java.util.List;

public class ClusterFactory {
	public List<Cluster> createClusters(final List<DataPoint> listOfDataPoints,
			final List<DataPoint> listOfCentroids) {
		final Cluster[] arrayOfClusters = new Cluster[listOfCentroids.size()];
		for (DataPoint dataPoint : listOfDataPoints) {
			final int clusterIndex = dataPoint
					.findClosestCentroidIndex(listOfCentroids);
			if (null == arrayOfClusters[clusterIndex]) {
				arrayOfClusters[clusterIndex] = new Cluster();
			}
			arrayOfClusters[clusterIndex].addPoint(dataPoint);
		}
		return Arrays.asList(arrayOfClusters);
	}
}