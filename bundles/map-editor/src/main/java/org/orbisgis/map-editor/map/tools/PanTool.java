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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import javax.swing.ImageIcon;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

/**
 * Tool to move the map extent
 * 
 */
public class PanTool extends AbstractDragTool {

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("pan");
        }
        
        

	@Override
	public void transitionTo_MouseReleased(MapContext vc, ToolManager tm)
			throws TransitionException, FinishedAutomatonException {
                // get the current point
		double[] v = tm.getValues();

                // get the start point (of the dragging move)
                double[] firstPoint = getFirstPoint();

                // diff
		double dx = firstPoint[0] - v[0];
		double dy = firstPoint[1] - v[1];

                // move the envelope
		Envelope extent = tm.getMapTransform().getExtent();
        if(extent!=null) {
            tm.getMapTransform().setExtent(
                    new Envelope(extent.getMinX() + dx, extent.getMaxX() + dx,
                            extent.getMinY() + dy, extent.getMaxY() + dy));
        }
                // we're done, this will get us back to StandBy
		transition(Code.FINISHED);
	}

	@Override
	public void drawIn_MouseDown(Graphics g, MapContext vc, ToolManager tm) {
                // this is what is displayed when dragging

                // current point position
                double[] firstPoint = getFirstPoint();

                // get the corresponding point in the map
		Point p = tm.getMapTransform().fromMapPoint(
				new Point2D.Double(firstPoint[0], firstPoint[1]));
		int height = tm.getMapTransform().getHeight();
		int width = tm.getMapTransform().getWidth();

                // draw the new map...
		g.clearRect(0, 0, width, height);
		g.drawImage(tm.getMapTransform().getImage(), tm.getLastMouseX() - p.x,
				tm.getLastMouseY() - p.y, null);
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return ToolUtilities.layerCountGreaterThan(vc, 0);
	}

        @Override
	public String getName() {
		return i18n.tr("Pan");
	}

    @Override
    public String getTooltip() {
        return i18n.tr("The Pan Tool");
    }

    @Override
    public ImageIcon getCursor() {
        return OrbisGISIcon.getIcon("pan");
    }
        
}
