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

package org.orbisgis.plugins.core.ui.editors.MapEditor;

import java.util.Arrays;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.gdms.data.DataSource;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.images.IconLoader;

public class DeleteMapSelectionPlugIn extends AbstractPlugIn {

	private JButton btn;

	public DeleteMapSelectionPlugIn() {
		btn = new JButton(getIcon());
	}

	public boolean execute(PlugInContext context) throws Exception {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		MapContext map = (MapContext) editor.getElement().getObject();
		ILayer activeLayer = map.getActiveLayer();
		int[] sel = activeLayer.getSelection().clone();
		Arrays.sort(sel);
		SpatialDataSourceDecorator dataSource = activeLayer.getDataSource();
		try {
			dataSource.setDispatchingMode(DataSource.STORE);
			for (int i = sel.length - 1; i >= 0; i--) {
				dataSource.deleteRow(sel[i]);
			}
			dataSource.setDispatchingMode(DataSource.DISPATCH);
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot delete selected features",
					e);
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbcontext = context.getWorkbenchContext();
		wbcontext.getWorkbench().getFrame().getEditionMapToolBar().addPlugIn(
				this, btn);
	}

	public void update(Observable o, Object arg) {
		btn.setEnabled(isEnabled());
		btn.setVisible(isVisible());
	}

	public boolean isEnabled() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if ("Map".equals(em.getEditorId(editor)) && editor != null) {
			MapContext map = (MapContext) editor.getElement().getObject();
			ILayer activeLayer = map.getActiveLayer();
			return (activeLayer != null)
					&& activeLayer.getSelection().length > 0;
		}
		return false;
	}

	public boolean isVisible() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if ("Map".equals(em.getEditorId(editor)) && editor != null) {
			MapContext map = (MapContext) editor.getElement().getObject();
			return map.getActiveLayer() != null;
		}
		return false;
	}

	public static ImageIcon getIcon() {
		return IconLoader.getIcon(Names.REMOVE_ICON);
	}
}
