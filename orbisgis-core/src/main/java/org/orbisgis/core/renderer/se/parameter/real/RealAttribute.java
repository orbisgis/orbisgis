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
package org.orbisgis.core.renderer.se.parameter.real;

import java.util.Map;
import net.opengis.fes._2.ValueReferenceType;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

/**
 * The {@code ValueReference} implementation of {@code RealParameter}. That means that 
 * this class is used to retrieve real (numeric) values by using a GDMS 
 * {@code DataSource} as specified in {@link ValueReference ValueReference}.</p>
 * <p>Note that the {@code DataSource} is not directly attached to the class,
 * and must be specified each time you call {@code getValue}.
 * @author Alexis Guéganno, Maxence Laurent
 */
public class RealAttribute extends ValueReference implements RealParameter {

    private RealParameterContext ctx;

    /**
     * Create a new instance of {@code RealAttribute}, with an empty associated field name.
     * @param fieldName 
     */
    public RealAttribute() {
        ctx = RealParameterContext.REAL_CONTEXT;
    }

    /**
     * Create a new instance of {@code RealAttribute}, setting the fieldName of the column where
     * the values will be searched.
     * @param fieldName 
     */
    public RealAttribute(String fieldName) {
        super(fieldName);
        ctx = RealParameterContext.REAL_CONTEXT;
    }

    /**
     * Create a new instance of {@code RealAttribute}, using a {@code JAXBElement} to retrieve
     * all the needed informations.
     * @param fieldName 
     */
    public RealAttribute(ValueReferenceType expr) throws InvalidStyle {
        super(expr);
        ctx = RealParameterContext.REAL_CONTEXT;
    }

    @Override
    public Double getValue(DataSource sds, long fid) throws ParameterException {
        try {
            Value value = this.getFieldValue(sds, fid);
            if (value.isNull()) {
                return null;
            }
            return value.getAsDouble();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"", e);
        }
    }

    @Override
    public Double getValue(Map<String,Value> map) throws ParameterException {
        try {
            Value value = this.getFieldValue(map);
            if (value.isNull()) {
                return null;
            }
            return value.getAsDouble();
        } catch (Exception e) {
            throw new ParameterException("Could not fetch feature attribute \"" + getColumnName() + "\"", e);
        }
    }

    @Override
    public String toString() {
        return "<" + getColumnName() + ">";
    }

    @Override
    public void setContext(RealParameterContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public RealParameterContext getContext() {
        return ctx;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
