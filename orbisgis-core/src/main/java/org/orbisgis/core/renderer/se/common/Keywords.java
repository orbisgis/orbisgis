/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

/**
 * Keywords are list of {@code LocalizedText}, associated with an optional
 * type. They are used to describe some objects (Rule, Style...).</p>
 * <p>A {@code Keywords} can be associated to a code type and a code space. This
 * way, it becomes possible to associate a dictionary, thesaurus or authority to
 * the list of keywords contained in the {@code Keywords} instance. Note that
 * the authority is defined only with the code space (returned by the {@link
 * Keywords#getSpace() } method).
 * @author alexis
 */
public class Keywords implements Comparable<Keywords>{

    /*
     * We can't use a HashMap<Locale, String> here, as we have to be able to
     * store multiple keywords with the same Locale instance.
     */
    private TreeSet<LocalizedText> keywords;
    private String type;
    private URI space;
    LocaleAndTextComparator comp=new LocaleAndTextComparator();

    /**
     * Builds a new empty {@code Keywords} instance.
     */
    public Keywords() {
        keywords = new TreeSet<LocalizedText>(comp);
    }

    /**
     * Build a new {@code Keywords} instance from the given JAXB object.
     * @param kt
     */
    public Keywords(KeywordsType kt) throws InvalidStyle{
        this();
        if(kt.getKeyword() != null){
            for(LanguageStringType k : kt.getKeyword()){
                keywords.add(new LocalizedText(k.getValue(), new Locale(k.getLang() == null ? "" : k.getLang())));
            }
        }
        if(kt.getType() != null){
            try {
                CodeType ct = kt.getType();
                type = ct.getValue();
                String sp = ct.getCodeSpace();
                if(sp != null){
                    space = new URI(sp);
                }
            } catch (URISyntaxException ex) {
                throw new InvalidStyle("The provided URI is not valid.", ex);
            }
        }
    }

    /**
     * Gets the code space where the keywords contained in {@code this} are.
     * declared.
     * @return
     */
    public URI getSpace() {
        return space;
    }

    /**
     * Sets the code space where the keywords contained in {@code this} are.
     * @param space
     */
    public void setSpace(URI space) {
        this.space = space;
    }

    /**
     * Gets the code type of the keywords contained in {@code this}.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the code type of the keywords contained in {@code this}.
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Adds a {@link LocalizedText} to this set of keywords.
     * @param lt
     */
    public void addLocalizedText(LocalizedText lt){
        keywords.add(lt);
    }

    /**
     * Gets the set of {@link LocalizedText} that are registered using the
     * {@code Locale} given in argument.
     * @param l
     * @return
     */
    public SortedSet<LocalizedText> getKeyworkds(Locale l){
        SortedSet<LocalizedText> st = keywords.tailSet(new LocalizedText("", l));
        TreeSet ret = new TreeSet(comp);
        for (LocalizedText localizedText : st) {
            if((l== null && localizedText.getLocale() == null) || l.equals(localizedText.getLocale())){
                ret.add(localizedText);
            } else {
                break;
            }
        }
        return ret;
    }

    /**
     * Gets all the {@link StyledText} associated to this {@code Keywords}
     * instance.
     * @return
     */
    public SortedSet<LocalizedText> getKeyworkds(){
        return keywords;
    }

    /**
     * Gets the JAXB representation of this object.
     * @return
     */
    public KeywordsType getJAXBType() {
        ObjectFactory of = new ObjectFactory();
        KeywordsType ret = of.createKeywordsType();
        List<LanguageStringType> lsts = ret.getKeyword();
        for(LocalizedText l : keywords){
            lsts.add(l.getJAXBType());
        }
        if(type != null && !type.isEmpty()){
            CodeType ct = of.createCodeType();
            ct.setValue(type);
            if(space != null){
                ct.setCodeSpace(space.toString());
            }
            ret.setType(ct);
        }
        return ret;
    }

    /**
     * We make a comparison based on the inner URI, considering that {@code
     * null} is inferior to any instanciated URI.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Keywords o) {
        if(space == null){
            if(o.space == null){
                return 0;
            } else {
                return -1;
            }
        } else {
            if(o.space == null){
                return 1;
            } else {
                return space.compareTo(o.space);
            }
        }
    }

}
