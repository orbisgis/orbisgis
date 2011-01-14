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
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 * 
 * Copyright (C) 2011 Erwan BOCHER,Alexis GUEGANNO, Antoine GOURLAY
 * 
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
 * info_at_orbisgis.org
 */

package org.orbisgis.core.ui.plugins.editors.tableEditor;

import javax.swing.JButton;

import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class ClearTableSelectionPlugIn extends AbstractPlugIn {

	private JButton btn;

	public ClearTableSelectionPlugIn() {
		btn = new JButton(OrbisGISIcon.EDIT_CLEAR);
		btn.setToolTipText(I18N
				.getText("orbisgis.ui.popupmenu.table.clearSelection"));
	}

	public boolean execute(final PlugInContext context) throws Exception {
		IEditor editor = context.getActiveEditor();
		TableEditableElement element = (TableEditableElement) editor
				.getElement();
		if (element.getMapContext() != null) {
			element.getMapContext().checkSelectionRefresh(new int[0],
					element.getSelection().getSelectedRows(),
					element.getDataSource());
		}
		element.getSelection().clearSelection();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(
				this, btn, context);
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		TableEditorPlugIn tableEditor = null;
		if ((tableEditor = getPlugInContext().getTableEditor()) != null
				&& getSelectedColumn() == -1) {
			TableEditableElement element = (TableEditableElement) tableEditor
					.getElement();
			isEnabled = element.getSelection().getSelectedRows().length > 0;
		}
		btn.setEnabled(isEnabled);
		return isEnabled;
	}
}
