/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.ArrayList;
import java.util.List;
import net.opengis.ows._2.DescriptionType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;

/**
 * This class intends to store a description of a {@code Rule}. It is made of
 * lists of title and abstract, and of sets of keywords. We use lists and set to
 * manage internationalization. We indeed use {@code LocalizedText} directly to
 * store title and abstract, and inderectly to store the keyword (through the
 * {@code Keywords} class.
 * @author alexis
 */
public class Description {

    private List<LocalizedText> titles;
    private List<LocalizedText> abstractTexts;
    private List<Keywords> keywords;

    /**
     * Builds a new, empty, {@code Description}.
     */
    public Description(){
        titles = new ArrayList<LocalizedText>();
        abstractTexts = new ArrayList<LocalizedText>();
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
                titles.add(new LocalizedText(l.getValue(), l.getLang()));
            }
        }
        List<LanguageStringType> dlst = dt.getAbstract();
        if(dlst !=null){
            for(LanguageStringType l : dlst){
                abstractTexts.add(new LocalizedText(l.getValue(), l.getLang()));
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
    public List<LocalizedText> getAbstractTexts() {
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
    public List<LocalizedText> getTitles() {
        return titles;
    }
    /**
     * Gets the JAXB representation of this object.
     * @return
     */
    public DescriptionType getJAXBType() {
        ObjectFactory of = new ObjectFactory();
        DescriptionType dt = of.createDescriptionType();
        List<LanguageStringType> ts = dt.getTitle();
        for(LocalizedText lt : titles){
            ts.add(lt.getJAXBType());
        }
        List<LanguageStringType> abs = dt.getAbstract();
        for(LocalizedText lt : abstractTexts){
            abs.add(lt.getJAXBType());
        }
        List<KeywordsType> kts = dt.getKeywords();
        for(Keywords kw : keywords){
            kts.add(kw.getJAXBType());
        }
        return dt;
    }

}
