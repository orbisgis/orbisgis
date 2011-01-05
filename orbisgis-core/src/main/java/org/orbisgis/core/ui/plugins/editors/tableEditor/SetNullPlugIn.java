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

import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableComponent;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.progress.IProgressMonitor;

public class SetNullPlugIn extends AbstractPlugIn {

	public boolean execute(final PlugInContext context) throws Exception {

		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {

				IEditor editor = context.getActiveEditor();
				TableEditableElement element = (TableEditableElement) editor
						.getElement();
				try {
					element.getDataSource().setFieldValue(
							((TableComponent) editor.getView().getComponent())
									.getTable().rowAtPoint(
											getEvent().getPoint()),
							((TableComponent) editor.getView().getComponent())
									.getTable().columnAtPoint(
											getEvent().getPoint()),
							ValueFactory.createNullValue());
				} catch (DriverException e) {
					ErrorMessages.error(ErrorMessages.CannotSetNullValue, e);
				}
			}

			@Override
			public String getTaskName() {
				return "Set to null";
			}
		});
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_SETNULL_PATH1 },
				Names.POPUP_TABLE_SETNULL_GROUP, false, wbContext);
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		IEditor editor = null;
		int row = -1;
		int column = -1;

		if ((editor = getPlugInContext().getTableEditor()) != null
				&& getSelectedColumn() == -1 && getEvent() != null) {

			editor = editor;
			TableEditableElement element = (TableEditableElement) editor
					.getElement();
			if (element.getSelection().getSelectedRows().length > 0) {

				row = ((TableComponent) editor.getView().getComponent())
						.getTable().rowAtPoint(getEvent().getPoint());
				column = ((TableComponent) editor.getView().getComponent())
						.getTable().columnAtPoint(getEvent().getPoint());
				if (row != -1 && column != -1) {
					try {
						isEnabled = element.isEditable()
								&& !element.getDataSource().isNull(row, column);
					} catch (DriverException e) {
						ErrorMessages.error(ErrorMessages.CannotTestNullValue, e);
						return false;
					}
				}
			}

		}
		return isEnabled;
	}
}
