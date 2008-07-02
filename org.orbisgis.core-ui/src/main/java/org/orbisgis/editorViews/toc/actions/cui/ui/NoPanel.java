package org.orbisgis.editorViews.toc.actions.cui.ui;

import java.awt.Component;

import javax.swing.JLabel;

import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.gui.LegendContext;
import org.orbisgis.renderer.legend.Legend;

public class NoPanel implements ILegendPanelUI {

	private Legend legend;

	public NoPanel(Legend legend) {
		this.legend = legend;
	}

	public boolean acceptsGeometryType(int geometryType) {
		return true;
	}

	public Component getComponent() {
		return new JLabel("No suitable editor for this legend");
	}

	public Legend getLegend() {
		return legend;
	}

	public String getLegendTypeName() {
		throw new RuntimeException("bug!");
	}

	public ILegendPanelUI newInstance(LegendContext legendContext) {
		throw new RuntimeException("bug!");
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public void setLegendContext(LegendContext lc) {

	}

}
