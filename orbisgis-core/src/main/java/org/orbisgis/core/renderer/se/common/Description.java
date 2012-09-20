/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import net.opengis.ows._2.*;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

/**
 * This class intends to store a description of a {@code Rule}. It is made of
 * lists of title and abstract, and of sets of keywords. This class is support
 * to manage internationalization. As there can be only one title and one
 * abstract per language, we use a {@code HashMap} to manage them. Keywords are
 * stored in a dedicated class.</p>
 * <p>According to 0GC 06-121r9, there shall be at most one title and/or
 * abstract per language. However, they may be many keywords associated to the
 * same language in a {@code Keywords} instance. In a {@code Description}
 * instance, there shall be at most one {@code Keywords} instance associated
 * to an authority.</p>
 * <p>Authorities are defined only considering the URI contained in the {@code
 * codeSpace} attribute of the {@code CodeType} element contained in {@code
 * Keywords}, according to 0GC 06-121r9. As there shall not be more than one
 * {@code Keywords} instance associated to a single authority, we map keywords
 * on this authority, ie only on the {@code URI}. The {@code CodeType} is not
 * considered meaningful in this mapping.
 * @author Alexis Guéganno
 * @see Keywords
 */
public class Description {

    private HashMap<Locale, String> titles;
    private HashMap<Locale, String> abstractTexts;
    private HashMap<URI,Keywords> keywords;

    /**
     * Builds a new, empty, {@code Description}.
     */
    public Description(){
        titles = new HashMap<Locale, String>();
        abstractTexts = new HashMap<Locale, String>();
        keywords = new  HashMap<URI,Keywords>();
    }

    /**
     * Builds a new {@code Description} from the given
     * {@code DescriptionType}.
     * @param dt
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle  
     */
    public Description(DescriptionType dt) throws InvalidStyle{
        this();
        List<LanguageStringType> tlst = dt.getTitle();
        if(tlst != null){
            for(LanguageStringType l : tlst){
                String lang = l.getLang();
                Locale loc = LocalizedText.forLanguageTag(lang);
                titles.put(loc,l.getValue());
            }
        }
        List<LanguageStringType> dlst = dt.getAbstract();
        if(dlst !=null){
            for(LanguageStringType l : dlst){
                String lang = l.getLang();
                Locale loc = LocalizedText.forLanguageTag(lang);
                abstractTexts.put(loc,l.getValue());
            }
        }
        List<KeywordsType> lkt = dt.getKeywords();
        if(lkt != null){
            for(KeywordsType kt : lkt){
                putKeywordsType(kt);
            }
        }
    }

        @Override
        public boolean equals(Object obj) {
                if (!(obj instanceof Description)) {
                        return false;
                }
                final Description other = (Description) obj;
                if (this.titles != other.titles && (this.titles == null || !this.titles.equals(other.titles))) {
                        return false;
                }
                if (this.abstractTexts != other.abstractTexts && (this.abstractTexts == null || !this.abstractTexts.equals(other.abstractTexts))) {
                        return false;
                }
                if (this.keywords != other.keywords && (this.keywords == null || !this.keywords.equals(other.keywords))) {
                        return false;
                }
                return true;
        }

        @Override
        public int hashCode() {
                int hash = 7;
                hash = 67 * hash + (this.titles != null ? this.titles.hashCode() : 0);
                hash = 67 * hash + (this.abstractTexts != null ? this.abstractTexts.hashCode() : 0);
                hash = 67 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
                return hash;
        }

    /**
     * Find the most appropriate title close to the default Locale
     * @return Title or Null if there is no title at all
     */
    public String getDefaultTitle() {
            // Find the title with default locale
            String title = getTitle(Locale.getDefault());
            if(title!=null) {
                    return title;
            }
            // Search with the lang only
            title = getTitle(new Locale(Locale.getDefault().getLanguage()));
            if(title!=null) {
                    return title;
            }
            // Get the first title
            if(!titles.isEmpty()) {
                    return titles.values().iterator().next();
            } else {
                    return null;
            }
    }
    /**
     * Find the most appropriate title close to the default Locale
     * @return Title or Null if there is no title at all
     */
    public String getDefaultAbstract() {
            // Find the title with default locale
            String defaultAbstract = getAbstract(Locale.getDefault());
            if(defaultAbstract!=null) {
                    return defaultAbstract;
            }
            // Search with the lang only
            defaultAbstract = getAbstract(new Locale(Locale.getDefault().getLanguage()));
            if(defaultAbstract!=null) {
                    return defaultAbstract;
            }
            // Get the first title
            if(!abstractTexts.isEmpty()) {
                    return abstractTexts.values().iterator().next();
            } else {
                    return null;
            }
    }
    
