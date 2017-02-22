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
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditor.map.tool.DrawingException;
import org.orbisgis.mapeditor.map.tool.FinishedAutomatonException;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.orbisgis.mapeditor.map.tools.generated.Drag;

/**
 * Abstract class for building Drag tools.
 *
 * getFirstPoint returns the point that was first clicked, and ToolManager.getValues returns the
 * current point.
 *
 * @author Antoine Gourlay
 */
public abstract class AbstractDragTool extends Drag {

        private double[] firstPoint;

        @Override
        public void transitionTo_Standby(MapContext vc, ToolManager tm) throws FinishedAutomatonException,
                TransitionException {
                firstPoint = null;
        }

        @Override
        public void drawIn_Standby(Graphics g, MapContext vc, ToolManager tm) throws DrawingException {
        }

        @Override
        public void transitionTo_MouseDown(MapContext vc, ToolManager tm) throws FinishedAutomatonException,
                TransitionException {
                firstPoint = tm.getValues();
        }

        @Override
        public void drawIn_MouseReleased(Graphics g, MapContext vc, ToolManager tm) throws DrawingException {
                drawIn_MouseDown(g, vc, tm);
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return true;
        }

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        /**
         * @return the firstPoint
         */
        public double[] getFirstPoint() {
                return firstPoint;
        }
}
