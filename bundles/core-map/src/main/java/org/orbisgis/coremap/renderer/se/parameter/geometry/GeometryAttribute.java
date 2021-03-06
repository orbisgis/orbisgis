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
package org.orbisgis.coremap.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import net.opengis.se._2_0.core.GeometryType;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.ValueReference;

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
     * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
     */
    public GeometryAttribute(GeometryType geom) throws InvalidStyle {
        super(geom.getValueReference());
    }

    /**
     * Retrieve the geometry registered in the {@code SpatialDataSetDecorator}
     * at index {@code fid}.
     * @param rs
     * @param fid
     * @return
     * @throws ParameterException 
     */
    public Geometry getTheGeom(ResultSet rs, long fid) throws ParameterException {
        try {
            return (Geometry)getFieldValue(rs, fid);
        } catch (SQLException ex) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"", ex);
        }
    }

    /**
     * Retrieve the geometry registered in the {@code SpatialDataSetDecorator}
     * at index {@code fid}.
     * @param map Field name and field value
     * @return Geometry instance
     * @throws ParameterException
     */
    public Geometry getTheGeom(Map<String,Object> map) throws ParameterException {
            return (Geometry)getFieldValue(map);
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
