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
 * Copyright (C) 2009 Erwan BOCHER, Pierre-yves FADET
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
 *    Pierre-Yves.Fadet_at_ec-nantes.fr
 *    thomas.leduc _at_ cerma.archi.fr
 */

package org.orbisgis.plugins.core.ui.editors.TableEditor;

import java.io.File;
import java.util.Observable;

import javax.swing.ImageIcon;
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
import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.table.TableEditableElement;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.plugins.errorManager.ErrorManager;
import org.orbisgis.plugins.images.IconLoader;

public class CreateSourceFromSelectionPlugIn extends AbstractPlugIn {

	private JButton btn;

	public CreateSourceFromSelectionPlugIn() {
		btn = new JButton(getIcon());
	}

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		DataSource original = element.getDataSource();
		int[] selectedRows = element.getSelection().getSelectedRows();
		createSourceFromSelection(original, selectedRows);
		return true;
	}

	@Override
	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		wbContext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(
				this, btn);

		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { "Create datasource from selection" },
				"toc.Selection", false, getIcon("table_go.png"), wbContext);
	}

	@Override
	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
	}

	public static void createSourceFromSelection(DataSource original,
			int[] selectedRows) {
		try {
			DataManager dm = Services.getService(DataManager.class);

			// Create the new source
			DataSourceFactory dsf = dm.getDSF();
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
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if ("Table".equals(em.getEditorId(editor)) && editor != null) {
			TableEditableElement element = (TableEditableElement) editor
					.getElement();
			return element.getSelection().getSelectedRows().length > 0;
		}
		return false;
	}

	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isVectorial() && layer.getSelection().length > 0;
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount > 0;
	}

	public static ImageIcon getIcon() {
		return IconLoader.getIcon(Names.TABLE_CREATE_SRC_ICON);
	}
}
