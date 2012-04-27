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

/**
 * This class intends to store a description of a {@code Rule}. It is made of
 * lists of title and abstract, and of sets of keywords. This class is support
 * to manage internationalization. As there can be only one title and one
 * abstract per language, we use a {@code HashMap} to manage them. Keywords are
 * stored in a dedicated class.
 * @author alexis
 * @see Keywords
 */
public class Description {

    private HashMap<Locale, String> titles;
    private HashMap<Locale, String> abstractTexts;
    private List<Keywords> keywords;

    /**
     * Builds a new, empty, {@code Description}.
     */
    public Description(){
        titles = new HashMap<Locale, String>();
        abstractTexts = new HashMap<Locale, String>();
        keywords = new ArrayList<Keywords>();
    }

    /**
     * Builds a new {@code Description} from the given
     * {@code DescriptionType}.
     * @param dt
     */
    public Description(DescriptionType dt){
        this();
        List<LanguageStringType> tlst = dt.getTitle();
        if(tlst != null){
            for(LanguageStringType l : tlst){
                titles.put(new Locale(l.getLang() == null ? "" : l.getLang()),l.getValue());
            }
        }
        List<LanguageStringType> dlst = dt.getAbstract();
        if(dlst !=null){
            for(LanguageStringType l : dlst){
                abstractTexts.put(new Locale(l.getLang() == null ? "" : l.getLang()),l.getValue());
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
    public List<Keywords> getKeywords() {
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

}
