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

package org.orbisgis.core.ui.plugins.editors.tableEditor;

import java.text.ParseException;

import javax.swing.JButton;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.components.sif.AskValidRow;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class NewRowTablePlugIn extends AbstractPlugIn {

	private JButton btn;

	public NewRowTablePlugIn() {
		btn = new JButton(OrbisGISIcon.TABLE_ADDROW);
	}

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		addRow(element);
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		wbContext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(
				this, btn, context);
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_ADDROW },
				Names.POPUP_TABLE_ADDROW_GROUP, false,
				OrbisGISIcon.TABLE_ADDROW, wbContext);
	}

	public static void addRow(TableEditableElement element) {
		DataSource ds = element.getDataSource();
		try {
			AskValidRow rowInput = new AskValidRow("Introduce row values", ds);
			if (UIFactory.showDialog(rowInput)) {
				ds.insertFilledRow(rowInput.getRow());
			}
		} catch (IllegalArgumentException e) {
			Services.getService(ErrorManager.class).error("Cannot add row", e);
		} catch (IncompatibleTypesException e) {
			Services.getService(ErrorManager.class).error(
					"Incompatible types at insertion", e);
		} catch (DriverException e) {
			Services.getService(ErrorManager.class).error("Data access error",
					e);
		} catch (ParseException e) {
			Services.getService(ErrorManager.class).error("Unrecognized input",
					e);
		}
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		TableEditorPlugIn tableEditor = null;
		if((tableEditor=getPlugInContext().getTableEditor()) != null
				&& getSelectedColumn()==-1){
			TableEditableElement element = (TableEditableElement) tableEditor
					.getElement();
			if(element.getSelection().getSelectedRows().length == 1) {
				if( element.isEditable() ) {
					isEnabled = true;
				}
				else if( element.getMapContext() == null ) {
					isEnabled = element.getDataSource().isEditable();
				}
			}
		}
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
}
