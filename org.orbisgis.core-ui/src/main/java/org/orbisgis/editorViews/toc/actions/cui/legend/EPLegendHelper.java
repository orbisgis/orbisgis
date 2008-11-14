package org.orbisgis.editorViews.toc.actions.cui.legend;

import java.util.ArrayList;

import org.orbisgis.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.editorViews.toc.actions.cui.extensions.ILegendPanelUI;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPLegendHelper {

	public static ILegendPanelUI[] getLegends(LegendContext legendContext) {
		ExtensionPointManager<ILegendPanelUI> epm = new ExtensionPointManager<ILegendPanelUI>(
				"org.orbisgis.LegendPanel");
		ArrayList<ItemAttributes<ILegendPanelUI>> ia = epm
				.getItemAttributes("/extension/legend-panel");
		ArrayList<ILegendPanelUI> legends = new ArrayList<ILegendPanelUI>();
		for (ItemAttributes<ILegendPanelUI> itemAttributes : ia) {
			legends.add(itemAttributes.getInstance("class"));
		}

		return legends.toArray(new ILegendPanelUI[0]);
	}

}
