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
package org.orbisgis.core.ui.plugins.editors.mapEditor;

import javax.swing.JButton;

import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editorViews.toc.EditableLayer;
import org.orbisgis.core.ui.editors.table.Selection;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

import org.gdms.driver.DriverException;

public class ClearMapSelectionPlugIn extends AbstractPlugIn {

	private JButton btn;

	public ClearMapSelectionPlugIn() {
		btn = new JButton(OrbisGISIcon.EDIT_CLEAR);
		btn.setToolTipText(I18N
				.getString("orbisgis.ui.popupmenu.table.clearSelection"));
	}

	public boolean execute(PlugInContext context) throws Exception {
		IEditor editor = getPlugInContext().getActiveEditor();
		MapContext mc = (MapContext) editor.getElement().getObject();
		ILayer[] layers = mc.getLayerModel().getLayersRecursively();
		EditableElement element = editor.getElement();
		for (ILayer lyr : layers) {
			if (!lyr.isStream()) {
				final Selection selection = new EditableLayer(element, lyr)
						.getSelection();
				if (!mc.isSelectionInducedRefresh()) {
					mc.checkSelectionRefresh(new int[0], selection
							.getSelectedRows(), lyr.getDataSource());
				}
				selection.clearSelection();
			}
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getEditionMapToolBar().addPlugIn(
				this, btn, context);
	}

	public boolean isEnabled() {
		boolean isEnabled = false;
		MapEditorPlugIn mapEditor = null;
		if ((mapEditor = getPlugInContext().getMapEditor()) != null) {
			MapContext mc = (MapContext) mapEditor.getElement().getObject();
			ILayer[] layers = mc.getLayerModel().getLayersRecursively();
			                     try {
                                for (ILayer lyr : layers) {
                                        if (!lyr.isStream()) {
                                                lyr.getSelection();
                                                if (lyr.getSelection().length > 0) {
                                                        isEnabled = true;
                                                }
                                        }
                                }
                        } catch (DriverException e) {
                        }
		}
		btn.setEnabled(isEnabled);
		return true;
	}
}
