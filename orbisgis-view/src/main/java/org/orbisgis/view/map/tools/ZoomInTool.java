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
/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

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

	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

        @Override
	public ImageIcon getImageIcon() {
		return OrbisGISIcon.getIcon("zoom_in");
	}

        @Override
        public ImageIcon getCursor() {
            return OrbisGISIcon.getIcon("zoom_in");
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
