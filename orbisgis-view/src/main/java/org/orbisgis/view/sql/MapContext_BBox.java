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
package org.orbisgis.view.sql;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.geometryUtils.GeometryConvert;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This class computes the mapcontext bounding box as geometry
 * 
 */
public class MapContext_BBox extends AbstractScalarFunction {

       protected final static I18n I18N = I18nFactory.getI18n(MapContext_BBox.class);
       
        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {

                MapElement mapElement = Services.getService(MapElement.class);
                MapContext mc = mapElement.getMapContext();               
                if (mc != null) {
                        return ValueFactory.createValue(GeometryConvert.toGeometry(mc.getBoundingBox()));

                }
                return ValueFactory.createNullValue();
        }

        @Override
        public String getName() {
                return "Map_BBox";
        }

        @Override
        public int getType(int[] argsTypes) throws InvalidTypeException {
                return Type.POLYGON;
        }

        @Override
        public String getDescription() {
                return I18N.tr("Return the current mapcontext bounding box as a geometry");
        }

        @Override
        public String getSqlOrder() {
                return "SELECT Map_BBox() FROM table";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null))
                        };
        }
}
