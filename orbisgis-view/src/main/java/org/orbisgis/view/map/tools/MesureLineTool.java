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
package org.orbisgis.view.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

/**
 * Get the distance of a drawn line.
 */
public class MesureLineTool extends AbstractLineTool {
    protected static Logger GUI_LOGGER = Logger.getLogger("gui."+MesureLineTool.class);

    @Override
	public void update(Observable o, Object arg) {
		//PlugInContext.checkTool(this);
	}

        @Override
	public boolean isEnabled(MapContext vc, ToolManager tm) {
		return vc.getLayerModel().getLayerCount() > 0;
	}

        @Override
	public boolean isVisible(MapContext vc, ToolManager tm) {
		return true;
	}

        @Override
	protected void lineDone(LineString ls, MapContext vc, ToolManager tm)
			throws TransitionException {
                GUI_LOGGER.info(i18n.tr("Distance : {0}",getLength(ls)));
	}

	private String getLength(Geometry ls) {
		return new DecimalFormat("0.000").format(ls.getLength());
	}

        @Override
	public void drawIn_Point(Graphics g, MapContext vc, ToolManager tm)
			throws DrawingException {
		super.drawIn_Point(g, vc, tm);
		Geometry geom = getCurrentLineString(vc, tm);
		tm.addTextToDraw(getLength(geom) + "");
	}

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("mesurelength");
        }

        @Override
        public String getTooltip() {
            return i18n.tr("This tool mesure the length on the map");
        }

	public String getName() {
		return i18n.tr("Mesure length");
	}
}