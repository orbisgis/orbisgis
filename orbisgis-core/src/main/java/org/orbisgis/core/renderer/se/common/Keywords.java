/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.common;

import java.util.ArrayList;
import java.util.List;
import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;

/**
 * Keywords are list of {@code LocaliezdText}, associated with an optional
 * type. They are used to describe some objects (Rule, Style...)/
 * @author alexis
 */
public class Keywords {

    private List<LocalizedText> keywords;
    private String type;
    private String space;

    /**
     * Builds a new empty {@code Keywords} instance.
     */
    public Keywords() {
        keywords = new ArrayList<LocalizedText>();
    }

    /**
     * Build a new {@code Keywords} instance from the given JAXB object.
     * @param kt
     */
    public Keywords(KeywordsType kt){
        keywords = new ArrayList<LocalizedText>();
        if(kt.getKeyword() != null){
            for(LanguageStringType k : kt.getKeyword()){
                keywords.add(new LocalizedText(k.getValue(), k.getLang()));
            }
        }
        if(kt.getType() != null){
            type = kt.getType().getValue();
            space = kt.getType().getCodeSpace();
        }
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
            if(space != null && !space.isEmpty()){
                ct.setCodeSpace(space);
            }
            ret.setType(ct);
        }
        return ret;
    }

}
