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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryTypeConstraint;
import org.gdms.data.types.Type;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.IRulePanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legends.GeometryProperties;
import org.orbisgis.core.ui.editorViews.toc.wrapper.RuleWrapper;
import org.orbisgis.core.ui.editorViews.toc.wrapper.StyleWrapper;
import org.orbisgis.legend.Legend;
import org.orbisgis.utils.I18N;

/**
 * This {@code Panel} contains all the needed informations to build an UI that
 * will let the user edit the legends. It is built with the following 
 * properties :</p>
 * <ul><li>Legends are displayed in the {@code LegendList}.</li>
 * <li>An inner list of available legends. It may be initialized using
 * {@code EPLegendHelper. It is used to validate a given {@code Legend}, in 
 * order to determine if it can be edited or not.</li>
 * <li>A {@code CardLayout} that is used to switch fast between the {@code
 * Legend} instances stored in {@code legends}</li>
 * <li>Two text fields : one for the min scale, the other for the max
 * scale.</li>
 * <li>Two buttons that are used to fastly set the min and/or max scales to the
 * current one.</li>
 * <li>A {@code MapTransform} that represents the current state of the map<li>
 * <li>A {@code Type} instance (should be the type of the {@code DataSource}
 * associated to the layer associated to the legend we want to edit.</li>
 * </ul>
 * @author alexis, others...
 */
public class LegendsPanel extends JPanel implements UIPanel, LegendContext {

	private static final String NO_LEGEND_ID = "no-legend"; 
	private int geometryType;
	private ArrayList<ILegendPanel> legends = new ArrayList<ILegendPanel>();
//	private LegendList legendList;
        private LegendTree legendTree;
	private ILegendPanel[] availableLegends;
	private JPanel pnlContainer;
	private CardLayout cardLayout;
	private String lastUID = ""; 
	private Type gc;
	private ILayer layer;
	private MapTransform mt;
        private StyleWrapper style;

	public void init(MapTransform mt, Type gc, StyleWrapper sw, ILegendPanel[] availableLegends,
                        ISymbolEditor[] availableEditors, ILayer layer) {
                style = sw;
		this.mt = mt;
		this.gc = gc;
		this.layer = layer;
		if (gc == null) {
			geometryType = GeometryProperties.ALL;
		} else {
			switch (gc.getTypeCode()) {
			case Type.POINT:
			case Type.MULTIPOINT:
				geometryType = GeometryProperties.POINT;
				break;
			case Type.LINESTRING:
			case Type.MULTILINESTRING:
				geometryType = GeometryProperties.LINE;
				break;
			case Type.POLYGON:
			case Type.MULTIPOLYGON:
				geometryType = GeometryProperties.POLYGON;
				break;
			case Type.GEOMETRYCOLLECTION:
			case Type.GEOMETRY:
				geometryType = GeometryProperties.ALL;
				break;
			}
		}

		this.availableLegends = availableLegends;
		initializeComponents();

                for (int i = 0; i<style.getSize(); i++){
                        RuleWrapper r = style.getRuleWrapper(i);
                        for (int j = 0; j<r.getSize();j++) {
                                //We get a panel for this legend, by finding one eligible
                                // in the availabe ones. setLegend is called by getPanel,
                                //so it's not necessary to do it here.
                                Legend legend = r.getLegend(j);
                                ILegendPanel panel = getPanel(legend);
                                panel.setId(getNewId());
                                addLegend(panel);
                        }
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
		legendTree = new LegendTree(this);
		this.add(legendTree, BorderLayout.WEST);
		JPanel right = new JPanel();
		right.setLayout(new BorderLayout());
		right.add(getLegendContainer(), BorderLayout.CENTER);
		this.add(right, BorderLayout.CENTER);

	}

	private JToolBar getLegendToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.layer") + layer.getName()));
		toolBar.setFloatable(false);
		return toolBar;
	}

