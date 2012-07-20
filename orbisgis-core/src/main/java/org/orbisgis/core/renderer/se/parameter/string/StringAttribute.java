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
package org.orbisgis.core.renderer.se.parameter.string;

import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ValueReferenceType;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

/**
 * The {@code ValueReference} implementation of {@code StringParameter}. That means that 
 * this class is used to retrieve string values by using a GDMS 
 * {@code DataSource} as specified in {@link ValueReference ValueReference}.</p>
 * <p>Note that the {@code DataSource} is not directly attached to the class,
 * and must be specified each time you call {@code getValue}.
 * @author Alexis Guéganno, Maxence Laurent
 */
public class StringAttribute extends ValueReference implements StringParameter{


    private String[] restriction;

    /**
     * Creates a new {@code StringAttribute}, that will searches its values in a column named
     * filedName.
     * @param fieldName
     * @param ds
     * @throws DriverException
     */
    public StringAttribute(String fieldName) {
        super(fieldName);
    }

    /**
     * Create a new instance of {@code StringAttribute}, using a {@code JAXBElement} to retrieve
     * all the needed informations.
     * @param fieldName 
     */
    public StringAttribute(JAXBElement<String> expr) throws InvalidStyle {
        super(expr);
    }

    @Override
    public String getValue(DataSource sds, long fid) throws ParameterException{ // TODO implement
        try {
			Value fieldValue = getFieldValue(sds, fid);
			return fieldValue.toString();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \""+ getColumnName() +"\" (" + e + ")");
        }
    }

    @Override
    public String getValue(Map<String, Value> feature) throws ParameterException {
        return getFieldValue(feature).toString();
    }

    @Override
    public void setRestrictionTo(String[] list) {
        restriction = list.clone();
    }

}
