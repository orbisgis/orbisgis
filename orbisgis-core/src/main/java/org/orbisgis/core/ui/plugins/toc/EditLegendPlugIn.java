/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.plugins.toc;

import javax.swing.JOptionPane;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.classification.ClassificationMethodException;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendsPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.EPLegendHelper;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ISymbolEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class EditLegendPlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();

		if (selectedResources.length == 0) {
			execute(mapContext, null);
		} else {
			for (ILayer resource : selectedResources) {
				execute(mapContext, resource);
			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LEGEND_PATH },
				Names.POPUP_TOC_LEGEND_GROUP, false,
				OrbisGISIcon.EDIT_LEGEND, wbContext);
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			Type typ = layer.getDataSource().getMetadata().getFieldType(
					layer.getDataSource().getSpatialFieldIndex());
			GeometryConstraint cons = (GeometryConstraint) typ
					.getConstraint(Constraint.GEOMETRY_TYPE);

			LegendsPanel pan = new LegendsPanel();
			// Obtain MapTransform
			EditorManager em = (EditorManager) Services
					.getService(EditorManager.class);
			MapTransform mt = null;
			// Find the map editor editing mapContext
			IEditor editor = em.getEditors(Names.EDITOR_MAP_ID, mapContext)[0];
			mt = ((MapEditorPlugIn) editor).getMapTransform();
			if (mt == null) {
				JOptionPane.showMessageDialog(null,
						Names.ERROR_EDIT_LEGEND_EDITOR);
			}

			Legend[] legend = layer.getVectorLegend();
			Legend[] copies = new Legend[legend.length];
			for (int i = 0; i < copies.length; i++) {
				Object obj = legend[i].getJAXBObject();
				Legend copy = legend[i].newInstance();
				copy.setJAXBObject(obj);
				copies[i] = copy;
				copies[i].setVisible(legend[i].isVisible());
			}
			ILegendPanel[] legends = EPLegendHelper.getLegendPanels(pan);
			ISymbolEditor[] symbolEditors = EPLegendHelper.getSymbolPanels();
			pan.init(mt, cons, copies, legends, symbolEditors, layer);
			if (UIFactory.showDialog(pan)) {
				try {
					layer.setLegend(pan.getLegends());
				} catch (DriverException e) {
					Services.getErrorManager().error(
							Names.ERROR_EDIT_LEGEND_DRIVER, e);

				} catch (ClassificationMethodException e) {
					Services.getErrorManager().error(e.getMessage());
				}
			}
		} catch (DriverException e) {
			Services.getErrorManager().error(Names.ERROR_EDIT_LEGEND_LAYER, e);
		}
	}

	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] {SelectionAvailability.EQUAL},
				1,
				new LayerAvailability[] {LayerAvailability.VECTORIAL});
	}
}
