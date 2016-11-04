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
package org.orbisgis.wpsservice.utils;

import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import org.orbisgis.wpsservice.model.ObjectFactory;
import org.orbisgis.wpsservice.model.TranslatableComplexData;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains methods which does the translation of a process.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessTranslator {


    /**
     * Return the process with the given language translation.
     * If the asked translation doesn't exists, use the english one. If it doesn't exists too, uses one of the others.
     * @param process Process to traduce.
     * @param requestedLanguage Language asked.
     * @param defaultLanguage Default language.
     * @return The traduced process.
     */
    public static final ProcessDescriptionType getTranslatedProcess(
            ProcessDescriptionType process, String requestedLanguage, String defaultLanguage){
        ProcessDescriptionType translatedProcess = new ProcessDescriptionType();
        translatedProcess.setLang(requestedLanguage);
        List<InputDescriptionType> inputList = new ArrayList<>();
        for(InputDescriptionType input : process.getInput()){
            InputDescriptionType translatedInput = new InputDescriptionType();
            JAXBElement jaxbElement = input.getDataDescription();
            if(jaxbElement.getValue() instanceof TranslatableComplexData){
                TranslatableComplexData translatableComplexData = (TranslatableComplexData)jaxbElement.getValue();
                jaxbElement.setValue(translatableComplexData.getTranslatedData(defaultLanguage, requestedLanguage));
            }
            translatedInput.setDataDescription(jaxbElement);
            translatedInput.setMaxOccurs(input.getMaxOccurs());
            translatedInput.setMinOccurs(input.getMinOccurs());
            translateDescriptionType(translatedInput, input, requestedLanguage, defaultLanguage);
            inputList.add(translatedInput);
        }
        translatedProcess.getInput().clear();
        translatedProcess.getInput().addAll(inputList);
        List<OutputDescriptionType> outputList = new ArrayList<>();
        for(OutputDescriptionType output : process.getOutput()){
            OutputDescriptionType translatedOutput = new OutputDescriptionType();
            JAXBElement jaxbElement = output.getDataDescription();
            if(jaxbElement.getValue() instanceof TranslatableComplexData){
                TranslatableComplexData translatableComplexData = (TranslatableComplexData)jaxbElement.getValue();
                jaxbElement.setValue(translatableComplexData.getTranslatedData(defaultLanguage, requestedLanguage));
            }
            translatedOutput.setDataDescription(jaxbElement);
            translateDescriptionType(translatedOutput, output, requestedLanguage, defaultLanguage);
            outputList.add(translatedOutput);
        }
        translatedProcess.getOutput().clear();
        translatedProcess.getOutput().addAll(outputList);
        translateDescriptionType(translatedProcess, process, requestedLanguage, defaultLanguage);
        return translatedProcess;
    }

    /**
     * Sets the given translatedDescriptionType with the traduced elements of the source descriptionType.
     * If the asked translation doesn't exists, use the english one. If it doesn't exists too, uses one of the others.
     *
     * @param translatedDescriptionType Translated DescriptionType.
     * @param descriptionType Source DescriptionType.
     * @param requestedLanguage Language asked.
     * @param defaultLanguage Default language.
     */
    public static final void translateDescriptionType(DescriptionType translatedDescriptionType,
                                                      DescriptionType descriptionType,
                                                      String requestedLanguage,
                                                      String defaultLanguage){
        translatedDescriptionType.setIdentifier(descriptionType.getIdentifier());
        translatedDescriptionType.getMetadata().clear();
        translatedDescriptionType.getMetadata().addAll(descriptionType.getMetadata());
        //Find the good abstract
        LanguageStringType translatedAbstract = null;
        List<String> languageList = new ArrayList<>();
        for(LanguageStringType abstr : descriptionType.getAbstract()){
            if(abstr.getLang() != null) {
                languageList.add(abstr.getLang());
            }
        }
        String language = getBestEffortLanguage(requestedLanguage, defaultLanguage, languageList);
        for(LanguageStringType abstr : descriptionType.getAbstract()){
            if(abstr.getLang() != null && abstr.getLang().equals(language)){
                translatedAbstract = abstr;
            }
        }
        if(translatedAbstract == null){
            if(descriptionType.getAbstract().size() > 0){
                translatedAbstract = descriptionType.getAbstract().get(0);
            }
            else{
                translatedAbstract = new LanguageStringType();
            }
        }
        List<LanguageStringType> abstrList = new ArrayList<>();
        abstrList.add(translatedAbstract);
        translatedDescriptionType.getAbstract().clear();
        translatedDescriptionType.getAbstract().addAll(abstrList);
        //Find the good title
        LanguageStringType translatedTitle = null;
        languageList = new ArrayList<>();
        for(LanguageStringType title : descriptionType.getTitle()){
            if(title.getLang() != null) {
                languageList.add(title.getLang());
            }
        }
        language = getBestEffortLanguage(requestedLanguage, defaultLanguage, languageList);
        for(LanguageStringType title : descriptionType.getTitle()){
            if(title.getLang() != null && title.getLang().equals(language)){
                translatedTitle = title;
            }
        }
        if(translatedTitle == null){
            if(descriptionType.getTitle().size() > 0){
                translatedTitle = descriptionType.getTitle().get(0);
            }
            else{
                translatedTitle = new LanguageStringType();
            }
        }
        List<LanguageStringType> titleList = new ArrayList<>();
        titleList.add(translatedTitle);
        translatedDescriptionType.getTitle().clear();
        translatedDescriptionType.getTitle().addAll(titleList);
        //Find the good keywords
        List<KeywordsType> keywordsList = new ArrayList<>();
        KeywordsType translatedKeywords = new KeywordsType();
        List<LanguageStringType> keywordList = new ArrayList<>();
        for(KeywordsType keywords : descriptionType.getKeywords()) {
            LanguageStringType translatedKeyword = null;
            languageList = new ArrayList<>();
            for(LanguageStringType keyword : keywords.getKeyword()){
                if(keyword != null && keyword.getLang() != null) {
                    languageList.add(keyword.getLang());
                }
            }
            language = getBestEffortLanguage(requestedLanguage, defaultLanguage, languageList);
            for(LanguageStringType keyword : keywords.getKeyword()){
                if(keyword.getLang() != null && keyword.getLang().equals(language)){
                    translatedKeyword = keyword;
                }
            }
            if(translatedKeyword == null){
                if(keywords.getKeyword().size() > 0){
                    translatedKeyword = keywords.getKeyword().get(0);
                }
                else{
                    translatedKeyword = new LanguageStringType();
                }
            }
            keywordList.add(translatedKeyword);
        }
        translatedKeywords.getKeyword().clear();
        translatedKeywords.getKeyword().addAll(keywordList);
        keywordsList.add(translatedKeywords);
        translatedDescriptionType.getKeywords().clear();
        translatedDescriptionType.getKeywords().addAll(keywordsList);
        translatedDescriptionType.setIdentifier(descriptionType.getIdentifier());
        translatedDescriptionType.getMetadata().addAll(descriptionType.getMetadata());
    }

    private static String getBestEffortLanguage(
            String requestedLanguage,
            String defaultLanguage,
            List<String> availableLanguages){

        if(requestedLanguage.equals("*")){
            return defaultLanguage;
        }
        if (availableLanguages.contains(requestedLanguage)) {
            return requestedLanguage;
        }
        for (String language : availableLanguages) {
            if (requestedLanguage.substring(0, 2).equals(language.substring(0, 2))) {
                return language;
            }
        }
        //If not language was found, try to use any language if allowed
        return defaultLanguage;
    }
}
