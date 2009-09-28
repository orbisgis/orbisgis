/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.gdms.data.types.GeometryConstraint;
import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LegendDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

public class LegendsPanel extends JPanel implements UIPanel, LegendContext {

	private static final String NO_LEGEND_ID = "no-legend";
	private int geometryType;
	private ArrayList<LegendElement> legends = new ArrayList<LegendElement>();
	private LegendList legendList;
	private ILegendPanel[] availableLegends;
	private JPanel pnlContainer;
	private CardLayout cardLayout;
	private String lastUID = "";
	private GeometryConstraint gc;
	private ILayer layer;
	private JTextField txtMinScale;
	private JTextField txtMaxScale;
	private MapTransform mt;
	private ISymbolEditor[] availableEditors;
	private JButton btnCurrentScaleToMin;
	private JButton btnCurrentScaleToMax;

	public void init(MapTransform mt, GeometryConstraint gc, Legend[] legends,
			ILegendPanel[] availableLegends, ISymbolEditor[] availableEditors,
			ILayer layer) {
		this.mt = mt;
		this.gc = gc;
		this.layer = layer;
		if (gc == null) {
			geometryType = ILegendPanel.ALL;
		} else {
			switch (gc.getGeometryType()) {
			case GeometryConstraint.POINT:
			case GeometryConstraint.MULTI_POINT:
				geometryType = ILegendPanel.POINT;
				break;
			case GeometryConstraint.LINESTRING:
			case GeometryConstraint.MULTI_LINESTRING:
				geometryType = ILegendPanel.LINE;
				break;
			case GeometryConstraint.POLYGON:
			case GeometryConstraint.MULTI_POLYGON:
				geometryType = ILegendPanel.POLYGON;
				break;
			}
		}

		this.availableLegends = availableLegends;
		this.availableEditors = availableEditors;
		initializeComponents();

		for (Legend legend : legends) {
			ILegendPanel panel = getPanel(legend);
			panel.setLegend(legend);
			LegendElement legendElement = new LegendElement(panel
					.getComponent(), panel, getNewId());
			addLegend(legendElement);
		}

		refreshLegendContainer();
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
		this.add(getLegendToolBar(), BorderLayout.NORTH);
		legendList = new LegendList(this);
		this.add(legendList, BorderLayout.WEST);
		JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.add(getLegendContainer(), BorderLayout.CENTER);
		right.add(getScalePanel(), BorderLayout.SOUTH);
		this.add(right, BorderLayout.CENTER);

	}

