package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.parameter.Literal;

public class StringLiteral extends Literal implements StringParameter{

    private String v;
    private String[] restriction;

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

    @Override
    public void setRestrictionTo(String[] list) {
        restriction = list;
    }

    public String[] getRestriction(){
        return restriction;
    }

}
