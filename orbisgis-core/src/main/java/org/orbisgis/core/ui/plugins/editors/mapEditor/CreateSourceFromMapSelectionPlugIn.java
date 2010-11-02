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

package org.orbisgis.core.ui.plugins.editors.mapEditor;

import java.io.File;

import javax.swing.JButton;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class CreateSourceFromMapSelectionPlugIn extends AbstractPlugIn {

	private JButton btn;

	public CreateSourceFromMapSelectionPlugIn() {
		btn = new JButton(OrbisGISIcon.TABLE_CREATE_SRC_ICON);
	}

	public boolean execute(PlugInContext context) throws Exception {
		TableEditorPlugIn tableEditor = null;
		if ((tableEditor = getPlugInContext().getTableEditor()) != null) {
			TableEditableElement element = (TableEditableElement) tableEditor
					.getElement();
			int[] selectedRows = element.getSelection().getSelectedRows();
			createSourceFromSelection(element.getDataSource(), selectedRows);

		} else {
			IEditor editor = context.getActiveEditor();
			MapContext mc = (MapContext) editor.getElement().getObject();
			ILayer[] layers = mc.getSelectedLayers();// getLayersRecursively();
			for (ILayer layer : layers) {
				createSourceFromSelection(layer.getDataSource(), layer
						.getSelection());
			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		wbContext.getWorkbench().getFrame().getEditionMapToolBar().addPlugIn(
				this, btn, context);

		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Create datasource from selection" },
				"toc.Selection", false, OrbisGISIcon.TABLE_CREATE_SRC_ICON,
				wbContext);
	}

	public static void createSourceFromSelection(DataSource original,
			int[] selectedRows) {
		try {
			DataManager dm = Services.getService(DataManager.class);

			// Create the new source
			DataSourceFactory dsf = dm.getDataSourceFactory();
			File file = dsf.getResultFile();
			DataSourceCreation dsc = new FileSourceCreation(file, original
					.getMetadata());
			dsf.createDataSource(dsc);
			FileSourceDefinition dsd = new FileSourceDefinition(file);

			// Find an unique name to register
			SourceManager sm = dm.getSourceManager();
			int index = -1;
			String newName;
			do {
				index++;
				newName = original.getName() + "_selection_" + index;
			} while (sm.getSource(newName) != null);
			sm.register(newName, dsd);

			// Populate the new source
			DataSource newds = dsf.getDataSource(newName);
			newds.open();
			for (int i = 0; i < selectedRows.length; i++) {
				newds.insertFilledRow(original.getRow(selectedRows[i]));
			}
			newds.commit();
			newds.close();
		} catch (SourceAlreadyExistsException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		} catch (DriverLoadException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		} catch (NoSuchTableException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot create source", e);
		} catch (DataSourceCreationException e) {
			Services.getService(ErrorManager.class).error(
					"Cannot create source", e);
		} catch (NonEditableDataSourceException e) {
			Services.getService(ErrorManager.class).error("Bug", e);
		}
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		isEnabled = getPlugInContext().getMapEditor() != null
				&& getPlugInContext()
						.checkLayerAvailability(
								new SelectionAvailability[] { SelectionAvailability.SUPERIOR },
								0,
								new LayerAvailability[] {
										LayerAvailability.VECTORIAL,
										LayerAvailability.ROW_SELECTED });
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
}
