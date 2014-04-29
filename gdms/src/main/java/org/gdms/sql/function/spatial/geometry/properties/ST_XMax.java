/*
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

package org.gdms.sql.function.spatial.geometry.properties;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.FunctionException;

/**
 * Extract the maximal X of input geometry.
 * @author Nicolas Fortin
 */
public class ST_XMax extends AbstractSpatialPropertyFunction {

    @Override
    protected Value evaluateResult(DataSourceFactory dsf, Value... args) throws FunctionException {
        Geometry value = args[0].getAsGeometry();
        if(value!=null) {
            return ValueFactory.createValue(value.getEnvelopeInternal().getMaxX());
        } else {
            return ValueFactory.createNullValue();
        }
    }

    @Override
    public String getDescription() {
        return "Return the maximal X of the provided geometry, return null value if geometry is null.";
    }

    @Override
    public String getName() {
        return "ST_XMax";
    }

    @Override
    public String getSqlOrder() {
        return "select ST_XMax(the_geom) from myTable;";
    }

    @Override
    public int getType(int[] types) {
        return Type.DOUBLE;
    }
}