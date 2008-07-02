package org.orbisgis.editorViews.toc.actions.cui.ui;

import java.awt.Component;

import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.renderer.legend.Legend;

public class LegendElement {

	private Legend legend;
	private Component component;
	private ILegendPanelUI legendPanel;
	private String id;

	public LegendElement(Legend legend, Component component,
			ILegendPanelUI legendPanel, String id) {
		this.legend = legend;
		this.component = component;
		this.legendPanel = legendPanel;
		this.id = id;
	}

	public Legend getLegend() {
		return legend;
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public ILegendPanelUI getLegendPanel() {
		return legendPanel;
	}

	public void setLegendPanel(ILegendPanelUI legendPanel) {
		this.legendPanel = legendPanel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