    private void putKeywordsType(KeywordsType kt) throws InvalidStyle {
        CodeType ct = kt.getType();
        if(ct!=null){
            String sp = ct.getCodeSpace();
            if(sp != null){
                try{
                    keywords.put(new URI(kt.getType().getCodeSpace()),new Keywords(kt));
                } catch (URISyntaxException ex) {
                    throw new InvalidStyle("The provided URI is not valid.", ex);
                }
            } else {
                keywords.put(null,new Keywords(kt));
            }
        } else {
            keywords.put(null,new Keywords(kt));
        }

    }

    /**
     * Gets the list of localized abstracts registered in this {@code
     * Description}.
     * @return
     */
    public HashMap<Locale, String> getAbstractTexts() {
        return abstractTexts;
    }

    /**
     * Gets the list of localized keywords registered in this {@code
     * Description}.
     * @return
     */
    public HashMap<URI,Keywords> getKeywords() {
        return keywords;
    }

    /**
     * Gets the set of keywords associated to this {@code URI}.
     * @param uri
     * @return
     */
    public Keywords getKeywords(URI uri){
        return keywords.get(uri);
    }

    /**
     * Sets the set of keywords associated to this {@code URI}.
     * @param uri
     * @param keys
     */
    public void putKeywords(URI uri, Keywords keys){
        keywords.put(uri, keys);
    }

    /**
     * Removes the set of keywords associated to this {@code URI}.
     * @param uri
     * @return
     * The {@code Keywords} instance that has just been removed from the map
     * of Keywords.
     */
    public Keywords removeKeywords(URI uri){
        return keywords.remove(uri);
    }

    /**
     * Gets the list of localized titles registered in this {@code
     * Description}.
     * @return
     */
    public HashMap<Locale, String> getTitles() {
        return titles;
    }
    
    /**
     * Sets the list of localized titles registered in this {@code
     * Description}.
     * @param titles The map of titles
     */
    public void setTitles(HashMap<Locale,String> titles) {
            this.titles = titles;
    }

    /**
     * Adds a title to this {@code Description}, associated to the given {@code
     * Locale}.
     * @param text
     * @param locale
     * @return
     * The title that was previously associated to {@code Locale}, if any.
     */
    public String addTitle(Locale locale,String text){
        return titles.put(locale, text);
    }

    /**
     * Gets the title of this {@code Description} associated to the given {@code
     * Locale}.
     * @param locale
     * @return
     */
    public String getTitle(Locale locale){
        return titles.get(locale);
    }

    /**
     * Adds an abstract to this {@code Description}, associated to the given
     * {@code Locale}.
     * @param text
     * @param locale
     * @return
     * The title that was previously associated to {@code Locale}, if any.
     */
    public String addAbstract(Locale locale,String text){
        return abstractTexts.put(locale, text);
    }

    /**
     * Gets the abstract of this {@code Description} associated to the given
     * {@code Locale}.
     * @param locale
     * @return
     */
    public String getAbstract(Locale locale){
        return abstractTexts.get(locale);
    }
    
    /**
     * Initialise the data of the provided description type with
     * the JAXB representation of this object.
     * @param dt Instance of DescriptionType
     */
    public void initJAXBType(DescriptionType dt) {
        ObjectFactory of = new ObjectFactory();
        List<LanguageStringType> ts = dt.getTitle();
        for(Map.Entry<Locale, String> lt : titles.entrySet()){
            LanguageStringType lst = of.createLanguageStringType();
            lst.setLang(lt.getKey()!= null ? LocalizedText.toLanguageTag(lt.getKey()) : "");
            lst.setValue(lt.getValue());
            ts.add(lst);
        }
        List<LanguageStringType> abs = dt.getAbstract();
        for(Map.Entry<Locale, String> lt : abstractTexts.entrySet()){
            LanguageStringType lst = of.createLanguageStringType();
            lst.setLang(lt.getKey()!= null ? LocalizedText.toLanguageTag(lt.getKey()) : "");
            lst.setValue(lt.getValue());
            abs.add(lst);
        }
        List<KeywordsType> kts = dt.getKeywords();
        Set<Map.Entry<URI, Keywords>> registered = keywords.entrySet();
        for(Map.Entry<URI, Keywords> entry : registered){
            KeywordsType kwjt = entry.getValue().getJAXBType();
            if(entry.getKey() != null && entry.getValue().getType() != null){
                kwjt.getType().setCodeSpace(entry.getKey().toString());
            }
            kts.add(kwjt);
        }            
    }
    
    /**
     * Gets the JAXB representation of this object.
     * @return
     */
    public DescriptionType getJAXBType() {
        ObjectFactory of = new ObjectFactory();
        DescriptionType dt = of.createDescriptionType();
        initJAXBType(dt);
        return dt;
    }
    
}
