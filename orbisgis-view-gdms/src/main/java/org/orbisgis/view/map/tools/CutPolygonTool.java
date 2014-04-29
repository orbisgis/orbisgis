/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import javax.swing.ImageIcon;
import org.gdms.data.DataSource;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.geometryUtils.GeometryEdit;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.*;

/**
 * This tool subtract polygons in Handler by the drawing one, using {@link GeometryEdit#cutMultiPolygon}.
 */
public class CutPolygonTool extends AbstractPolygonTool {

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        protected void polygonDone(Polygon pol,
                MapContext mc, ToolManager tm) throws TransitionException {
                DataSource sds = mc.getActiveLayer().getDataSource();
                try {
                        ArrayList<Handler> handlers = new ArrayList<Handler>(tm.getCurrentHandlers());
                        for (Handler handler : handlers) {
                                if (handler instanceof MultiPolygonHandler) {
                                        MultiPolygonHandler mp = (MultiPolygonHandler) handler;
                                        MultiPolygon mpolygon = (MultiPolygon) sds.getGeometry(mp.getGeometryIndex());
                                        MultiPolygon result = GeometryEdit.cutMultiPolygonWithPolygon(mpolygon, pol);
                                        if (result != null) {
                                                sds.setGeometry(mp.getGeometryIndex(), result);
                                        }
                                } else if (handler instanceof PolygonHandler) {
                                        PolygonHandler ph = (PolygonHandler) handler;
                                        Polygon polygon = (Polygon) sds.getGeometry(ph.getGeometryIndex());
                                        Collection<Polygon> polygons = GeometryEdit.cutPolygonWithPolygon(polygon, pol);
                                        if (polygons != null) {
                                                sds.deleteRow(handler.getGeometryIndex());
                                                Value[] row = sds.getRow(handler.getGeometryIndex());
                                                for (Polygon result : polygons) {
                                                        row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(result);
                                                        sds.insertFilledRow(row);
                                                }
                                        }
                                }
                        }

                } catch (DriverException e) {
                        throw new TransitionException(i18n.tr("Cannot cut the polygon"), e);
                }
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return ToolUtilities.geometryTypeIs(vc, 
                        TypeFactory.createType(Type.POLYGON), 
                        TypeFactory.createType(Type.MULTIPOLYGON), 
                        TypeFactory.createType(Type.GEOMETRY, 
                                new GeometryDimensionConstraint(GeometryDimensionConstraint.DIMENSION_SURFACE)), 
                        TypeFactory.createType(Type.MULTIPOLYGON, 
                                new GeometryDimensionConstraint(GeometryDimensionConstraint.DIMENSION_SURFACE))) && 
                        ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionEqualsTo(vc, 1);
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        public double getInitialZ(MapContext mapContext) {
                return ToolUtilities.getActiveLayerInitialZ(mapContext);
        }

        @Override
        public String getName() {
                return i18n.tr("Cut a polygon");
        }

        @Override
        public String getTooltip() {
                return i18n.tr("Select a polygon then draw another polygon to subtract the first one");
        }

        @Override
        public ImageIcon getImageIcon() {
            return OrbisGISIcon.getIcon("edition/cutpolygon");
        }
}
