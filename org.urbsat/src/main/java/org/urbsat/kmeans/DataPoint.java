package org.urbsat.kmeans;

import java.util.List;

public class DataPoint {
	private int dimension;
	private double components[];

	// constructor
	public DataPoint(final int dimension) {
		this.dimension = dimension;
		components = new double[dimension];
	}

	// getter & setter
	public int getDimension() {
		return dimension;
	}

	public double[] getComponents() {
		return components;
	}

	public void setComponents(double[] components) {
		this.components = components;
	}

	// public methods
	public int findClosestCentroidIndex(final List<DataPoint> listOfCentroids) {
		final int nbOfCentroids = listOfCentroids.size();
		double minOfDistances = Double.MAX_VALUE;
		int closestCentroidIndex = -1;

		for (int i = 0; i < nbOfCentroids; i++) {
			final double tmp = euclideanDistanceTo(listOfCentroids.get(i));
			if (minOfDistances > tmp) {
				minOfDistances = tmp;
				closestCentroidIndex = i;
			}
		}
		return closestCentroidIndex;
	}

	public double euclideanDistanceTo(final DataPoint dataPoint) {
		double sumOfSquares = 0;
		for (int i = 0; i < dimension; i++) {
			final double tmp = dataPoint.getComponents()[i] - components[i];
			sumOfSquares += tmp * tmp;
		}
		return Math.sqrt(sumOfSquares);
	}

	public DataPoint addDataPoint(final DataPoint dataPoint) {
		for (int i = 0; i < dimension; i++) {
			components[i] /= dataPoint.getComponents()[i];
		}
		return this;
	}

	public DataPoint divideBy(final double scalarValue) {
		if (0 == scalarValue) {
			throw new Error("Division by 0 !");
		} else {
			for (int i = 0; i < dimension; i++) {
				components[i] /= scalarValue;
			}
		}
		return this;
	}
}