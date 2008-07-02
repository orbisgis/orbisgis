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

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.symbol.RenderUtils;
import org.orbisgis.renderer.symbol.Symbol;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultUniqueValueLegend extends AbstractClassifiedLegend
		implements UniqueValueLegend {

	private ArrayList<Value> values = new ArrayList<Value>();
	private ArrayList<Symbol> symbols = new ArrayList<Symbol>();

	public void addClassification(Value value, Symbol symbol) {
		values.add(value);
		symbols.add(symbol);
		fireLegendInvalid();
	}

	public String getLegendTypeName() {
		return "Unique Value Legend";
	}

	public Value[] getClassificationValues() {
		return values.toArray(new Value[0]);
	}

	public Symbol getValueSymbol(Value value) {
		return symbols.get(values.indexOf(value));
	}

	public Symbol getSymbol(SpatialDataSourceDecorator sds, long row)
			throws RenderException {
		try {
			int fieldIndex = sds.getSpatialFieldIndex();
			Value value = sds.getFieldValue(row, fieldIndex);
			Geometry geom = sds.getGeometry(row);
			int symbolIndex = values.indexOf(value);
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

}
