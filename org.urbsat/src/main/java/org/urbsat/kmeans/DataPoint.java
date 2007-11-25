package org.urbsat.kmeans;

import java.util.List;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;

public class DataPoint {
	private double components[];

	// constructors
	public DataPoint(final int dimension) {
		this(new double[dimension]);
	}

	public DataPoint(final double[] components) {
		this.components = components;
	}

	public DataPoint(final Value[] fields, final int cellIndexFieldId) {
		this(fields.length - 1);

		for (int fieldId = 0, d = 0; fieldId < fields.length; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				components[d] = ((NumericValue) fields[fieldId]).doubleValue();
				d++;
			}
		}
	}

	// getters & setters
	public int getDimension() {
		return components.length;
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
		for (int i = 0; i < getDimension(); i++) {
			final double tmp = dataPoint.getComponents()[i] - components[i];
			sumOfSquares += tmp * tmp;
		}
		return Math.sqrt(sumOfSquares);
	}

	public DataPoint addDataPoint(final DataPoint dataPoint) {
		for (int i = 0; i < getDimension(); i++) {
			components[i] += dataPoint.getComponents()[i];
		}
		return this;
	}

	public DataPoint divideBy(final double scalarValue) {
		if (0 == scalarValue) {
			throw new Error("Division by 0 !");
		} else {
			for (int i = 0; i < getDimension(); i++) {
				components[i] /= scalarValue;
			}
		}
		return this;
	}

	public void print() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getDimension(); i++) {
			sb.append(components[i]).append(
					(i == getDimension() - 1) ? "" : ", ");
		}
		System.out.println("[ " + sb.toString() + " ]");
	}
}