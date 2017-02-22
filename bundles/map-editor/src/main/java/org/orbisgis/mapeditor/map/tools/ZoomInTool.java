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

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * Tool to zoom in
 * 
 * @author Fernando Gonzalez Cortes
 */
public class ZoomInTool extends AbstractRectangleTool {

	
	public ZoomInTool() {
	}

	@Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

	@Override
	protected void rectangleDone(Rectangle2D rect,
			boolean smallerThanTolerance, MapContext vc, ToolManager tm)
			throws TransitionException {
		tm.getMapTransform().setExtent(
				new Envelope(rect.getMinX(), rect.getMaxX(), rect.getMinY(),
						rect.getMaxY()));
	}

	@Override
	protected Rectangle2D buildRectangleOnPoint(ToolManager toolManager,
			double x, double y) {
		Envelope extent = toolManager.getMapTransform().getExtent();
		double width = extent.getWidth() / 2;
		double height = extent.getHeight() / 2;
		double minx = x - width / 2;
		double miny = y - height / 2;
        return new Rectangle2D.Double(minx, miny, width,
                height);
	}

	@Override
	public Point getHotSpotOffset() {
		return new Point(5, 5);
	}

	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

        @Override
	public ImageIcon getImageIcon() {
		return MapEditorIcons.getIcon("zoom_in");
	}

        @Override
        public ImageIcon getCursor() {
            return MapEditorIcons.getIcon("zoom_in");
        }
        @Override
	public String getName() {
		return i18n.tr("Zoom in");
	}

        @Override
        public String getTooltip() {
                return i18n.tr("Click on the map to zoom in");
        }
        

}
