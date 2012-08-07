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
package org.orbisgis.core.ui.plugins.editors.tableEditor;

import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.FieldNameChooser;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.tableEditor.TableEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class ChangeFieldNamePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		final TableEditableElement element = (TableEditableElement) editor
				.getElement();
		try {
			DataSource dataSource = element.getDataSource();
			FieldNameChooser av = new FieldNameChooser(
					dataSource.getFieldNames(),
					I18N
							.getString("orbisgis.ui.popupmenu.table.changeFieldName.new"),
					"strlength(txt) > 0",
					I18N
							.getString("orbisgis.ui.popupmenu.table.changeFieldName.emptyNameNotAllowed"),
					dataSource.getMetadata().getFieldName(getSelectedColumn()));
			if (UIFactory.showDialog(av)) {
				dataSource.setFieldName(getSelectedColumn(), av.getValue());
			}
		} catch (DriverException e) {
			ErrorMessages.error(ErrorMessages.CannotRenameField, e);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbContext.getWorkbench()
				.getFrame().getTableEditor();
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_CHANGEFIELDNAME_PATH1 },
				Names.POPUP_TABLE_CHANGEFIELDNAME_GROUP, false,
				OrbisGISIcon.TABLE_CHANGEFIELDNAME, wbContext);
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		TableEditorPlugIn tableEditor = null;
		if ((tableEditor = getPlugInContext().getTableEditor()) != null) {
			final TableEditableElement element = (TableEditableElement) tableEditor
					.getElement();
			isEnabled = (getSelectedColumn() != -1) && element.isEditable();
		}
		return isEnabled;
	}
}
