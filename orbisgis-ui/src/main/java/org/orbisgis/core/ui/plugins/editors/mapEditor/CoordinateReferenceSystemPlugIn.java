/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.orbisgis.core.DataManager;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.crs.ProjectionConfigPanel;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.editor.PlugInEditorListener;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

import org.gdms.data.DataSourceFactory;

public class CoordinateReferenceSystemPlugIn extends AbstractPlugIn{

	private JButton CRSButton;

	public boolean execute(PlugInContext context) throws Exception {
                DataSourceFactory dsf = Services.getService(DataManager.class).getDataSourceFactory();
		final ProjectionConfigPanel projectionPanel = new ProjectionConfigPanel(dsf,
				context.getWorkbenchContext().getWorkbench().getFrame(), true);


		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		CRSButton = new JButton(OrbisGISIcon.TOOLBAR_PROJECTION);
		EditorManager em = Services.getService(EditorManager.class);
		em.addEditorListener(new PlugInEditorListener(this,CRSButton,Names.MAP_TOOLBAR_PROJECTION,
								null,context,false));

	}

	public boolean isEnabled() {
		boolean isVisible = false;
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
		if (editor != null && editor instanceof MapEditorPlugIn && getPlugInContext().getMapEditor()!=null) {
			MapContext mc = (MapContext) editor.getElement().getObject();
			isVisible = mc.getLayerModel().getLayerCount() > 0;
			if(isVisible) {
				try {
					CRSButton.setText( mc.getLayerModel().getLayer(0).getDataSource().getCRS().getName());
				} catch (IllegalStateException e) {
					Services.getErrorManager().error("CRS not found");
				} catch (DriverException e) {
					Services.getErrorManager().error("CRS not found");
				}
			}
		}
		CRSButton.setEnabled(isVisible);
		return isVisible;
	}
}