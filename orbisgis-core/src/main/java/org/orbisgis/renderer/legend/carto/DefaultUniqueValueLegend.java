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

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.legend.Legend;
import org.orbisgis.renderer.legend.RenderException;
import org.orbisgis.renderer.symbol.RenderUtils;
import org.orbisgis.renderer.symbol.Symbol;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultUniqueValueLegend extends AbstractClassifiedLegend
		implements UniqueValueLegend {

	private ArrayList<Value> values = new ArrayList<Value>();
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	private ArrayList<String> labels = new ArrayList<String>();

	public void addClassification(Value value, Symbol symbol, String label) {
		values.add(value);
		symbols.add(symbol);
		labels.add(label);
		fireLegendInvalid();
	}

	public String getLegendTypeName() {
		return NAME;
	}

	public Symbol getSymbol(int index) {
		return symbols.get(index);
	}

	private int getValueIndex(Value value) {
		return values.indexOf(value);
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			int fieldIndex = sds.getSpatialFieldIndex();
			Value value = sds.getFieldValue(row, fieldIndex);
			Geometry geom = sds.getGeometry(row);
			int symbolIndex = getValueIndex(value);
			if (symbolIndex != -1) {
				Symbol classificationSymbol = this.symbols.get(symbolIndex);
				Symbol symbol = RenderUtils.buildSymbolToDraw(
						classificationSymbol, geom);
				return symbol;
			} else {
				return getDefaultSymbol();
			}
		} catch (DriverException e) {
			throw new RenderException("Cannot access the layer contents", e);
		}
	}

	public String getVersion() {
		return "1.0";
	}

	public void save(File file) {
		throw new UnsupportedOperationException();
	}

	public void load(File file, String version) {
		throw new UnsupportedOperationException();
	}

	public String getLegendTypeId() {
		return "org.orbisgis.legend.ValueClassification";
	}

	public Legend newInstance() {
		return new DefaultUniqueValueLegend();
	}

	public String getLabel(int index) throws IllegalArgumentException {
		return labels.get(index);
	}

	public Value getValue(int index) {
		return values.get(index);
	}

	public int getValueCount() {
		return values.size();
	}

	public void setLabel(int index, String label)
			throws IllegalArgumentException {
		labels.set(index, label);
	}

	public void setSymbol(int index, Symbol symbol)
			throws IllegalArgumentException {
		symbols.set(index, symbol);
	}

	public void setValue(int index, Value value) {
		values.set(index, value);
	}

	public void clear() {
		values.clear();
		symbols.clear();
		labels.clear();
	}

	public void removeClassification(int index)
			throws IllegalArgumentException {
		values.remove(index);
		symbols.remove(index);
		labels.remove(index);
	}

}
