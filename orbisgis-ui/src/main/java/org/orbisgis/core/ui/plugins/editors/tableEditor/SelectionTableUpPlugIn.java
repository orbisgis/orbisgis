/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

import javax.swing.JButton;

import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.tableEditor.TableEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

public class SelectionTableUpPlugIn extends AbstractPlugIn {

	private JButton btn;

	public SelectionTableUpPlugIn() {
		btn = new JButton(OrbisGISIcon.TABLE_ROW_UP);
		btn.setToolTipText(Names.POPUP_TABLE_UP_PATH1);
	}

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		TableEditorPlugIn te = (TableEditorPlugIn) editor;
		te.moveSelectionUp();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench()
				.getFrame().getTableEditor();
		wbContext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(
				this, btn, context);
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_UP_PATH1 },
				Names.POPUP_TABLE_UP_GROUP, false,
				OrbisGISIcon.TABLE_ROW_UP, wbContext);
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		IEditor editor = null;
		if ((editor = getPlugInContext().getTableEditor()) != null
				&& getSelectedColumn() == -1) {
			isEnabled = ((TableEditableElement) editor.getElement())
					.getSelection().getSelectedRows().length > 0;
		}
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
}
