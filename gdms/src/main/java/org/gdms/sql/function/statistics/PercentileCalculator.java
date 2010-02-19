/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/***********************************
 * <p>Title: CarThema</p>
 * Perspectives Software Solutions
 * Copyright (c) 2006
 * @author Vladimir Peric, Vladimir Cetkovic
 ***********************************/

package org.gdms.sql.function.statistics;

import java.util.Arrays;

public class PercentileCalculator {

	/**
	 * Determines what percentile is computed when evaluate() is activated with
	 * no quantile argument -- use median value (50) as default
	 */
	private double quantile = 50.0;

	/**
	 * Constructs a PercentileCalculator with a default quantile value of 50.0.
	 */
	public PercentileCalculator() {
		this(50.0);
	}

	/**
	 * Constructs a PercentileCalculator with the specific quantile value.
	 * 
	 * @param p
	 *            the quantile
	 * @throws IllegalArgumentException
	 *             if p is not greater than 0 and less than or equal to 100
	 */
	public PercentileCalculator(final double p) {
		setQuantile(p);
	}

	/**
	 * See {@link PercentileCalculator#evaluate(double[], int, int, double)} for
	 * a description of the percentile estimation algorithm used.
	 * 
	 * @param values
	 *            input array of values
	 * @param p
	 *            the percentile value to compute
	 * @return the result of the evaluation or Double.NaN if the array is empty
	 * @throws IllegalArgumentException
	 *             if values is null
	 */
	public double evaluate(final double[] values, final double p) {
		return evaluate(values, 0, values.length, p);
	}

	/**
	 * See {@link PercentileCalculator#evaluate(double[], int, int, double)} for
	 * a description of the percentile estimation algorithm used.
	 * 
	 * @param values
	 *            the input array
	 * @param start
	 *            index of the first array element to include
	 * @param length
	 *            the number of elements to include
	 * @return the percentile value
	 * @throws IllegalArgumentException
	 *             if the parameters are not valid
	 * 
	 */
	public double evaluate(final double[] values, final int start,
			final int length) {
		return evaluate(values, start, length, quantile);
	}

	/**
	 * Returns an estimate of the pth percentile of the values in the values
	 * array, starting with the element in (0-based) position begin in the array
	 * and including length values.
	 * 
	 * @param values
	 *            array of input values
	 * @param p
	 *            the percentile to compute
	 * @param begin
	 *            the first (0-based) element to include in the computation
	 * @param length
	 *            the number of array elements to include
	 * @return the percentile value
	 * @throws IllegalArgumentException
	 *             if the parameters are not valid or the input array is null
	 */
	public double evaluate(final double[] values, final int begin,
			final int length, final double p) {

		if ((p > 100) || (p <= 0)) {
			throw new IllegalArgumentException("invalid quantile value: " + p);
		}
		double n = (double) length;
		if (n == 0) {
			return Double.NaN;
		}
		if (n == 1) {
			return values[begin]; // always return single value for n = 1
		}
		double pos = p * (n + 1) / 100;
		double fpos = Math.floor(pos);
		int intPos = (int) fpos;
		double dif = pos - fpos;
		double[] sorted = new double[length];
		System.arraycopy(values, begin, sorted, 0, length);
		Arrays.sort(sorted);

		if (pos < 1) {
			return sorted[0];
		}
		if (pos >= n) {
			return sorted[length - 1];
		}
		double lower = sorted[intPos - 1];
		double upper = sorted[intPos];
		return lower + dif * (upper - lower);
	}

	/**
	 * Returns the value of the quantile field (determines what percentile is
	 * computed when evaluate() is called with no quantile argument).
	 * 
	 * @return quantile
	 */
	public double getQuantile() {
		return quantile;
	}

	/**
	 * Sets the value of the quantile field (determines what percentile is
	 * computed when evaluate() is called with no quantile argument).
	 * 
	 * @param p
	 *            a value between 0 < p <= 100
	 * @throws IllegalArgumentException
	 *             if p is not greater than 0 and less than or equal to 100
	 */
	public void setQuantile(final double p) {
		if (p <= 0 || p > 100) {
			throw new IllegalArgumentException("Illegal quantile value: " + p);
		}
		quantile = p;
	}

	/**
	 * Used for testing the calculating the percentile on an a array
	 * 
	 * @param args
	 */

	public static void main(String args[]) {
		double vals[] = { 10, 3, 2, 1, 10, 54 };
		System.out.println(Math.round(new PercentileCalculator().evaluate(vals,
				75)));
	}

}
