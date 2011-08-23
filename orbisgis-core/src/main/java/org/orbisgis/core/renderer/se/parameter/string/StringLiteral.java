package org.orbisgis.core.renderer.se.parameter.string;

import javax.xml.bind.JAXBElement;
import net.opengis.fes._2.LiteralType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.renderer.se.parameter.Literal;

/**
 * A {@code StringParameter} with a stored, inner {@code String} value.
 * @author alexis
 */
public class StringLiteral extends Literal implements StringParameter{

    private String v;
    private String[] restriction;

    /**
     * Instanciates a new {@code StringLiteral} with an empty inner {@code String} value.
     */
    public StringLiteral(){
        v = "";
    }

    /**
     * Instanciates a new {@code StringLiteral} with inner {@code String} set to {@code value}.
     */
    public StringLiteral(String value){
        v = value;
    }

    /**
     * Builds a new {@code StringLiteral} from the given {@code JAXBElement.}.
     */
    public StringLiteral(JAXBElement<LiteralType> l) {
        this(l.getValue().getContent().get(0).toString());
    }

    @Override
    public String getValue(SpatialDataSourceDecorator sds, long fid){
        return v;
    }

    /**
     * Set the inner {@code String} of this {@code StringLiteral} to {@code value}.
     */
    public void setValue(String value){
        v = value;
    }

    @Override
    public String toString(){
        return v;
    }

    @Override
    public void setRestrictionTo(String[] list) {
        restriction = list.clone();
    }

    /**
     * Get the list of restrictions currently associated to this {@code StringLiteral}
     * @return 
     * The restrictions as an array of {@code String}
     */
    public String[] getRestriction(){
        return restriction;
    }

}
