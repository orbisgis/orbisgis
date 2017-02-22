/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.mapeditor.map.tools;

import java.awt.Graphics;
import java.util.Observable;

import javax.swing.ImageIcon;

import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.orbisgis.mapeditor.map.tools.generated.ZoomOut;


import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;

/**
 * Tool to zoom out
 * 
 * @author Fernando Gonzalez Cortes
 */
public class ZoomOutTool extends ZoomOut {

	@Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

	@Override
	public void transitionTo_Standby(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	@Override
	public void transitionTo_Done(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
		MapTransform mapTransform = tm.getMapTransform();
		Envelope extent = mapTransform.getExtent();
		double width = 2 * extent.getWidth();
		double height = 2 * extent.getHeight();
		double x = tm.getValues()[0];
		double y = tm.getValues()[1];
		Envelope newExtent = new Envelope(x - width / 2, x + width / 2, y
				- height / 2, y + height / 2);
		mapTransform.setExtent(newExtent);

		transition(Code.INIT);
	}

	@Override
	public void transitionTo_Cancel(MapContext vc, ToolManager tm)
			throws TransitionException {
	}

	@Override
	public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) {
	}

	@Override
	public void drawIn_Done(Graphics g, MapContext vc, ToolManager tm) {
	}

	@Override
	public void drawIn_Cancel(Graphics g, MapContext vc, ToolManager tm) {
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

        @Override
	public ImageIcon getImageIcon() {
		return MapEditorIcons.getIcon("zoom_out");
	}

        @Override
	public String getName() {
		return i18n.tr("Zoom out");
	}

        @Override
        public String getTooltip() {
            return i18n.tr("This tool zoom out on click");
        }

        @Override
        public ImageIcon getCursor() {
            return MapEditorIcons.getIcon("zoom_out");
        }
        
}
