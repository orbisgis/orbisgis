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

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;

import javax.swing.JLabel;

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.plugins.editor.PlugInEditorListener;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;
import org.orbisgis.utils.I18N;

public class ShowXYPlugIn extends AbstractPlugIn {

	private JLabel showXY;

	@Override
	public boolean execute(PlugInContext context) throws Exception {
		return true;
	}

	private MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			String xCoord = "", yCoord = "", scale = "";
			ToolManager toolManager = getPlugInContext().getToolManager();
			if (toolManager != null) {
				Point2D point = toolManager.getLastRealMousePosition();
				if (point != null) {
					// if (getPlugInContext().isGeographicCRS()) {
					// xCoord = ("" + point.getX()).substring(0, MAX_DIGIT);
					// yCoord = ("" + point.getY()).substring(0, MAX_DIGIT);
					// } else {
					xCoord = "X:" + (int) point.getX();
					yCoord = "Y:" + (int) point.getY();
					scale = I18N
							.getString("orbisgis.org.orbisgis.core.ui.plugins.editors.mapEditor.scale")
							+ ": 1/"
							+ (int) getPlugInContext().getMapEditor()
									.getMapTransform().getScaleDenominator();
				}
				//scale = "1:" + (int)toolManager.getMapTransform().getScaleDenominator();
			}
			showXY.setText(xCoord + "  " + yCoord + " " + scale);
		}
	};

	@Override
	public void initialize(final PlugInContext context) throws Exception {
		showXY = new JLabel();
		showXY.setForeground(Color.blue);
		showXY.setEnabled(false);
		EditorManager em = Services.getService(EditorManager.class);
		em.addEditorListener(new PlugInEditorListener(this, showXY,
				Names.MAP_TOOLBAR_PROJECTION, mouseMotionAdapter, context,
				true));

	}

	@Override
	public boolean isEnabled() {
		boolean isVisible = false;
		IEditor editor = Services.getService(EditorManager.class)
				.getActiveEditor();
		MapEditorPlugIn mapEditor = getPlugInContext().getMapEditor();
		if (editor != null && editor instanceof MapEditorPlugIn
				&& mapEditor != null) {
			String scale = I18N
					.getString("orbisgis.org.orbisgis.core.ui.plugins.editors.mapEditor.scale")
					+ ": 1/"
					+ (int) mapEditor.getMapTransform().getScaleDenominator();
			showXY.setText("0.0     0.0  " + scale);
			MapContext mc = (MapContext) editor.getElement().getObject();
			isVisible = mc.getLayerModel().getLayerCount() > 0;
		}
		showXY.setEnabled(isVisible);
		return isVisible;
	}
}
