/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
 *
 *Copyright (C) 2011 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legend;

import java.util.ArrayList;

import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.ArrowSymbolEditor;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.ImageSymbolEditor;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlIntervalLegend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlLabelLegend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlProportionalLineLegend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlProportionalPointLegend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlUniqueSymbolLegend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.PnlUniqueValueLegend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.StandardSymbolEditor;

@Deprecated
public class EPLegendHelper {

	/**
	 * Create an arraylist with all legend panels used by the edit legend plugin. 
	 */
	public static ILegendPanel[] getLegendPanels(LegendContext legendContext) {

		ArrayList<ILegendPanel> legends = new ArrayList<ILegendPanel>();

		ILegendPanel pnlUniqueSymbolLegend = new PnlUniqueSymbolLegend();
		pnlUniqueSymbolLegend.initialize(legendContext);
		legends.add(pnlUniqueSymbolLegend);

		ILegendPanel pnlUniqueValueLegend = new PnlUniqueValueLegend();
		pnlUniqueValueLegend.initialize(legendContext);
		legends.add(pnlUniqueValueLegend);

		ILegendPanel pnlIntervalLegend = new PnlIntervalLegend();
		pnlIntervalLegend.initialize(legendContext);
		legends.add(pnlIntervalLegend);

		ILegendPanel pnlProportionalLegend = new PnlProportionalPointLegend();
		pnlProportionalLegend.initialize(legendContext);
		legends.add(pnlProportionalLegend);

		ILegendPanel pnlProportionalLineLegend = new PnlProportionalLineLegend();
		pnlProportionalLineLegend.initialize(legendContext);
		legends.add(pnlProportionalLineLegend);

		ILegendPanel pnlLabelLegend = new PnlLabelLegend();
		pnlLabelLegend.initialize(legendContext);
		legends.add(pnlLabelLegend);

		return legends.toArray(new ILegendPanel[legends.size()]);
	}

	/**
	 * Create an arraylist with all symbol panels loaded by the symbol editor panel. 
	 */
	public static ISymbolEditor[] getSymbolPanels() {

		ArrayList<ISymbolEditor> symbols = new ArrayList<ISymbolEditor>();
		ImageSymbolEditor ImageSymbolPanel = new ImageSymbolEditor();
		symbols.add(ImageSymbolPanel);
		StandardSymbolEditor standardSymbolEditor = new StandardSymbolEditor();
		symbols.add(standardSymbolEditor);
		ArrowSymbolEditor arrowSymbolEditor = new ArrowSymbolEditor();
		symbols.add(arrowSymbolEditor);
		return symbols.toArray(new ISymbolEditor[symbols.size()]);
	}

}
