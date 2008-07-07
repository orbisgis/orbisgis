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
package org.orbisgis.renderer.legend.carto;

import org.gdms.data.values.Value;
import org.orbisgis.renderer.symbol.Symbol;

public interface UniqueValueLegend extends ClassifiedLegend {

	String NAME = "Unique value legend";

	/**
	 * Adds a classification to the legend
	 *
	 * @param value
	 *            Classification value
	 * @param symbol
	 *            Symbol to draw the features that is equal the specified
	 *            classification value
	 * @param label
	 *            Human readable description for the classification
	 */
	void addClassification(Value value, Symbol symbol, String label);

	/**
	 * Gets the number of classified values in this legend
	 *
	 * @return
	 */
	int getValueCount();

	/**
	 * Gets the value of the i-th classification
	 *
	 * @param index
	 * @return
	 */
	Value getValue(int index);

	/**
	 * Gets the symbol used in the specified classification
	 *
	 * @param value
	 * @return The associated symbol
	 */
	Symbol getSymbol(int i);

	/**
	 * Gets the label for the specified classification
	 *
	 * @param value
	 * @return
	 */
	String getLabel(int i);

	/**
	 * Sets the label for the specified classification
	 *
	 * @param i
	 * @param label
	 */
	void setLabel(int i, String label);

	/**
	 * Sets the symbol for the specified classification
	 *
	 * @param index
	 * @param symbol
	 */
	void setSymbol(int index, Symbol symbol);

	/**
	 * Sets the label for the specified classification
	 *
	 * @param index
	 * @param value
	 */
	void setValue(int index, Value value);

	/**
	 * Clears all the classifications in the legend
	 */
	void clear();

	/**
	 * Removes the specified classification
	 *
	 * @param index
	 */
	void removeClassification(int index);

}
