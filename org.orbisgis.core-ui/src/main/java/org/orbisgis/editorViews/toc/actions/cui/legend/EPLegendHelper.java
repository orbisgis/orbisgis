package org.orbisgis.editorViews.toc.actions.cui.legend;

import java.util.ArrayList;

import org.orbisgis.editorViews.toc.actions.cui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.ISymbolEditor;
import org.orbisgis.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPLegendHelper {

	public static ILegendPanelUI[] getLegendPanels(LegendContext legendContext) {
		ExtensionPointManager<ILegendPanelUI> epm = new ExtensionPointManager<ILegendPanelUI>(
				"org.orbisgis.LegendPanel");
		ArrayList<ItemAttributes<ILegendPanelUI>> ia = epm
				.getItemAttributes("/extension/legend-panel");
		ArrayList<ILegendPanelUI> legends = new ArrayList<ILegendPanelUI>();
		for (ItemAttributes<ILegendPanelUI> itemAttributes : ia) {
			ILegendPanelUI legendPanel = itemAttributes.getInstance("class");
			legendPanel.initialize(legendContext);
			legends.add(legendPanel);
		}

		return legends.toArray(new ILegendPanelUI[0]);
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
