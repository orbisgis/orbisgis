/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.Observable;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.*;


public class EditionSelectionTool extends AbstractSelectionTool {

        AbstractButton button;

        public AbstractButton getButton() {
                return button;
        }

        public void setButton(AbstractButton button) {
                this.button = button;
        }

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return ToolUtilities.isActiveLayerEditable(vc)
                        && ToolUtilities.isActiveLayerVisible(vc) && ToolUtilities.isSelectionGreaterOrEqualsThan(vc,1);
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        protected ILayer getLayer(MapContext mc) {
                return mc.getActiveLayer();
        }

        @Override
        public void transitionTo_OnePoint(MapContext mc, ToolManager tm) {
        }

        @Override
        public void transitionTo_TwoPoints(MapContext mc, ToolManager tm){
                
        }

        /**
         * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#transitionTo_MakeMove()
         */
        @Override
        public void transitionTo_MakeMove(MapContext mc, ToolManager tm)
                throws TransitionException, FinishedAutomatonException {
                DataSource ds = getLayer(mc).getDataSource();
                for (int i = 0; i < selected.size(); i++) {
                        Handler handler = selected.get(i);
                        Geometry g;
                        try {
                                g = handler.moveTo(tm.getValues()[0], tm.getValues()[1]);
                        } catch (CannotChangeGeometryException e1) {
                                throw new TransitionException(e1);
                        }

                        try {
                                ds.setGeometry(handler.getGeometryIndex(), g);
                        } catch (DriverException e) {
                                throw new TransitionException(e);
                        }
                }

                transition(Code.EMPTY);
        }

        /**
         * @see org.orbisgis.plugins.org.orbisgis.plugins.core.ui.editors.table.estouro.tools.generated.Selection#drawIn_Movement(java.awt.Graphics)
         */
        @Override
        public void drawIn_Movement(Graphics g, MapContext vc, ToolManager tm)
                throws DrawingException {
                Point2D p = tm.getLastRealMousePosition();
                try {
                        for (int i = 0; i < selected.size(); i++) {
                                Handler handler = selected.get(i);
                                Geometry geom = handler.moveTo(p.getX(), p.getY());
                                tm.addGeomToDraw(geom);
                        }
                } catch (CannotChangeGeometryException e) {
                        throw new DrawingException(
                                I18N.tr("Cannot update the geometry {0}",e.getMessage()));
                }
        }

        @Override
        public String getName() {
                return I18N.tr("Move vertex");
        }

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("moveVertex");
        }

}
