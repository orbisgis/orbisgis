package org.orbisgis.wpsservice.model;

import net.opengis.ows._2.LanguageStringType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Sylvain PALOMINOS
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TranslatableString",
        propOrder = {"strings"})
public class TranslatableString {

    @XmlElement(name = "TranslatedStrings", namespace = "http://orbisgis.org")
    private LanguageStringType[] strings;

    public TranslatableString(LanguageStringType[] strings){
        this.strings = strings;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    public TranslatableString(){
    }

    public LanguageStringType[] getStrings() {
        return strings;
    }

    public void setStrings(LanguageStringType[] strings) {
        this.strings = strings;
    }
}
