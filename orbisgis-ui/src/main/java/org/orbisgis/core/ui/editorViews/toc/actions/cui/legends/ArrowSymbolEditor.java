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
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.renderer.symbol.ArrowSymbol;
import org.orbisgis.core.renderer.symbol.Symbol;
import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;

public class ArrowSymbolEditor extends StandardSymbolEditor implements
		ISymbolEditor {

	private JNumericSpinner spnArrowLength;

	@Override
	public boolean accepts(Symbol symbol) {
		return symbol instanceof ArrowSymbol;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public ISymbolEditor newInstance() {
		return new ArrowSymbolEditor();
	}

	@Override
	protected JPanel getPnlSizeControls() {
		JPanel ret = super.getPnlSizeControls();
		spnArrowLength = getSpinner(1, Integer.MAX_VALUE);
		ret.add(spnArrowLength);

		return ret;
	}

	@Override
	protected JPanel getPnlSizeTexts() {
		JPanel ret = super.getPnlSizeTexts();
		ret.add(new JLabel("Arrow length"));

		return ret;
	}

	@Override
	protected void symbolChanged() {
		if (!ignoreEvents) {
			super.symbolChanged();
			ArrowSymbol arrowSymbol = (ArrowSymbol) symbol;
			arrowSymbol.setArrowLength((int) spnArrowLength.getValue());
		}
	}

	@Override
	public void setSymbol(Symbol symbol) {
		super.setSymbol(symbol);
		ArrowSymbol arrowSymbol = (ArrowSymbol) symbol;
		spnArrowLength.setValue(arrowSymbol.getArrowLength());
	}
}
