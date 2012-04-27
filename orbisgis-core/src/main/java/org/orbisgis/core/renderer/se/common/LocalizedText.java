/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.Locale;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;

/**
 * Basically a {@code String} associated to a {@code Locale} instance.
 * @author alexis
 */
public class LocalizedText {

    private String content;
    private Locale locale;

    /**
     * Build a new instance of {@code LocalizedText} with the given text.
     * {@code loc} is used to build a {@code Locale} instance.
     * @param text
     * @param loc
     * @throws IllegalArgumentException if {@code loc} can't be used safely to
     * build a {@code Locale}.
     */
    public LocalizedText(String text, String loc){
        content = text;
        if(loc != null && validateLocale(loc)){
            locale = new Locale(loc);
        }
    }

    /**
     * Do a pretty naive validation about the structure of the given {@code
     * String}. It is absolutely not imperfect, but faster than retrieving all
     * the available {@code Locale} instances available in the current runtime
     * environment.
     * @param loc
     * @return
     */
    private boolean validateLocale(String loc){
        String[] parts = loc.split("_");
        if(parts.length == 1){
            return parts[0].length() == 2;
        } else if(parts.length == 2 || parts.length == 3){
            return parts[0].length() == 2 && parts[1].length() == 2;
        } else {
            return false;
        }
    }

    /**
     * Gets the content of this {@code LocalizedText}.
     * @return
     */
    public String getValue() {
        return content;
    }

    /**
     * Sets the content of this {@code LocalizedText}.
     * @param content
     */
    public void setValue(String content) {
        this.content = content;
    }

    /**
     * Gets the {@code Locale} associated to this text.
     * @return
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Sets the {@code Locale} associated to this text.
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Gets the JAXB representation of this object.
     * @return
     */
    public LanguageStringType getJAXBType(){
        ObjectFactory of = new ObjectFactory();
        LanguageStringType lst = of.createLanguageStringType();
        lst.setLang(locale != null ? getLocale().toString() : "");
        lst.setValue(getValue());
        return lst;
    }

}
