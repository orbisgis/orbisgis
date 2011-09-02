/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *
 * Copyright (C) 2007-2008 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
 * Copyright (C) 2011 Erwan BOCHER, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.editors.map.tools;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.swing.AbstractButton;

import org.gdms.data.DataSource;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.utils.I18N;
 
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.geometryUtils.GeometryEdit;
import org.orbisgis.core.ui.editors.map.tool.Handler;

public class SplitPolygonTool extends AbstractLineTool {

        AbstractButton button;
        GeometryFactory gf = new GeometryFactory();

        @Override
        public AbstractButton getButton() {
                return button;
        }

        @Override
        public void setButton(AbstractButton button) {
                this.button = button;
        }

        @Override
        public void update(Observable o, Object arg) {
                PlugInContext.checkTool(this);
        }

        @Override
        public boolean isEnabled(MapContext vc, ToolManager tm) {
                return (ToolUtilities.geometryTypeIs(vc, 
                                TypeFactory.createType(Type.POLYGON), 
                                TypeFactory.createType(Type.MULTIPOLYGON), 
                                TypeFactory.createType(Type.GEOMETRY, 
                                        ConstraintFactory.createConstraint(Constraint.DIMENSION_2D_GEOMETRY,
                                                GeometryDimensionConstraint.DIMENSION_POLYGON)), 
                                TypeFactory.createType(Type.GEOMETRYCOLLECTION, 
                                        ConstraintFactory.createConstraint(Constraint.DIMENSION_2D_GEOMETRY,
                                                GeometryDimensionConstraint.DIMENSION_POLYGON))))
                        && ToolUtilities.isActiveLayerEditable(vc) && ToolUtilities.isSelectionEqualsTo(vc, 1);
        }

        @Override
        public boolean isVisible(MapContext vc, ToolManager tm) {
                return isEnabled(vc, tm);
        }

        @Override
        public String getName() {
                return I18N.getString("orbisgis.core.ui.editors.map.tool.polygon.splitPolygon");
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
                                                        GeometryDimensionConstraint.DIMENSION_POLYGON)))) {
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
                                                        GeometryDimensionConstraint.DIMENSION_POLYGON)))) {
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
                        throw new TransitionException(I18N.getString("orbisgis.core.ui.editors.map.tool.polygon.cannotSplitPolygon"), e);
                }
        }
}
