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
     * Builds a new instance of {@code LocalizedText} with the given {@code
     * String} and {@code Locale}.
     * @param string
     * @param l
     */
    LocalizedText(String string, Locale l) {
        content = string;
        locale = l;
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

    /**
     * Two {@code LocalizedText} are equal if and only if their associated
     * {@code Locale} and content are equal.
     * @param obj
     * Hopefully a {@code LocalizedText} instance.
     * @return
     */
    @Override
    public boolean equals(Object obj){
            if(obj instanceof LocalizedText){
                LocalizedText lt = (LocalizedText)obj;
                boolean locs = locale == null ? lt.locale == null : locale.equals(lt.locale);
                boolean conts = content == null ? lt.content == null : content.equals(lt.content);
                return locs && conts;
            }
            return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.content != null ? this.content.hashCode() : 0);
        hash = 59 * hash + (this.locale != null ? this.locale.hashCode() : 0);
        return hash;
    }

}
