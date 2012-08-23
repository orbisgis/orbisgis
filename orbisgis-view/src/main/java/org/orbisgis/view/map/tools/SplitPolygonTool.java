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

import com.vividsolutions.jts.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import org.gdms.data.DataSource;
import org.gdms.data.types.*;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.geometryUtils.GeometryEdit;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.tool.Handler;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;

public class SplitPolygonTool extends AbstractLineTool {

        GeometryFactory gf = new GeometryFactory();

        @Override
        public void update(Observable o, Object arg) {
                //PlugInContext.checkTool(this);
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return (ToolUtilities.geometryTypeIs(vc, 
                                TypeFactory.createType(Type.POLYGON), 
                                TypeFactory.createType(Type.MULTIPOLYGON), 
                                TypeFactory.createType(Type.GEOMETRY, 
                                        new GeometryDimensionConstraint(GeometryDimensionConstraint.DIMENSION_SURFACE)), 
                                TypeFactory.createType(Type.GEOMETRYCOLLECTION, 
                                        ConstraintFactory.createConstraint(Constraint.DIMENSION_2D_GEOMETRY,
                                                GeometryDimensionConstraint.DIMENSION_SURFACE))))
                        && ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionEqualsTo(vc, 1);
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        public String getName() {
                return I18N.tr("Split polygon");
        }

        @Override
        protected void lineDone(LineString ls, MapContext vc, ToolManager tm) throws TransitionException {
                Handler handler = tm.getCurrentHandlers().get(0);
                DataSource sds = vc.getActiveLayer().getDataSource();
                try {
                        Geometry geom = sds.getGeometry(handler.getGeometryIndex());
                        if (ToolUtilities.geometryTypeIs(vc, 
                                        TypeFactory.createType(Type.MULTIPOLYGON), 
                                        TypeFactory.createType(Type.GEOMETRYCOLLECTION, 
                                                ConstraintFactory.createConstraint(Constraint.DIMENSION_2D_GEOMETRY,
                                                        GeometryDimensionConstraint.DIMENSION_SURFACE)))) {
                                List<Polygon> pols = new ArrayList<Polygon>();
                                for (int i = 0; i < geom.getNumGeometries(); i++) {
                                        pols.addAll(GeometryEdit.splitPolygon((Polygon) geom.getGeometryN(i), ls));
                                }
                                MultiPolygon result = gf.createMultiPolygon(pols.toArray(new Polygon[pols.size()]));
                                if (result != null) {
                                        sds.setGeometry(handler.getGeometryIndex(), result);
                                }
                        } else if (ToolUtilities.geometryTypeIs(vc, 
                                        TypeFactory.createType(Type.POLYGON), 
                                        TypeFactory.createType(Type.GEOMETRY, 
                                                ConstraintFactory.createConstraint(Constraint.DIMENSION_2D_GEOMETRY,
                                                        GeometryDimensionConstraint.DIMENSION_SURFACE)))) {
                                List<Polygon> polygons = GeometryEdit.splitPolygon((Polygon) geom, ls);
                                if (polygons != null) {
                                        sds.deleteRow(handler.getGeometryIndex());
                                        Value[] row = sds.getRow(handler.getGeometryIndex());
                                        for (Polygon polygon : polygons) {
                                                row[sds.getSpatialFieldIndex()] = ValueFactory.createValue(polygon);
                                                sds.insertFilledRow(row);
                                        }
                                }

                        }

                } catch (DriverException e) {
                        throw new TransitionException(I18N.tr("Cannot split the polygon"), e);
                }
        }
}
