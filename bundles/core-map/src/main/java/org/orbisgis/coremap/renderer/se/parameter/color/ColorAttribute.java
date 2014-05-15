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
package org.orbisgis.coremap.renderer.se.parameter.color;

import java.awt.Color;
import java.sql.ResultSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.ValueReference;

/**
 * The {@code ValueReference} implementation of {@code ColorParameter}. That means that 
 * this class is used to retrieve color values by using a GDMS 
 * {@code DataSet} as specified in {@link ValueReference ValueReference}.</p>
 * <p>Note that the {@code DataSet} is not directly attached to the class,
 * and must be specified each time you call {@code getValue}.
 * @author Alexis Guéganno, Maxence Laurent
 */
public class ColorAttribute extends ValueReference implements ColorParameter {

    /**
     * Instanciates the attribute with the name of the column where to search.
     * @param fieldName 
     */
    public ColorAttribute(String fieldName) {
        super(fieldName);
    }

    /**
     * Build a <code>colorAttribute</code> from its JAXB representation.
     * @param expr
     * @throws org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle
     */
    public ColorAttribute(JAXBElement<String> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public Color getColor(ResultSet rs, long fid) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(rs, fid).toString());
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"",e);
        }
    }

    @Override
    public Color getColor(Map<String,Object> map) throws ParameterException {
        try {
            return Color.getColor(getFieldValue(map).toString());
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"",e);
        }
    }
}
