package org.urbsat.kmeans;

import java.util.List;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;

public class DataPoint {
	private int dimension;
	private double components[];
	private String cellIndex;

	// constructors
	public DataPoint(final int dimension) {
		this.dimension = dimension;
		components = new double[dimension];
	}

	public DataPoint(final double[] components) {
		this.dimension = components.length;
		this.components = components;
	}

	public DataPoint(final Value[] fields, final int cellIndexFieldId) {
		this.dimension = fields.length - 1;
		components = new double[this.dimension];

		for (int fieldId = 0, d = 0; fieldId < fields.length; fieldId++) {
			if (cellIndexFieldId != fieldId) {
				components[d] = ((NumericValue) fields[fieldId]).doubleValue();
				d++;
			} else {
				cellIndex = fields[cellIndexFieldId].toString();
			}
		}
	}

	// getters & setters
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

	public DataPoint addDataPoint(final long dataPointIndex) {
		for (int i = 0; i < dimension; i++) {
			components[i] /= dataPointIndex.getComponents()[i];
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

	public void print() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dimension; i++) {
			sb.append(components[i]).append((i == dimension - 1) ? "" : ", ");
		}
		System.out.println("[ " + sb.toString() + " ]");
	}

	public String getIndex() {
		return cellIndex;
	}
}