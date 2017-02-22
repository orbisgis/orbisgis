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

import com.vividsolutions.jts.geom.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;

/**
 * Show in the log the coordinate of the selected point.
 */
public class PickCoordinatesPointTool extends AbstractPointTool {

        protected static Logger GUI_LOGGER = LoggerFactory.getLogger("gui."+PickCoordinatesPointTool.class);
        
        

        @Override
        protected void pointDone(Point point, MapContext mc, ToolManager tm)
                throws TransitionException {                             
                Graphics g = tm.getComponent().getGraphics();
                if ((g != null) && (g instanceof Graphics2D)) {
                                // flash make the GUI unresponsive during 1s..
                                //SymbolUtil.flashPoint(point, (Graphics2D) g, tm.getMapTransform());
                                GUI_LOGGER.info(i18n.tr("Coordinate : {0}", point.toText()));
                        }
                }
        
        @Override
        public String getName() {
                return i18n.tr("Pick a point");
        }

        @Override
        public String getTooltip() {
            return i18n.tr("Pick a point");
        }

        @Override
        public ImageIcon getImageIcon() {
               return MapEditorIcons.getIcon("coordinate_capture");
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return vc.getLayerModel().getLayerCount() > 0;
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        public void update(Observable o, Object o1) {

        }
}
