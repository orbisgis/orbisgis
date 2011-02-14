package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.persistance.ogc.LiteralType;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class StringLiteral extends Literal implements StringParameter{

    private String v;

    public StringLiteral(){
        v = "";
    }

    public StringLiteral(String value){
        v = value;
    }

    public StringLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public String getValue(SpatialDataSourceDecorator sds, long fid){
        return v;
    }

    public void setValue(String value){
        v = value;
    }

    @Override
    public String toString(){
        return v;
    }

}
