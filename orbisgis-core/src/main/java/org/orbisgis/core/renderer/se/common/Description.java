/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.*;
import net.opengis.ows._2.DescriptionType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

/**
 * This class intends to store a description of a {@code Rule}. It is made of
 * lists of title and abstract, and of sets of keywords. This class is support
 * to manage internationalization. As there can be only one title and one
 * abstract per language, we use a {@code HashMap} to manage them. Keywords are
 * stored in a dedicated class.</p>
 * <p>According to 0GC 06-121r9, there shall be at most one title and/or
 * abstract per langugage. However, they may be many keywords associated to the
 * same language in a {@code Keywords} instance. In a {@code Description}
 * instance, there shall be at most one {@code Keywords} instance associated
 * to an authority.
 * @author alexis
 * @see Keywords
 */
public class Description {

    private HashMap<Locale, String> titles;
    private HashMap<Locale, String> abstractTexts;
    private TreeSet<Keywords> keywords;

    /**
     * Builds a new, empty, {@code Description}.
     */
    public Description(){
        titles = new HashMap<Locale, String>();
        abstractTexts = new HashMap<Locale, String>();
        keywords = new TreeSet<Keywords>();
    }

    /**
     * Builds a new {@code Description} from the given
     * {@code DescriptionType}.
     * @param dt
     */
    public Description(DescriptionType dt) throws InvalidStyle{
        this();
        List<LanguageStringType> tlst = dt.getTitle();
        if(tlst != null){
            for(LanguageStringType l : tlst){
                String lang = l.getLang();
                Locale loc = lang != null && validateLocale(lang) ? new Locale(lang) : null;
                titles.put(loc,l.getValue());
            }
        }
        List<LanguageStringType> dlst = dt.getAbstract();
        if(dlst !=null){
            for(LanguageStringType l : dlst){
                String lang = l.getLang();
                Locale loc = lang != null && validateLocale(lang) ? new Locale(lang) : null;
                abstractTexts.put(loc,l.getValue());
            }
        }
        List<KeywordsType> lkt = dt.getKeywords();
        if(lkt != null){
            for(KeywordsType kt : lkt){
                keywords.add(new Keywords(kt));
            }
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
    public TreeSet<Keywords> getKeywords() {
        return keywords;
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
     * Gets the JAXB representation of this object.
     * @return
     */
    public DescriptionType getJAXBType() {
        ObjectFactory of = new ObjectFactory();
        DescriptionType dt = of.createDescriptionType();
        List<LanguageStringType> ts = dt.getTitle();
        for(Map.Entry<Locale, String> lt : titles.entrySet()){
            LanguageStringType lst = of.createLanguageStringType();
            lst.setLang(lt.getKey()!= null ? lt.getKey().toString() : "");
            lst.setValue(lt.getValue());
            ts.add(lst);
        }
        List<LanguageStringType> abs = dt.getAbstract();
        for(Map.Entry<Locale, String> lt : abstractTexts.entrySet()){
            LanguageStringType lst = of.createLanguageStringType();
            lst.setLang(lt.getKey()!= null ? lt.getKey().toString() : "");
            lst.setValue(lt.getValue());
            abs.add(lst);
        }
        List<KeywordsType> kts = dt.getKeywords();
        for(Keywords kw : keywords){
            kts.add(kw.getJAXBType());
        }
        return dt;
    }


    /**
     * Does a pretty naive validation about the structure of the given {@code
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
}