	private JPanel getLegendContainer() {
		pnlContainer = new JPanel();
		pnlContainer.setPreferredSize(new Dimension(600, 400));
		cardLayout = new CardLayout();
		pnlContainer.setLayout(cardLayout);
		pnlContainer.add(new JLabel(
			I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.addOrSelectLegendLeft")),
				NO_LEGEND_ID);
		return pnlContainer;
	}

	private ILegendPanel getPanel(Legend legend) {
		for (ILegendPanel panel : availableLegends) {
			if (panel.getLegend().getLegendTypeId().equals(
					legend.getLegendTypeId())) {
                                ILegendPanel ilp = (ILegendPanel)newInstance(panel);
                                ilp.setLegend(legend);
				return ilp;
			}
		}

		return new NoPanel(legend);
	}

	public Legend[] getLegends() {
		Legend[] ret = new Legend[legends.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = legends.get(i).getLegend();
		}
		return ret;
	}

	public ILegendPanel[] getAvailableLegends() {
		return availableLegends;
	}

        @Override
	public int getGeometryType() {
		return geometryType;
	}

        @Override
	public boolean isLine() {
		return (geometryType & GeometryProperties.LINE) > 0;
	}

        @Override
	public boolean isPoint() {
		return (geometryType & GeometryProperties.POINT) > 0;
	}

        @Override
	public boolean isPolygon() {
		return (geometryType & GeometryProperties.POLYGON) > 0;
	}

	private void refresh() {
		legendTree.refresh();
		refreshLegendContainer();
	}

	private void refreshLegendContainer() {
                //We need to retrieve the currently selected legend in the tree,
                //then find its id, and finally use it to show the panel.
                Legend selected = legendTree.getSelectedLegend();
		if (selected != null) {
			cardLayout.show(pnlContainer, legends.get(0).getId());
		} else {
			cardLayout.show(pnlContainer, NO_LEGEND_ID);
		}
	}

	public void legendRemoved(int index) {
		legends.remove(index);
		refresh();
	}

	public void legendAdded(ILegendPanel panel) {
                //We can cast safely as we KNOW we are already dealing with a LegendPanel.
		panel = (ILegendPanel)newInstance(panel);
                panel.setId(getNewId());
		addLegend(panel);
	}

	private IRulePanel newInstance(IRulePanel panel) {
		IRulePanel ret = panel.newInstance();
		ret.initialize(this);

		return ret;
	}

	private void addLegend(ILegendPanel le) {
		legends.add(le);
		pnlContainer.add(le.getComponent(), le.getId());
		le.initialize(this);
		refresh();
	}

	private Legend getLegend(ILegendPanel le) {
		Legend ret = le.getLegend();
//		if (ret instanceof LegendDecorator) {
//			ret = ((LegendDecorator) ret).getLegend();
//		}

		return ret;
	}

	public void legendRenamed(int idx, String newName) {
		legends.get(idx).getLegend().setName(newName);
		refresh();
	}

	public void legendMovedDown(int idx) {
//		LegendElement aux = legends.get(idx);
//		legends.set(idx, legends.get(idx + 1));
//		legends.set(idx + 1, aux);
//		refresh();
	}

	public void legendSelected(int selectedIndex) {
		refreshLegendContainer();
	}

	public void legendMovedUp(int idx) {
//		LegendElement aux = legends.get(idx);
//		legends.set(idx, legends.get(idx - 1));
//		legends.set(idx - 1, aux);
//		refresh();
	}

        @Override
	public Component getComponent() {
		return this;
	}

        @Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

        @Override
	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

        @Override
	public String getTitle() {
		return I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.legendEdition"); 
	}

        @Override
	public String initialize() {
		return null;
	}

        @Override
	public String postProcess() {
		return null;
	}

        @Override
	public String validateInput() {
		if (legends.isEmpty()) {
			return I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.mustCreateAlmostOneLegend"); 
		}

		for (ILegendPanel legendElement : legends) {
			String panelError = legendElement.validateInput();
			if (panelError != null) {
				return panelError;
			}
		}

//		String error = validateScale(txtMinScale);
//		if (error != null) {
//			return error;
//		}
//
//		error = validateScale(txtMaxScale);
//		if (error != null) {
//			return error;
//		}

		return null;
	}

	private String validateScale(JTextField txt) {
		String minScale = txt.getText();
		if (minScale.trim().length() != 0) {
			try {
				Integer.parseInt(minScale);
			} catch (NumberFormatException e) {
				return I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.minScaleIsNotAValidNumber"); 
			}
		}

		return null;
	}

	public List<String> getLegendsNames() {
		List<String> ret = new ArrayList<String>(legends.size());
		for (ILegendPanel le : legends) {
			ret.add(le.getLegend().getName());
		}
		return ret;
	}

        @Override
	public GeometryTypeConstraint getGeometryTypeConstraint() {
		return (GeometryTypeConstraint) gc.getConstraint(Constraint.GEOMETRY_TYPE);
	}

        @Override
	public ILayer getLayer() {
		return layer;
	}

        @Override
	public MapTransform getCurrentMapTransform() {
		return mt;
	}

        @Override
	public ISymbolEditor[] getAvailableSymbolEditors() {
		return null;
	}

        public StyleWrapper getStyleWrapper() {
                return style;
        }

}
