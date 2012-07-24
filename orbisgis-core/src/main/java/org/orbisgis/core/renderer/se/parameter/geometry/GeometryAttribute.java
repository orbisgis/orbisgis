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
package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import java.util.Map;
import net.opengis.se._2_0.core.GeometryType;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

/**
 * {@code GeometryAttribute} is a {@link ValueReference} to a geometry in an
 * external file.
 * @author Alexis Guéganno
 */
public class GeometryAttribute extends ValueReference {

    /**
     * Build a new {@code GeometryAttribute} using the JAXB {@code 
     * GeometryType} given in argument.
     * @param geom
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public GeometryAttribute(GeometryType geom) throws InvalidStyle {
        super(geom.getValueReference());
    }

    /**
     * Retrieve the geometry registered in the {@code SpatialDataSourceDecorator}
     * at index {@code fid}.
     * @param sds
     * @param fid
     * @return
     * @throws ParameterException 
     */
    public Geometry getTheGeom(DataSource sds, long fid) throws ParameterException {
        try {
            return getFieldValue(sds, fid).getAsGeometry();
        } catch (DriverException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"", ex);
        }
    }

    /**
     * Retrieve the geometry registered in the {@code SpatialDataSourceDecorator}
     * at index {@code fid}.
     * @param sds
     * @param fid
     * @return
     * @throws ParameterException
     */
    public Geometry getTheGeom(Map<String,Value> map) throws ParameterException {
            return getFieldValue(map).getAsGeometry();
    }

    /**
     * @todo This operation is currently not supported.
     * @return 
     */
    public GeometryType getJAXBGeometryType() {
        GeometryType gt = new GeometryType();
        gt.setValueReference(this.getColumnName());
        return gt;
    }



}
