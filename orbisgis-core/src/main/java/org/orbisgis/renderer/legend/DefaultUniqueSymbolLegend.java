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
import org.gdms.driver.DriverException;

public class DefaultUniqueSymbolLegend extends AbstractSimpleLegend implements
		UniqueSymbolLegend {

	private Symbol symbol;
	private ArrayList<Symbol> symbols;

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}

	private ArrayList<Symbol> getSymbols() throws RenderException {
		if (symbols == null) {
			if (getDataSource() == null) {
				throw new RenderException("The legend is "
						+ "not associated to a DataSource");
			}
			try {
				symbols = doClassification();
			} catch (DriverException e) {
				throw new RenderException("Error while accessing the "
						+ "data: cannot draw", e);
			}
		}

		return symbols;
	}

	protected ArrayList<Symbol> doClassification() throws DriverException,
			RenderException {
		SpatialDataSourceDecorator sds = getDataSource();
		ArrayList<Symbol> ret = new ArrayList<Symbol>();
		for (int i = 0; i < sds.getRowCount(); i++) {
			Symbol symbolToDraw = RenderUtils.buildSymbolToDraw(getSymbol(),
					sds.getGeometry(i));
			if (symbolToDraw != null) {
				ret.add(symbolToDraw);
			} else {
				ret.add(SymbolFactory.createNullSymbol());
			}
		}

		return ret;
	}

	public Symbol getSymbol(long row) throws RenderException {
		ArrayList<Symbol> symbols = getSymbols();
		Symbol ret;
		if (symbols == null) {
			ret = getSymbol();
		} else {
			ret = symbols.get((int) row);
		}
		return ret;
	}

	public String getLegendTypeName() {
		return "Unique Symbol Legend";
	}

}
