/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.renderer.symbol;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Geometry;

public class RenderUtils {

	/**
	 * Returns a symbol that will draw the geometry. If the specified symbol is
	 * a symbol composite it will remove the symbols inside that will not draw
	 * the geometry. It will return null if there is no symbol to draw the
	 * geometry
	 * 
	 * @param symbol
	 * @param geometry
	 * @return
	 */
	public static Symbol buildSymbolToDraw(Symbol symbol, Geometry geometry) {
		if ((symbol == null) || (geometry == null)) {
			return null;
		} else if (symbol instanceof SymbolComposite) {
			SymbolComposite comp = (SymbolComposite) symbol;
			ArrayList<Symbol> symbols = new ArrayList<Symbol>();
			for (int i = 0; i < comp.getSymbolCount(); i++) {
				Symbol newSymbol = buildSymbolToDraw(comp.getSymbol(i),
						geometry);
				if (newSymbol != null) {
					symbols.add(newSymbol);
				}
			}
			if (symbols.size() == 0) {
				return null;
			} else {
				return SymbolFactory.createSymbolComposite(symbols
						.toArray(new Symbol[symbols.size()]));
			}
		} else {
			if (symbol.acceptGeometry(geometry)) {
				return symbol;
			} else {
				return null;
			}
		}
	}
}
