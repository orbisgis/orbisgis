package org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legend;

import java.util.ArrayList;

import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.ArrowSymbolEditor;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.ImageSymbolEditor;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.PnlIntervalLegend;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.PnlLabelLegend;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.PnlProportionalLegend;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.PnlUniqueSymbolLegend;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.PnlUniqueValueLegend;
import org.orbisgis.plugins.core.ui.editorViews.toc.actions.cui.legends.StandardSymbolEditor;

public class EPLegendHelper {

	// TODO (pyf): à mettre sous forme de plugin
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

		ILegendPanel pnlProportionalLegend = new PnlProportionalLegend();
		pnlProportionalLegend.initialize(legendContext);
		legends.add(pnlProportionalLegend);

		ILegendPanel pnlLabelLegend = new PnlLabelLegend();
		pnlLabelLegend.initialize(legendContext);
		legends.add(pnlLabelLegend);

		return legends.toArray(new ILegendPanel[0]);
	}

	// TODO (pyf): à mettre sous forme de plugin
	public static ISymbolEditor[] getSymbolPanels() {

		ArrayList<ISymbolEditor> symbols = new ArrayList<ISymbolEditor>();
		ImageSymbolEditor ImageSymbolPanel = new ImageSymbolEditor();
		symbols.add(ImageSymbolPanel);
		StandardSymbolEditor standardSymbolEditor = new StandardSymbolEditor();
		symbols.add(standardSymbolEditor);
		ArrowSymbolEditor arrowSymbolEditor = new ArrowSymbolEditor();
		symbols.add(arrowSymbolEditor);
		return symbols.toArray(new ISymbolEditor[0]);

	}

}
