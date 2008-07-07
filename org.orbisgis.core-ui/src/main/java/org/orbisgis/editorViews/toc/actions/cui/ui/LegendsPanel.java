package org.orbisgis.editorViews.toc.actions.cui.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.gui.LegendContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LegendDecorator;
import org.orbisgis.renderer.legend.Legend;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class LegendsPanel extends JPanel implements UIPanel, LegendContext {

	private int geometryType;
	private ArrayList<LegendElement> legends = new ArrayList<LegendElement>();
	private LegendList legendList;
	private ILegendPanelUI[] availableLegends;
	private JPanel pnlContainer;
	private CardLayout cardLayout;
	private String lastUID = "";
	private GeometryConstraint gc;
	private ILayer layer;

	public void init(GeometryConstraint gc, Legend[] legends,
			ILegendPanelUI[] availableLegends, ILayer layer) {
		this.gc = gc;
		this.layer = layer;
		if (gc == null) {
			geometryType = ILegendPanelUI.ALL;
		} else {
			switch (gc.getGeometryType()) {
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				geometryType = ILegendPanelUI.POINT;
				break;
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				geometryType = ILegendPanelUI.LINE;
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				geometryType = ILegendPanelUI.POLYGON;
				break;
			}
		}

		this.availableLegends = availableLegends;
		initializeComponents();

		for (Legend legend : legends) {
			ILegendPanelUI panel = getPanel(legend);
			panel.setLegend(legend);
			LegendElement legendElement = new LegendElement(panel
					.getComponent(), panel, getNewId());
			addLegend(legendElement);
		}
	}

	private String getNewId() {
		String name = "gdms" + System.currentTimeMillis();

		while (name.equals(lastUID)) {
			name = "" + System.currentTimeMillis();
		}

		lastUID = name;
		return name;
	}

	private void initializeComponents() {
		this.setLayout(new BorderLayout());
		legendList = new LegendList(this);
		this.add(legendList, BorderLayout.WEST);
		JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.add(getLegendContainer(), BorderLayout.CENTER);
		right.add(getScalePanel(), BorderLayout.SOUTH);
		this.add(right, BorderLayout.CENTER);
	}

	private Component getScalePanel() {
		// TODO Auto-generated method stub
		return new JLabel("Scale panel");
	}

	private JPanel getLegendContainer() {
		pnlContainer = new JPanel();
		cardLayout = new CardLayout();
		pnlContainer.setLayout(cardLayout);
		return pnlContainer;
	}

	private ILegendPanelUI getPanel(Legend legend) {
		for (ILegendPanelUI panel : availableLegends) {
			if (panel.getLegend().getLegendTypeName().equals(
					legend.getLegendTypeName())) {
				return panel.newInstance(this);
			}
		}

		return new NoPanel(legend);
	}

	public Legend[] getLegends() {
		Legend[] ret = new Legend[legends.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = legends.get(i).getLegendPanel().getLegend();
		}
		return ret;
	}

	public ILegendPanelUI[] getAvailableLegends() {
		return availableLegends;
	}

	public int getGeometryType() {
		return geometryType;
	}

	public boolean isLine() {
		return (geometryType & ILegendPanelUI.LINE) > 0;
	}

	public boolean isPoint() {
		return (geometryType & ILegendPanelUI.POINT) > 0;
	}

	public boolean isPolygon() {
		return (geometryType & ILegendPanelUI.POLYGON) > 0;
	}

	private void refresh() {
		legendList.refresh();
		refreshLegendContainer();
	}

	private void refreshLegendContainer() {
		int index = legendList.getSelectedIndex();
		if (index != -1) {
			cardLayout.show(pnlContainer, legends.get(index).getId());
		}
	}

	public void legendRemoved(int index) {
		legends.remove(index);
		refresh();
	}

	public void legendAdded(ILegendPanelUI panel) {
		panel = panel.newInstance(this);
		LegendElement le = new LegendElement(panel.getComponent(), panel,
				getNewId());
		addLegend(le);
	}

	private void addLegend(LegendElement le) {
		legends.add(le);
		pnlContainer.add(le.getComponent(), le.getId());
		le.getLegendPanel().setLegendContext(this);
		le.getLegendPanel().setLegend(getLegend(le));
		refresh();
	}

	private Legend getLegend(LegendElement le) {
		Legend ret = le.getLegend();
		if (ret instanceof LegendDecorator) {
			ret = ((LegendDecorator) ret).getLegend();
		}

		return ret;
	}

	public void legendRenamed(int idx, String newName) {
		legends.get(idx).getLegend().setName(newName);
		refresh();
	}

	public void legendMovedDown(int idx) {
		LegendElement aux = legends.get(idx);
		legends.set(idx, legends.get(idx + 1));
		legends.set(idx + 1, aux);
		refresh();
	}

	public void legendSelected(int selectedIndex) {
		refreshLegendContainer();
	}

	public void legendMovedUp(int idx) {
		LegendElement aux = legends.get(idx);
		legends.set(idx, legends.get(idx - 1));
		legends.set(idx - 1, aux);
		refresh();
	}

	public Component getComponent() {
		return this;
	}

	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

	public String getTitle() {
		return "Legend edition";
	}

	public String initialize() {
		return null;
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		if (legends.size() == 0) {
			return "You must create almost one legend";
		}
		return null;
	}

	public String[] getLegendsNames() {
		String[] ret = new String[legends.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = legends.get(i).getLegend().getName();
		}

		return ret;
	}

	public GeometryConstraint getGeometryConstraint() {
		return gc;
	}

	public ILayer getLayer() {
		return layer;
	}

}
