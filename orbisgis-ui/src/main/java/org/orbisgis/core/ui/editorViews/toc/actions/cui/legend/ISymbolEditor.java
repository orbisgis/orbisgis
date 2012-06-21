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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legend;

import java.awt.Component;

import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.SymbolEditorListener;

public interface ISymbolEditor {

	/**
	 * Set the symbol editor listener.
	 * 
	 * @param listener
	 */
	void setSymbolEditorListener(SymbolEditorListener listener);

	/**
	 * Set the symbol this editor have to edit
	 * 
	 * @param symbol
	 */
	void setSymbol(Symbol symbol);

	/**
	 * Get the symbol currently in edition
	 * 
	 * @return
	 */
	Symbol getSymbol();

	/**
	 * Return true if the editor can edit this symbol, false otherwise
	 * 
	 * @param symbol
	 * @return
	 */
	boolean accepts(Symbol symbol);

	/**
	 * Return the swing component that will perform the edition
	 * 
	 * @return
	 */
	Component getComponent();

	/**
	 * Creates a new empty instance of this editor
	 * 
	 * @return
	 */
	ISymbolEditor newInstance();
}
