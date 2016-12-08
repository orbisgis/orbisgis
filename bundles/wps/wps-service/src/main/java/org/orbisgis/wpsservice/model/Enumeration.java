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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.wpsservice.model;

import net.opengis.ows._2.LanguageStringType;
import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;
import org.orbisgis.wpsgroovyapi.attributes.LanguageString;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration model class
 * @author Sylvain PALOMINOS
 **/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Enumeration",
        propOrder = {"values", "names", "defaultValues", "multiSelection", "isEditable"})
public class Enumeration extends ComplexDataType implements TranslatableComplexData{

    /** List of values.*/
    @XmlElement(name = "Value", namespace = "http://orbisgis.org")
    private String[] values;
    /** List of values names.*/
    @XmlElement(name = "Name", namespace = "http://orbisgis.org")
    private TranslatableString[] names;
    /** Default values.*/
    @XmlElement(name = "DefaultValue", namespace = "http://orbisgis.org")
    private String[] defaultValues;
    /** Enable or not the selection of more than one value.*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection = false;
    /** Enable or not the user to use its own value.*/
    @XmlAttribute(name = "isEditable")
    private boolean isEditable = false;

    /**
     * Main constructor.
     * @param formatList Formats of the data accepted.
     * @param valueList List of values.
     * @param defaultValues Default value. If null, no default value.
     * @throws MalformedScriptException
     */
    public Enumeration(List<Format> formatList, String[] valueList, String[] defaultValues) throws MalformedScriptException {
        format = formatList;
        this.values = valueList;
        this.defaultValues = defaultValues;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected Enumeration(){
        super();
    }

    /**
     * Returns the list of the enumeration value.
     * @return The list of values.
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Returns the default values.
     * @return The default values.
     */
    public String[] getDefaultValues() {
        return defaultValues;
    }

    /**
     * Returns true if more than one value can be selected, false otherwise.
     * @return True if more than one value can be selected, false otherwise.
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than one value.
     * @param multiSelection True if more than one value can be selected, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    /**
     * Returns true if the user can use a custom value, false otherwise.
     * @return True if the user can use a custom value, false otherwise.
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Sets if the user can use a custom value.
     * @param editable True if the user can use a custom value, false otherwise.
     */
    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    /**
     * Sets the names of the values. The names will be only used for the displaying.
     * @param names String array of the names. It should have the same size of the values array.
     */
    public void setValuesNames(TranslatableString[] names){
        this.names = names;
    }

    /**
     * Returns the array of the values name.
     * @return The array of the values name.
     */
    public TranslatableString[] getValuesNames(){
        return names;
    }

    @Override
    public ComplexDataType getTranslatedData(String serverLanguage, String clientLanguages) {
        try {
            Enumeration enumeration = new Enumeration(format, values, defaultValues);
            enumeration.setEditable(this.isEditable());
            enumeration.setMultiSelection(this.isMultiSelection());
            List<TranslatableString> translatedNames = new ArrayList<>();
            if(this.getValuesNames() != null) {
                for (TranslatableString translatableString : this.getValuesNames()) {
                    String clientLanguageTranslation = null;
                    String subClientLanguageTranslation = null;
                    String serverLanguageTranslation = null;
                    for (LanguageStringType stringType : translatableString.getStrings()) {
                        if (stringType.getLang().equals(clientLanguages)) {
                            clientLanguageTranslation = stringType.getValue();
                        } else if (stringType.getLang().equals(clientLanguages.substring(0, 2))) {
                            subClientLanguageTranslation = stringType.getValue();
                        } else if (stringType.getLang().equals(serverLanguage)) {
                            serverLanguageTranslation = stringType.getValue();
                        }
                    }
                    LanguageStringType language = new LanguageStringType();
                    if (clientLanguageTranslation != null) {
                        language.setLang(clientLanguageTranslation);
                        language.setValue(clientLanguages);
                    } else if (subClientLanguageTranslation != null) {
                        language.setLang(subClientLanguageTranslation);
                        language.setValue(clientLanguages.substring(0, 2));
                    } else if (serverLanguageTranslation != null) {
                        language.setLang(serverLanguageTranslation);
                        language.setValue(serverLanguage);
                    }
                    else {
                        language.setLang(translatableString.getStrings()[0].getValue());
                        language.setValue(translatableString.getStrings()[0].getLang());
                    }
                    TranslatableString str = new TranslatableString();
                    str.setStrings(new LanguageStringType[]{language});
                    translatedNames.add(str);
                }
                enumeration.setValuesNames(translatedNames.toArray(new TranslatableString[translatedNames.size()]));
            }
            return enumeration;
        } catch (MalformedScriptException ignored) {}
        return this;
    }
}
