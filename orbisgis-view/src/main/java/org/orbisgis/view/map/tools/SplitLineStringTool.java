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
import com.vividsolutions.jts.geom.MultiLineString;
import java.util.Observable;


import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;


import com.vividsolutions.jts.geom.Point;
import javax.swing.ImageIcon;
import org.gdms.data.DataSource; 
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.geometryUtils.GeometryEdit;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.Handler;

public class SplitLineStringTool extends AbstractPointTool {

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return ToolUtilities.geometryTypeIs(vc, 
                                TypeFactory.createType(Type.LINESTRING), 
                                TypeFactory.createType(Type.MULTILINESTRING))
                        && ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionEqualsTo(vc, 1);
        }

        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        protected void pointDone(Point point, MapContext mc, ToolManager tm)
                throws TransitionException {
                Handler handler = tm.getCurrentHandlers().get(0);
                DataSource sds = mc.getActiveLayer().getDataSource();
                try {
                        Geometry geom = sds.getGeometry(handler.getGeometryIndex());
                        int uiTolerance = tm.getUITolerance();
                        Value[] row = sds.getRow(handler.getGeometryIndex());
                        if (ToolUtilities.geometryTypeIs(mc, TypeFactory.createType(Type.MULTILINESTRING))) {
                               MultiLineString result =  GeometryEdit.splitMultiLineString((MultiLineString) geom,point, uiTolerance);
                                if (result != null) {
                                    sds.setGeometry(handler.getGeometryIndex(), result);
                                }
                        } else if (ToolUtilities.geometryTypeIs(mc, TypeFactory.createType(Type.LINESTRING))) {
                                LineString[] lines = GeometryEdit.splitLineString((LineString) geom, point, uiTolerance);
                                if (lines != null) {
                                        sds.setGeometry(handler.getGeometryIndex(), lines[0]);
                                        row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(lines[1]);
                                        sds.insertFilledRow(row);
                                        mc.getActiveLayer().setSelection(new int[]{handler.getGeometryIndex(), (int) (sds.getRowCount() - 1)});
                                }
                        }

                } catch (DriverException e) {
                        throw new TransitionException(I18N.tr("Cannot split line"), e);
                }

        }

        @Override
        public String getName() {
                return I18N.tr("Split line");
        }

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("splitlinestring");
        }
}
