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
package org.orbisgis.core.renderer.se.parameter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.fes._2.ValueReferenceType;
import net.opengis.se._2_0.core.ParameterValueType;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;

/**
 * An (abstract) representation of a Value in a GDMS table. 
 * @author Alexis Guéganno, Maxence Laurent
 */
public abstract class ValueReference extends AbstractSymbolizerNode implements SeParameter{

	private String fieldName;
	private int fieldId;
	private ArrayList<PropertyNameListener> listeners;

        /**
         * Creates a new ValueReference, without column name or valid index to a value.
         */
	public ValueReference() {
		this.fieldId = -1;
	}

        /**
         * Creates a new ValueReference, that will searches its values in a column named
         * filedName.
         * @param fieldName 
         */
	public ValueReference(String fieldName) {
		this.fieldId = -1;
		this.fieldName = fieldName;
	}

        /**
         * Build a new {@code ValueReference} using the given 
         * {@code ValueReferenceType}.
         * @param pName
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
	public ValueReference(ValueReferenceType pName) throws InvalidStyle {
		if (pName.getContent().size() == 1) {
			this.fieldName = (String) pName.getContent().get(0);
			this.fieldId = -1;
		} else {
			throw new InvalidStyle("Invalid field name");
		}
	}

        /**
         * Build a new {@code ValueReference} using the given {@code JAXBElement
         * } that contains a {@code ValueReferenceType}.
         * 
         * @param expr
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
         */
	public ValueReference(JAXBElement<String> expr) throws InvalidStyle {
		this(expr.getValue());
	}

        /**
         * Add a listener to this ValueReference.
         * @param l 
         */
	public synchronized void register(PropertyNameListener l) {
		if (listeners == null) {
			this.listeners = new ArrayList<PropertyNameListener>();
		}

		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	private synchronized void firePropertyNameChange() {
		if (listeners != null) {
			for (PropertyNameListener l : listeners) {
				l.propertyNameChanged(this);
			}
		}
	}

        /**
         * Set the name of the column where the data will be retrieved.
         * @param fieldName 
         */
	public final void setColumnName(String fieldName) {
		// look for field before assigning the name !
		this.fieldId = -1;
		this.fieldName = fieldName;
		firePropertyNameChange();
                update();
	}

        /**
         * Retrieve the name of the column where the value can be retrieved in the table
         * @return 
         */
	public String getColumnName() {
		return fieldName;
	}

        /**
         * Get the GDMS {@code Object} associated to this Reference in the given
         * table (represented by the {@code DataSet sds}) at line fid.
         * @param sds ResultSet
         * @param fid Field Id
         * @return Field Value
         */
    public Object getFieldValue(ResultSet sds, long fid) throws SQLException {
        if (this.fieldId == -1) {
            this.fieldId = getFieldIndexFromLabel(sds, fieldName);
        }
        return sds.getObject(fieldId);
    }

    private static int getFieldIndexFromLabel(ResultSet rs, String fieldName) throws SQLException {
        ResultSetMetaData mt = rs.getMetaData();
        for(int idcolumn=1;idcolumn<=mt.getColumnCount();idcolumn++) {
            if(mt.getColumnName(idcolumn).equalsIgnoreCase(fieldName)) {
                return idcolumn;
            }
        }
        throw new SQLException("Field not found "+fieldName);
    }
    /**
     * Get the GDMS {@code Value} associated to this reference in the given
     * {@code map}. The value returned by {@link ValueReference#getColumnName()}
     * is used as the key.
     * @return Value
     * @throws ParameterException
     * If the value returned by {@link ValueReference#getColumnName()} is not
     * a key in {@code map}.
     */
    public Object getFieldValue(Map<String,Object> map) throws ParameterException {
        if(map.containsKey(fieldName)){
            return map.get(fieldName);
        } else {
            throw new ParameterException("The given map does not contain the needed key/value pair.");
        }
    }

	@Override
	public ParameterValueType getJAXBParameterValueType() {
		ParameterValueType p = new ParameterValueType();
		p.getContent().add(this.getJAXBExpressionType());
		return p;
	}

	@Override
	public JAXBElement<?> getJAXBExpressionType() {
		ObjectFactory of = new ObjectFactory();
		return of.createValueReference(fieldName);
	}

        @Override
        public List<SymbolizerNode> getChildren() {
                return new ArrayList<SymbolizerNode>();
        }


}
