package org.orbisgis.core.renderer.se.parameter;

import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.data.feature.Feature;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.ogc.ObjectFactory;
import org.orbisgis.core.renderer.persistance.ogc.PropertyNameType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;

public abstract class PropertyName implements SeParameter {

    public PropertyName(){
        this.fieldId = -1;
    }

    public PropertyName(String fieldName){
        this.fieldId = -1;
        this.fieldName = fieldName;
    }

    public PropertyName(String fieldName, DataSource ds) throws DriverException{
        setColumnName(fieldName, ds);
    }

    public PropertyName(JAXBElement<PropertyNameType> expr) {
        if (expr.getValue().getContent().size() == 1) {
            this.fieldName = (String) expr.getValue().getContent().get(0);
            this.fieldId = -1;
        } else {
            System.out.println("Error in PropertyName(JAXBElement)");
        }
    }

    @Override
    public boolean dependsOnFeature() {
        return true;
    }


    public void setColumnName(String fieldName, DataSource ds) throws DriverException{
        // look for field before assigning the name !
        this.fieldId = ds.getFieldIndexByName(fieldName);
        this.fieldName = fieldName;
    }

    public String getColumnName(){
        return fieldName;
    }

    public Value getFieldValue(Feature feat) throws DriverException{
        if (this.fieldId == -1){
            this.fieldId = feat.getMetadata().getFieldIndex(fieldName);
        }
        return feat.getValue(fieldId);
    }


    @Override
    public ParameterValueType getJAXBParameterValueType()
    {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.getJAXBExpressionType());
        return p;
    }
    

    @Override
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
        PropertyNameType p = new PropertyNameType();
        p.getContent().add(fieldName);
        ObjectFactory of = new ObjectFactory();
        return of.createPropertyName(p);
    }

    protected String fieldName;
    private int fieldId;

}
