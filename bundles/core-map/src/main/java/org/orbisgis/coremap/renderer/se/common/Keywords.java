/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.ows._2.ObjectFactory;
import org.orbisgis.coremap.renderer.se.label.StyledText;

/**
 * Keywords are list of {@code LocalizedText}, associated with an optional
 * type. They are used to describe some objects (Rule, Style...).</p>
 * <p>A {@code Keywords} can be associated to a code type and a code space. This
 * way, it becomes possible to associate a dictionary, thesaurus or authority to
 * the list of keywords contained in the {@code Keywords} instance.</p>
 * <p>Note that
 * the authority is defined only with the code space (returned by the {@link
 * Keywords#getSpace } method). The URI is not stored here. As it is used to
 * distinguish {@code Keywords} instances, this would duplicate data.
 * @author Alexis Guéganno
 */
public class Keywords {

    /*
     * We can't use a HashMap<Locale, String> here, as we have to be able to
     * store multiple keywords with the same Locale instance.
     */
    private TreeSet<LocalizedText> keywords;
    private String type;
    private LocaleAndTextComparator comp=new LocaleAndTextComparator();

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
    public Keywords(KeywordsType kt) {
        this();
        if(kt.getKeyword() != null){
            for(LanguageStringType k : kt.getKeyword()){
                keywords.add(new LocalizedText(k.getValue(), new Locale(k.getLang() == null ? "" : k.getLang())));
            }
        }
        if(kt.getType() != null){
            CodeType ct = kt.getType();
            type = ct.getValue();
        }
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
    public SortedSet<LocalizedText> getKeywords(Locale l){
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
    public SortedSet<LocalizedText> getKeywords(){
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
            ret.setType(ct);
        }
        return ret;
    }

}
