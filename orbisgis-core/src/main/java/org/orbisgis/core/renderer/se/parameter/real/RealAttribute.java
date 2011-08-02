package org.orbisgis.core.renderer.se.parameter.real;

import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.ValueReferenceType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;

public class RealAttribute extends ValueReference implements RealParameter {

    private RealParameterContext ctx;

    /**
     * Create a new instance of RealAttribute, with an empty associated field name.
     * @param fieldName 
     */
    public RealAttribute() {
        ctx = RealParameterContext.REAL_CONTEXT;
    }

    /**
     * Create a new instance of RealAttribute, setting the fieldName of the column where
     * the values will be searched.
     * @param fieldName 
     */
    public RealAttribute(String fieldName) {
        super(fieldName);
        ctx = RealParameterContext.REAL_CONTEXT;
    }

    /**
     * Create a new instance of RealAttribute, setting the fieldName of the column where
     * the values will be searched.
     * @param fieldName 
     */
    public RealAttribute(JAXBElement<ValueReferenceType> expr) throws InvalidStyle {
        super(expr);
        ctx = RealParameterContext.REAL_CONTEXT;
    }

    @Override
    public Double getValue(SpatialDataSourceDecorator sds, long fid) throws ParameterException {
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
