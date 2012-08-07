/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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

import org.orbisgis.core.renderer.symbol.collection.persistence.SymbolType;

/**
 * Keeps a collection of available symbols in the system. The collection can be
 * increased by adding new symbol types and new symbols of the specified types
 * can be created
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public interface SymbolManager {

	/**
	 * Gets all the symbols the system supports
	 * 
	 * @return
	 */
	ArrayList<Symbol> getAvailableSymbols();

	/**
	 * Creates a new instance of the symbol with the specified id
	 * 
	 * @param symbolId
	 * @return
	 */
	Symbol createSymbol(String symbolId);

	/**
	 * Adds a new symbol to the system
	 * 
	 * @param symbol
	 * @return
	 */
	boolean addSymbol(Symbol symbol);

	/**
	 * Gets a JAXB object that contains all the info of the symbol
	 * 
	 * @param symbol
	 * @return
	 */
	SymbolType getJAXBSymbol(Symbol symbol);

	/**
	 * Builds a symbol from the information in the JAXB symbol. Returns null if
	 * the symbol id is not recognized, this is, there is no symbol in the
	 * manager with the same id
	 * 
	 * @param symbol
	 * @return
	 */
	Symbol getSymbolFromJAXB(SymbolType symbol);
}
