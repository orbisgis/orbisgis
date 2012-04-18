/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 * info@orbisgis.org
 */
package org.orbisgis.view.map.tools;

import java.awt.Graphics;
import java.util.Observable;
import javax.swing.AbstractButton;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.DrawingException;
import org.orbisgis.view.map.tool.FinishedAutomatonException;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.generated.Drag;

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
