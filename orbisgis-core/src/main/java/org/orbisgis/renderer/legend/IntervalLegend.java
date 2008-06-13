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
package org.orbisgis.renderer.legend;

import java.util.ArrayList;

import org.gdms.data.values.Value;

public interface IntervalLegend extends ClassifiedLegend {

	/**
	 * Adds a classification to the legend
	 *
	 * @param initialValue
	 *            Initial value of the interval to classify
	 * @param minIncluded
	 *            If the initialValue is included in the interval
	 * @param finalValue
	 *            Final value of the interval to classify
	 * @param maxIncluded
	 *            If the finalValue is included in the interval
	 * @param symbol
	 *            Symbol for the interval, including the initial and final
	 *            values
	 */
	void addInterval(Value initialValue, boolean minIncluded, Value finalValue,
			boolean maxIncluded, Symbol symbol);

	/**
	 * Adds a classification to the legend
	 *
	 * @param initialValue
	 *            Initial value of the interval to classify
	 * @param included
	 *            if the value is included in the interval
	 * @param symbol
	 *            symbol for the values that match the interval
	 */
	void addIntervalWithMinLimit(Value initialValue, boolean included,
			Symbol symbol);

	/**
	 * Adds a classification to the legend
	 *
	 * @param finalValue
	 *            Final value of the interval to classify
	 * @param included
	 *            if the value is included in the interval
	 * @param symbol
	 *            symbol for the values that match the interval
	 */
	void addIntervalWithMaxLimit(Value finalValue, boolean included,
			Symbol symbol);
	
	public ArrayList<Interval> getIntervals();
	
	public Symbol getSymbolInterval(Interval inter);

}
