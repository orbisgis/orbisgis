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
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.StyleType;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.classification.ClassificationMethodException;
import org.orbisgis.core.renderer.se.*;
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
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 * If activated, this plugin will offer an entry in the menu that is shown when
 * right clicking on a layer. If chosen, it tries to build an interface to edit
 * the symbolization of the layer.
 * @author alexis, others
 */
public class EditLegendPlugIn extends AbstractPlugIn {

        @Override
	public boolean execute(PlugInContext context) {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();

		if (selectedResources.length >= 0) {
			for (ILayer resource : selectedResources) {
				execute(mapContext, resource);
			}
		}
		return true;
	}

        @Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TOC_LEGEND_PATH },
				Names.POPUP_TOC_LEGEND_GROUP, false, OrbisGISIcon.EDIT_LEGEND,
				wbContext);
	}

	public void execute(MapContext mapContext, ILayer layer) {
		try {
			Type typ = layer.getDataSource().getMetadata().getFieldType(
					layer.getDataSource().getSpatialFieldIndex());

			LegendsPanel pan = new LegendsPanel();
			// Obtain MapTransform
			EditorManager em = (EditorManager) Services
					.getService(EditorManager.class);
			MapTransform mt;
			// Find the map editor editing mapContext
			IEditor editor = em.getEditors(Names.EDITOR_MAP_ID, mapContext)[0];
			mt = ((MapEditorPlugIn) editor).getMapTransform();
			if (mt == null) {
				JOptionPane.showMessageDialog(null,
						Names.ERROR_EDIT_LEGEND_EDITOR);
			}
                        Style style = layer.getStyle();
                        //In order to be able to cancel all of our modifications,
                        //we produce a copy of our style.
                        JAXBElement<StyleType> jest = style.getJAXBElement();
                        Style copy = new Style(jest, layer);
			ILegendPanel[] legends = EPLegendHelper.getLegendPanels(pan);
			ISymbolEditor[] symbolEditors = EPLegendHelper.getSymbolPanels();
			pan.init(mt, typ, copy, legends, symbolEditors, layer);
			if (UIFactory.showDialog(pan)) {
				try {
                                        layer.setStyle(pan.getStyleWrapper().getStyle());
				} catch (ClassificationMethodException e) {
					ErrorMessages.error(e.getMessage());
				}
			}
		} catch (DriverException e) {
			ErrorMessages.error(Names.ERROR_EDIT_LEGEND_DRIVER, e);
		} catch (SeExceptions.InvalidStyle is){
			ErrorMessages.error(Names.ERROR_EDIT_LEGEND_DRIVER, is);
                }
	}

    @Override
	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] { SelectionAvailability.EQUAL }, 1,
				new LayerAvailability[] { LayerAvailability.VECTORIAL });
	}
}
