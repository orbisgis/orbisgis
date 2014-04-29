/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.se.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Basically a {@code String} associated to a {@code Locale} instance.
 * @author Alexis Guéganno
 */
public class LocalizedText {

    private static final String lang_sep = "-";
    
    private String content;
    private Locale locale;
    private static final I18n I18N = I18nFactory.getI18n(LocalizedText.class);
            
    /**
     * Builds a new instance of {@code LocalizedText} with the given {@code
     * String} and {@code Locale}.
     * @param string The data string
     * @param l The Locale associated to {@code string}
     */
    LocalizedText(String string, Locale l) {
        content = string;
        locale = l;
    }

    /**
     * Gets the content of this {@code LocalizedText}.
     * @return The value contained in this {@code LocalizedText}.
     */
    public String getValue() {
        return content;
    }

    /**
     * Sets the content of this {@code LocalizedText}.
     * @param content The new content of this {@code LocalizedText}.
     */
    public void setValue(String content) {
        this.content = content;
    }

    /**
     * Gets the {@code Locale} associated to this text.
     * @return The {@link Locale} associated to this {@code LocalizedText}
     */
    public Locale getLocale() {
        return locale;
    }

            
    /**
     * Sets the {@code Locale} associated to this text.
     * @param locale The new Locale for this {@code LocalizedText}.
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    /**
     * Separate a Locale from one to three parts
     * Note: ([a-zA-Z]{2,8})-?([a-zA-Z]{2}|[0-9]{3})?-?([0-9a-zA-Z]*)? regex should work
     * @param localeRepresentation A string that conforms to the IETF BCP 47 standard
     * @return An array of [Language,Country,Variant]
     */
    public static String[] separateLocale(String localeRepresentation) {
            List<String> ret = new ArrayList<String>(3);
            int start = 0;
            do {
                    int end = localeRepresentation.indexOf(lang_sep, start);
                    if(ret.size()==2 || end==-1) {
                            if(start<localeRepresentation.length()) {
                                ret.add(localeRepresentation.substring(start));
                            }
                            break;
                    } else {
                            ret.add(localeRepresentation.substring(start,end));
                    }
                    start = end + 1;
            } while(start!=0);
            return ret.toArray(new String[ret.size()]);
    }
    /**
     * @param localeRepresentation  a string that conforms to the IETF BCP 47 standard
     * @return Locale instance or null if the specified string is not a valid Locale representation
     * @link http://docs.oracle.com/javase/tutorial/i18n/locale/create.html#factory
     */
    public static Locale forLanguageTag(String localeRepresentation) {
            String[] parts = separateLocale(localeRepresentation);
            if(localeRepresentation.equals("und")) {
                    return null;
            }
            switch(parts.length) {
                    case 1:
                            return new Locale(localeRepresentation);
                    case 2:
                            return new Locale(parts[0],parts[1]);
                    case 3:
                            return new Locale(parts[0],parts[1],parts[2]);
                    default:
                            return null;
            }
    }
    
    /**
     * Validation of the language part of the java.util.Locale
     * @link http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#def_region
     * @param language the String we analyse
     * @return true if the input String is a valid language
     */
    public static boolean validateLanguage(String language) {
            return language.matches("[a-zA-Z]{2,8}");
    }
    
    /**
     * Validation of the country part of the java.util.Locale
     * @link http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#def_region
     * @param country the String we analyse
     * @return true if the input String is a valid country
     */
    public static boolean validateCountry(String country) {
            return country.matches("[a-zA-Z]{2}|[0-9]{3}");
    }
    
    /**
     * Validation of the variant part of the java.util.Locale
     * @link http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#def_region
     * @param variant the String we analyse
     * @return true if the input String is a valid variant
     */
    public static boolean validateVariant(String variant) {
            return true;
    }
    
    /**
     * Serialisation of a Locale, simple version of the java 7 function
     * @param locale A locale instance
     * @return A string that conforms to the IETF BCP 47 standard
     * @link http://docs.oracle.com/javase/7/docs/api/java/util/Locale.html#toLanguageTag%28%29
     */
    public static String toLanguageTag(Locale locale) {
            String[] parts = locale.toString().split("_");
            StringBuilder ret = new StringBuilder();
            if(parts.length>=1) {
                    //Language
                    if(validateLanguage(parts[0])) {
                            ret.append(parts[0]);
                    } else {
                            return "und";
                    }
                    if(parts.length==1) {
                            return ret.toString();
                    }
                    //Country
                    if(validateCountry(parts[1])) {
                            ret.append("-");
                            ret.append(parts[1]);
                    } else {
                            return ret.toString();                            
                    }
                    if(parts.length==2) {
                            return ret.toString();
                    }
                    //Variant
                    if(validateVariant(parts[2])) {
                            ret.append("-");
                            ret.append(parts[2]);  
                    }
                    return ret.toString();
            } else {
                    return "und";
            }
            
    }
    
    /**
     * Gets the JaXB representation of this object.
     * @return The JaXB representation of this object.
     */
    public LanguageStringType getJAXBType(){
        ObjectFactory of = new ObjectFactory();
        LanguageStringType lst = of.createLanguageStringType();
        lst.setLang(locale != null ? toLanguageTag(getLocale()) : "");
        lst.setValue(getValue());
        return lst;
    }

    /**
     * Two {@code LocalizedText} are equal if and only if their associated
     * {@code Locale} and content are equal.
     * @param obj
     * Hopefully a {@code LocalizedText} instance.
     * @return true if the input and this are equal.
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
