package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ExpressionType;
import net.opengis.fes._2.ObjectFactory;
import net.opengis.fes._2.ValueReferenceType;
import net.opengis.se._2_0.core.ParameterValueType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

/**
 * An (abstract) representation of a Value in a GDMS table. 
 * @author alexis
 */
public abstract class ValueReference implements SeParameter {

	protected String fieldName;
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

	public ValueReference(ValueReferenceType pName) throws InvalidStyle {
		if (pName.getContent().size() == 1) {
			this.fieldName = (String) pName.getContent().get(0);
			this.fieldId = -1;
		} else {
			throw new InvalidStyle("Invalid field name");
		}
	}

	public ValueReference(JAXBElement<ValueReferenceType> expr) throws InvalidStyle {
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

	@Override
	public String dependsOnFeature() {
		return getColumnName();
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
	}

        /**
         * Retrieve the name of the column where the value can be retrieved in the table
         * @return 
         */
	public String getColumnName() {
		return fieldName;
	}

        /**
         * Get the GDMS value associated to this Reference in the given table (representing
         * by the SpatialDataSourceDecorator sds) at line fid.
         * @param sds
         * @param fid
         * @return
         * @throws DriverException 
         */
	public Value getFieldValue(SpatialDataSourceDecorator sds, long fid) throws DriverException {
		if (this.fieldId == -1) {
			this.fieldId = sds.getMetadata().getFieldIndex(fieldName);
		}
		return sds.getFieldValue(fid, fieldId);
	}

	@Override
	public ParameterValueType getJAXBParameterValueType() {
		ParameterValueType p = new ParameterValueType();
		p.getContent().add(this.getJAXBExpressionType());
		return p;
	}

	@Override
	public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
		ValueReferenceType p = new ValueReferenceType();
		p.getContent().add(fieldName);
		ObjectFactory of = new ObjectFactory();
		return of.createValueReference(p);
	}
}