	private JToolBar getLegendToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.add(new JLabel("Layer : " + layer.getName()));
		toolBar.setFloatable(false);
		return toolBar;
	}

	private Component getScalePanel() {
		JPanel pnlScale = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setVgap(0);
		pnlScale.setLayout(fl);
		JPanel pnlLabels = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setVgap(14);
		pnlLabels.setLayout(flowLayout);
		pnlLabels.add(new JLabel("Min. scale:"));
		pnlLabels.add(new CarriageReturn());
		pnlLabels.add(new JLabel("Max. scale:"));
		pnlScale.add(pnlLabels);

		JPanel pnlTexts = new JPanel();
		pnlTexts.setLayout(new CRFlowLayout());
		KeyAdapter keyAdapter = new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				int selectedIndex = legendList.getSelectedIndex();
				if (selectedIndex != -1) {
					LegendElement legendElement = legends.get(selectedIndex);
					Legend legend = legendElement.getLegend();
					String minScale = txtMinScale.getText();
					if (minScale.trim().length() != 0) {
						try {
							int min = Integer.parseInt(minScale);
							legend.setMinScale(min);
						} catch (NumberFormatException e1) {
						}
					} else {
						legend.setMinScale(Integer.MIN_VALUE);
					}
					String maxScale = txtMaxScale.getText();
					if (maxScale.trim().length() != 0) {
						try {
							int max = Integer.parseInt(maxScale);
							legend.setMaxScale(max);
						} catch (NumberFormatException e1) {
						}
					} else {
						legend.setMaxScale(Integer.MAX_VALUE);
					}
				} else {
					Services.getErrorManager().error(
							"There is no selected legend, "
									+ "cannot set scale.");
				}
			}

		};
		txtMinScale = new JTextField(10);
		txtMinScale.addKeyListener(keyAdapter);
		txtMaxScale = new JTextField(10);
		txtMaxScale.addKeyListener(keyAdapter);
		pnlTexts.add(txtMinScale);
		btnCurrentScaleToMin = new JButton("Current scale");
		btnCurrentScaleToMin.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				txtMinScale.setText(Integer
						.toString((int) getCurrentMapTransform()
								.getScaleDenominator()));
			}

		});
		pnlTexts.add(btnCurrentScaleToMin);
		pnlTexts.add(new CarriageReturn());
		pnlTexts.add(txtMaxScale);
		btnCurrentScaleToMax = new JButton("Current scale");
		btnCurrentScaleToMax.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				txtMaxScale.setText(Integer
						.toString((int) getCurrentMapTransform()
								.getScaleDenominator()));
			}

		});
		pnlTexts.add(btnCurrentScaleToMax);
		pnlScale.add(pnlTexts);

		pnlScale.setPreferredSize(new Dimension(200, 100));
		pnlScale.setBorder(BorderFactory.createTitledBorder("Scale"));
		return pnlScale;
	}

	private JPanel getLegendContainer() {
		pnlContainer = new JPanel();
		pnlContainer.setPreferredSize(new Dimension(550, 400));
		cardLayout = new CardLayout();
		pnlContainer.setLayout(cardLayout);
		pnlContainer.add(new JLabel("Add or select a legend on the left"),
				NO_LEGEND_ID);
		return pnlContainer;
	}

	private ILegendPanel getPanel(Legend legend) {
		for (ILegendPanel panel : availableLegends) {
			if (panel.getLegend().getLegendTypeId().equals(
					legend.getLegendTypeId())) {
				return newInstance(panel);
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

	public ILegendPanel[] getAvailableLegends() {
		return availableLegends;
	}

	public int getGeometryType() {
		return geometryType;
	}

	public boolean isLine() {
		return (geometryType & ILegendPanel.LINE) > 0;
	}

	public boolean isPoint() {
		return (geometryType & ILegendPanel.POINT) > 0;
	}

	public boolean isPolygon() {
		return (geometryType & ILegendPanel.POLYGON) > 0;
	}

	private void refresh() {
		legendList.refresh();
		refreshLegendContainer();
	}

	private void refreshLegendContainer() {
		int index = legendList.getSelectedIndex();
		if ((index >= 0) && (index <= legends.size() - 1)) {
			cardLayout.show(pnlContainer, legends.get(index).getId());
			Legend legend = legends.get(index).getLegend();
			int minScale = legend.getMinScale();
			if (minScale != Integer.MIN_VALUE) {
				txtMinScale.setText(minScale + "");
			} else {
				txtMinScale.setText("");
			}
			int maxScale = legend.getMaxScale();
			if (maxScale != Integer.MAX_VALUE) {
				txtMaxScale.setText(maxScale + "");
			} else {
				txtMaxScale.setText("");
			}
		} else {
			cardLayout.show(pnlContainer, NO_LEGEND_ID);
		}

		boolean scaleEnabled = legendList.getSelectedIndex() != -1;
		txtMinScale.setEnabled(scaleEnabled);
		btnCurrentScaleToMin.setEnabled(scaleEnabled);
		txtMaxScale.setEnabled(scaleEnabled);
		btnCurrentScaleToMax.setEnabled(scaleEnabled);
	}

	public void legendRemoved(int index) {
		legends.remove(index);
		refresh();
	}

	public void legendAdded(ILegendPanel panel) {
		panel = newInstance(panel);
		LegendElement le = new LegendElement(panel.getComponent(), panel,
				getNewId());
		addLegend(le);
	}

	private ILegendPanel newInstance(ILegendPanel panel) {
		ILegendPanel ret = panel.newInstance();
		ret.initialize(this);

		return ret;
	}

	private void addLegend(LegendElement le) {
		legends.add(le);
		pnlContainer.add(le.getComponent(), le.getId());
		// le.getLegendPanel().initialize(this);
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

		for (LegendElement legendElement : legends) {
			String panelError = legendElement.getLegendPanel().validateInput();
			if (panelError != null) {
				return panelError;
			}
		}

		String error = validateScale(txtMinScale);
		if (error != null) {
			return error;
		}

		error = validateScale(txtMaxScale);
		if (error != null) {
			return error;
		}

		return null;
	}

	private String validateScale(JTextField txt) {
		String minScale = txt.getText();
		if (minScale.trim().length() != 0) {
			try {
				Integer.parseInt(minScale);
			} catch (NumberFormatException e) {
				return "Min. scale is not a valid number";
			}
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

	public MapTransform getCurrentMapTransform() {
		return mt;
	}

	public ISymbolEditor[] getAvailableSymbolEditors() {
		return availableEditors;
	}

}
