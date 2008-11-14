package org.orbisgis.editorViews.toc.actions.cui.legend;

import java.util.ArrayList;

import org.orbisgis.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPLegendHelper {

	public static ILegendPanel[] getLegendPanels(LegendContext legendContext) {
		ExtensionPointManager<ILegendPanel> epm = new ExtensionPointManager<ILegendPanel>(
				"org.orbisgis.LegendPanel");
		ArrayList<ItemAttributes<ILegendPanel>> ia = epm
				.getItemAttributes("/extension/legend-panel");
		ArrayList<ILegendPanel> legends = new ArrayList<ILegendPanel>();
		for (ItemAttributes<ILegendPanel> itemAttributes : ia) {
			ILegendPanel legendPanel = itemAttributes.getInstance("class");
			legendPanel.initialize(legendContext);
			legends.add(legendPanel);
		}

		return legends.toArray(new ILegendPanel[0]);
	}

	public static ISymbolEditor[] getSymbolPanels() {
		ExtensionPointManager<ISymbolEditor> epm = new ExtensionPointManager<ISymbolEditor>(
				"org.orbisgis.SymbolPanel");
		ArrayList<ItemAttributes<ISymbolEditor>> ia = epm
				.getItemAttributes("/extension/symbol-panel");
		ArrayList<ISymbolEditor> symbols = new ArrayList<ISymbolEditor>();
		for (ItemAttributes<ISymbolEditor> itemAttributes : ia) {
			ISymbolEditor symbolPanel = itemAttributes.getInstance("class");
			symbols.add(symbolPanel);
		}

		return symbols.toArray(new ISymbolEditor[0]);
	}

}
