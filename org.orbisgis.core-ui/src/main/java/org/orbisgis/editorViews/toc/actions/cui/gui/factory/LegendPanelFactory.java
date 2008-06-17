package org.orbisgis.editorViews.toc.actions.cui.gui.factory;

import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelIntervalClassifiedLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelLabelLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelProportionalLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelUniqueSymbolLegend;
import org.orbisgis.editorViews.toc.actions.cui.gui.JPanelUniqueValueLegend;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;

public class LegendPanelFactory {
	
	public static final int UNIQUE_SYMBOL_LEGEND=1;
	public static final int UNIQUE_VALUE_LEGEND=2;
	public static final int LABEL_LEGEND=3;
	public static final int PROPORTIONAL_LEGEND=4;
	public static final int INTERVAL_LEGEND=5;
	
	public static ILegendPanelUI createPanel(int type, Legend leg, int constraint, ILayer layer, boolean showCollection){
		ILegendPanelUI legendPanel=null;
		switch(type){
		case UNIQUE_SYMBOL_LEGEND:
			legendPanel = new JPanelUniqueSymbolLegend(leg, constraint, showCollection);
			break;
		case UNIQUE_VALUE_LEGEND:
			legendPanel = new JPanelUniqueValueLegend(leg, constraint, layer);
			break;
		case LABEL_LEGEND:
			legendPanel = new JPanelLabelLegend(leg, constraint, layer);
			break;
		case INTERVAL_LEGEND:
			legendPanel = new JPanelIntervalClassifiedLegend(leg, constraint, layer);
			break;
		case PROPORTIONAL_LEGEND:
			legendPanel = new JPanelProportionalLegend(leg, constraint, layer);
			break;
		
		}
		return legendPanel;
			
	}
	
	
}
