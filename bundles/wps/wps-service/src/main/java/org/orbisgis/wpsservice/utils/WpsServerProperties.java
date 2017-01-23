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

import net.opengis.ows._2.CodeType;
import net.opengis.ows._2.KeywordsType;
import net.opengis.ows._2.LanguageStringType;
import org.orbisgis.frameworkapi.CoreWorkspace;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Properties of the wps server.
 *
 * @author Sylvain PALOMINOS
 */
public class WpsServerProperties {

    /** CoreWorkspace of OrbisGIS */
    private CoreWorkspace coreWorkspace;
    private static final Logger LOGGER = LoggerFactory.getLogger(WpsServerProperties.class);
    private static final I18n I18N = I18nFactory.getI18n(WpsServerProperties.class);
    private static final String SERVER_PROPERTIES = "wpsServer.properties";
    private static final String BASIC_SERVER_PROPERTIES = "basicWpsServer.properties";

    public GlobalProperties GLOBAL_PROPERTIES;
    public ServiceIdentificationProperties SERVICE_IDENTIFICATION_PROPERTIES;

    @Reference
    public void setCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = coreWorkspace;
    }
    public void unsetCoreWorkspace(CoreWorkspace coreWorkspace) {
        this.coreWorkspace = null;
    }

    public WpsServerProperties(){
        if(coreWorkspace != null) {
            Properties wpsProperties = new Properties();
            //Load the property file
            File propertiesFile = new File(coreWorkspace.getWorkspaceFolder() + File.separator + SERVER_PROPERTIES);
            if (propertiesFile.exists()) {
                try {
                    wpsProperties.load(new FileInputStream(propertiesFile));
                } catch (IOException e) {
                    LOGGER.warn(I18N.tr("Unable to restore the wps properties."));
                    wpsProperties = new Properties();
                }
            }
            try {
                GLOBAL_PROPERTIES = new GlobalProperties(wpsProperties);
                SERVICE_IDENTIFICATION_PROPERTIES = new ServiceIdentificationProperties(wpsProperties);
            } catch (Exception e) {
                LOGGER.error(I18N.tr("Unable to load the server configuration.\nCause : {0}\nLoading the default configuration.", e.getMessage()));

                URL url = this.getClass().getClassLoader().getResource(BASIC_SERVER_PROPERTIES);
                if(url == null){
                    LOGGER.error(I18N.tr("Unable to find the basic server properties file."));
                }
                else {
                    Properties properties = new Properties();
                    try {
                        GLOBAL_PROPERTIES = new GlobalProperties(properties);
                        SERVICE_IDENTIFICATION_PROPERTIES = new ServiceIdentificationProperties(properties);
                    } catch (Exception ex) {
                        LOGGER.error(I18N.tr("Unable to load the server configuration.\nCause : {0}\nLoading the default configuration.", ex.getMessage()));
                        GLOBAL_PROPERTIES = null;
                    }
                }
            }
        }
        else{
            LOGGER.warn(I18N.tr("Warning, no CoreWorkspace found. Unable to load the previous state."));
        }
    }


    /** Global properties of the server */
    public class GlobalProperties{
        /** Service provided by the server, WPS by default */
        public final String SERVICE;
        /** Version of the server. */
        public final String SERVER_VERSION;
        /** Supported version of the WPS (1.0.0, 2.0.0 ...). */
        public final String[] SUPPORTED_VERSIONS;
        /** Default languages. */
        public final String DEFAULT_LANGUAGE;
        /** Supported languages. */
        public final String[] SUPPORTED_LANGUAGES;
        /** Supported format for the communication with the client. */
        public final String[] SUPPORTED_FORMATS;
        /** Default languages. */
        public final String JOB_CONTROL_OPTIONS;

        public GlobalProperties(Properties properties) throws Exception {
            SERVICE = properties.getProperty("SERVICE");

            SERVER_VERSION = properties.getProperty("SERVER_VERSION");

            String supportedVersions = properties.getProperty("SUPPORTED_VERSIONS");
            if(supportedVersions == null || supportedVersions.isEmpty()){
                throw new Exception(I18N.tr("The property 'SUPPORTED_VERSIONS' isn't defined"));
            }
            SUPPORTED_VERSIONS = properties.getProperty("SUPPORTED_VERSIONS").split(",");

            String supportedLanguages = properties.getProperty("SUPPORTED_LANGUAGES");
            if(supportedLanguages == null || supportedLanguages.isEmpty()){
                throw new Exception(I18N.tr("The property 'SUPPORTED_LANGUAGES' isn't defined"));
            }
            SUPPORTED_LANGUAGES = properties.getProperty("SUPPORTED_LANGUAGES").split(",");

            DEFAULT_LANGUAGE = properties.getProperty("DEFAULT_LANGUAGE");

            String supportedFormats = properties.getProperty("SUPPORTED_FORMATS");
            if(supportedFormats == null || supportedFormats.isEmpty()){
                throw new Exception(I18N.tr("The property 'SUPPORTED_FORMATS' isn't defined"));
            }
            SUPPORTED_FORMATS = properties.getProperty("SUPPORTED_FORMATS").split(",");

            JOB_CONTROL_OPTIONS = properties.getProperty("JOB_CONTROL_OPTIONS");
        }
    }

    /** Properties associated to the service identification part of the server */
    public class ServiceIdentificationProperties{
        /** Service provided by the server, WPS by default */
        public final CodeType SERVICE_TYPE;
        /** Version of the server. */
        public final String[] SERVICE_TYPE_VERSIONS;
        /** Supported version of the WPS (1.0.0, 2.0.0 ...). */
        public final LanguageStringType[] TITLE;
        /** Default languages. */
        public final LanguageStringType[] ABSTRACT;
        /** Supported languages. */
        public final KeywordsType[] KEYWORDS;
        /** Supported format for the communication with the client. */
        public final String FEES;
        /** Default languages. */
        public final String[] ACCESS_CONSTRAINTS;

        public ServiceIdentificationProperties(Properties properties) throws Exception {
            // Sets the service type property
            SERVICE_TYPE = new CodeType();
            SERVICE_TYPE.setValue(properties.getProperty("SERVICE_TYPE"));

            // Sets the service type version which is an array of values. So first check if the property isn't null or
            // empty.
            String serviceTypeVersions = properties.getProperty("SERVICE_TYPE_VERSIONS");
            if(serviceTypeVersions == null || serviceTypeVersions.isEmpty()){
                throw new Exception(I18N.tr("The property 'SERVICE_TYPE_VERSIONS' isn't defined"));
            }
            SERVICE_TYPE_VERSIONS = properties.getProperty("SERVICE_TYPE_VERSIONS").split(",");

            // Sets the title which is an array of LanguageStringType. So the property is split with the ';' character
            // and the first string is under the first language, the second one in the second language ...
            String title = properties.getProperty("TITLE");
            //First test if the title property was set in the property file
            if(title == null || title.isEmpty()){
                throw new Exception(I18N.tr("The property 'TITLE' isn't defined"));
            }
            //Split the title property string and check if there is enough languages
            String[] titleSplit = title.split(";");
            if(titleSplit.length != GLOBAL_PROPERTIES.SUPPORTED_LANGUAGES.length){
                throw new Exception(I18N.tr("The property 'TITLE' doesn't contain the good number of string."));
            }
            //Sets the title with the constructed LanguageStringType.
            TITLE = new LanguageStringType[titleSplit.length];
            for(int i=0; i<titleSplit.length; i++){
                TITLE[i] = new LanguageStringType();
                TITLE[i].setValue(titleSplit[i]);
                TITLE[i].setLang(GLOBAL_PROPERTIES.SUPPORTED_LANGUAGES[i]);
            }

            //Sets the abstract which, like the title, is composed of an array of LanguageStringType. So the property
            // is split with the character ';' and stored in a LanguageStringType Object with the good language.
            String abstract_ = properties.getProperty("ABSTRACT");
            //First test if the abstract property was set in the property file
            if(abstract_ == null || abstract_.isEmpty()){
                throw new Exception(I18N.tr("The property 'ABSTRACT' isn't defined"));
            }
            //Split the abstract property string and check if there is enough languages
            String[] abstractSplit = abstract_.split(";");
            if(abstractSplit.length != GLOBAL_PROPERTIES.SUPPORTED_LANGUAGES.length){
                throw new Exception(I18N.tr("The property 'ABSTRACT' doesn't contain the good number of string."));
            }
            //Sets the abstract with the constructed LanguageStringType.
            ABSTRACT = new LanguageStringType[abstractSplit.length];
            for(int i=0; i<abstractSplit.length; i++){
                ABSTRACT[i] = new LanguageStringType();
                ABSTRACT[i].setValue(abstractSplit[i]);
                ABSTRACT[i].setLang(GLOBAL_PROPERTIES.SUPPORTED_LANGUAGES[i]);
            }

            //Sets the keywords which, like the title, is composed of an array of LanguageStringType. So the property
            // is split with the character ';' for each languages and then split with the character ',' to get each
            // keywords.
            String keywords = properties.getProperty("KEYWORDS");
            //First test if the abstract property was set in the property file
            if(keywords == null || keywords.isEmpty()){
                throw new Exception(I18N.tr("The property 'KEYWORDS' isn't defined"));
            }
            //Split the keywords property string by languages and check if there is enough languages
            String[] split = keywords.split(";");
            if(split.length != GLOBAL_PROPERTIES.SUPPORTED_LANGUAGES.length){
                throw new Exception(I18N.tr("The property 'KEYWORDS' doesn't contain the good number of string."));
            }
            //Sets the abstract with the constructed KeywordsType and sets that there is the same number of keywords in
            // each languages.
            String [][] keywordsByLanguage = new String[split.length][];
            for(int i = 0; i<split.length; i++){
                keywordsByLanguage[i] = split[i].split(",");
            }
            for(int i=0; i<keywordsByLanguage.length-1; i++){
                if(keywordsByLanguage[i].length != keywordsByLanguage[i+1].length){
                    throw new Exception(I18N.tr("The property 'KEYWORDS' doesn't contain the same number of keywords " +
                            "for each languages."));
                }
            }
            KEYWORDS = new KeywordsType[keywordsByLanguage.length];
            //For each keyword (index j) add its languageStringType with the language (index i) to have the keywordType
            // (build this way : [ {key[0],lang[0]}, {key[2],lang[0]}, {key[2],lang[0]} ],
            //                   [ {key[0],lang[1]}, {key[2],lang[1]}, {key[2],lang[1]} ]
            for(int j=0; j<keywordsByLanguage.length; j++){ //Keyword loop
                List<LanguageStringType> keywordList = new ArrayList<>();
                for(int i = 0; i<keywordsByLanguage[0].length; i++){// Language loop
                    LanguageStringType keyword = new LanguageStringType();
                    keyword.setLang(GLOBAL_PROPERTIES.SUPPORTED_LANGUAGES[i]);
                    keyword.setValue(keywordsByLanguage[i][j]);
                    keywordList.add(keyword);
                }
                KEYWORDS[j].getKeyword().addAll(keywordList)
            }

            FEES = properties.getProperty("FEES");


            String accessConstraints = properties.getProperty("ACCESS_CONSTRAINTS");
            if(accessConstraints == null || accessConstraints.isEmpty()){
                throw new Exception(I18N.tr("The property 'ACCESS_CONSTRAINTS' isn't defined"));
            }
            ACCESS_CONSTRAINTS = properties.getProperty("ACCESS_CONSTRAINTS").split(",");
        }
    }
}
