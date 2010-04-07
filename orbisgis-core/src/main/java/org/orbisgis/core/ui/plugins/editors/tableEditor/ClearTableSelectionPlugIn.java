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

package org.orbisgis.core.ui.plugins.editors.tableEditor;

import java.awt.event.MouseEvent;
import java.util.Observable;

import javax.swing.JButton;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundJob;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.table.TableEditableElement;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.progress.IProgressMonitor;

public class ClearTableSelectionPlugIn extends AbstractPlugIn {

	private JButton btn;
	private boolean menuItemIsVisible;

	public ClearTableSelectionPlugIn() {
		btn = new JButton(getIcon(IconNames.EDIT_CLEAR));
	}

	public boolean execute(final PlugInContext context) throws Exception {
		BackgroundManager bm = Services.getService(BackgroundManager.class);
		bm.backgroundOperation(new BackgroundJob() {

			@Override
			public void run(IProgressMonitor pm) {
		
				IEditor editor = context.getActiveEditor();
				TableEditableElement element = (TableEditableElement) editor
						.getElement();
				element.getSelection().clearSelection();
			}

			@Override
			public String getTaskName() {				
				return "Clear selection";
			}
		});
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		WorkbenchFrame frame = (WorkbenchFrame) wbcontext.getWorkbench()
				.getFrame().getTableEditor();
		wbcontext.getWorkbench().getFrame().getEditionTableToolBar().addPlugIn(
				this, btn, context);
		context.getFeatureInstaller().addPopupMenuItem(frame, this,
				new String[] { Names.POPUP_TABLE_CLEAR_PATH1 },
				Names.POPUP_TABLE_CLEAR_GROUP, false,
				getIcon(IconNames.EDIT_CLEAR), wbcontext);
	}

	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
		btn.setVisible(true);
		menuItemIsVisible = (arg instanceof MouseEvent) ? true : false;
	}

	public boolean isEnabled() {
		TableEditorPlugIn tableEditor = null;
		if((tableEditor=getPlugInContext().getTableEditor()) != null){
			TableEditableElement element = (TableEditableElement) tableEditor
					.getElement();
			return element.getSelection().getSelectedRows().length > 0;
		}
		return false;
	}

	public boolean isVisible() {
		return menuItemIsVisible && isEnabled();
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
