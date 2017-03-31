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

package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.Format;
import net.opengis.wps._2_0.LiteralDataType.LiteralDataDomain;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.model.*;
import org.orbisgis.wpsservice.model.BoundingBoxData;
import org.orbisgis.wpsservice.model.Enumeration;
import org.orbisgis.wpsservice.model.TranslatableString;

import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * Class able to convert groovy annotation into java object and object into annotation.
 *
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
 **/

public class ObjectAnnotationConverter {

    /**
     * Builds a {@link BoundingBoxData} Object from a BoundingBoxAttribute annotation.
     * @param boundingBoxAttribute Groovy annotation to decode to build the Java object.
     * @param formatList
     * @return A {@link BoundingBoxData} object with the data from the {@link BoundingBoxAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static BoundingBoxData annotationToObject(BoundingBoxAttribute boundingBoxAttribute, List<Format> formatList)
            throws MalformedScriptException {
        String[] supportedCrs = boundingBoxAttribute.supportedCRS();
        return new BoundingBoxData(formatList, supportedCrs, boundingBoxAttribute.dimension());
    }

    /**
     * Create the {@link SupportedCRS} object from a string representation of a CRS like EPSG:2041.
     *
     * @param crs {@link String} representation of the CRS.
     * @param isDefault True if the {@link SupportedCRS} is the default one.
     * @return The supported CRS.
     */
    private static SupportedCRS getCRS(String crs, boolean isDefault){
        if(crs == null || crs.isEmpty()){
            return null;
        }
        SupportedCRS supportedCRS = new SupportedCRS();
        supportedCRS.setDefault(isDefault);

        String[] splitCrs = crs.split(":");
        String authority = splitCrs[0].toUpperCase();
        switch(authority){
            case "EPSG":
                supportedCRS.setValue("http://www.opengis.net/def/crs/"+authority+"/8.9.2/"+splitCrs[1]);
                break;
            case "IAU":
                supportedCRS.setValue("http://www.opengis.net/def/crs/"+authority+"/0/"+splitCrs[1]);
                break;
            case "AUTO":
                supportedCRS.setValue("http://www.opengis.net/def/crs/"+authority+"/1.3/"+splitCrs[1]);
                break;
            case "OGC":
                supportedCRS.setValue("http://www.opengis.net/def/crs/"+authority+"/0/"+splitCrs[1]);
                break;
            case "IGNF":
                supportedCRS.setValue("http://registre.ign.fr/ign/IGNF/crs/IGNF/"+splitCrs[1]);
                break;
            default:
                return null;
        }
        return supportedCRS;
    }

    /**
     * Builds a {@link DescriptionType} Object from a {@link DescriptionTypeAttribute} annotation.
     * @param descriptionTypeAttribute Groovy annotation to decode to build the Java object.
     * @param descriptionType A {@link DescriptionType} object with the data from the {@link DescriptionTypeAttribute}
     *                        annotation.
     * @param processIdentifier
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static void annotationToObject(DescriptionTypeAttribute descriptionTypeAttribute,
                                          DescriptionType descriptionType, String processIdentifier)
            throws MalformedScriptException {
        //First check if there is at least one title.
        if(descriptionTypeAttribute.title().length == 0){
            throw new MalformedScriptException(DescriptionTypeAttribute.class, "title", "The title should be defined.");
        }
        //Then adds the titles
        List<LanguageStringType> titleList = new ArrayList<>();
        String[] titles = descriptionTypeAttribute.title();
        //Case of only one title without language
        if(titles.length == 1){
            LanguageStringType string = new LanguageStringType();
            string.setValue(titles[0].trim());
            titleList.add(string);
        }
        //Case of several titles with their language
        else if(titles.length%2 == 0){
            for (int i = 0; i < titles.length; i += 2) {
                LanguageStringType string = new LanguageStringType();
                string.setValue(titles[i].trim());
                string.setLang(titles[i + 1]);
                titleList.add(string);
            }
        }
        else{
            throw new MalformedScriptException(DescriptionTypeAttribute.class, "title", "The title should be composed " +
                    "of pairs of String : the title and its language.");
        }
        descriptionType.getTitle().clear();
        descriptionType.getTitle().addAll(titleList);

        //Descriptions
        List<LanguageStringType> descriptionList = new ArrayList<>();
        String[] descriptions = descriptionTypeAttribute.description();
        //Case of only one description without language
        if(descriptions.length == 1){
            LanguageStringType resume = new LanguageStringType();
            resume.setValue(descriptions[0].trim());
            descriptionList.add(resume);
        }
        //Case of several description with their language
        else if(descriptions.length%2 == 0){
            for (int i = 0; i < descriptions.length; i += 2) {
                LanguageStringType resume = new LanguageStringType();
                resume.setValue(descriptions[i].trim());
                resume.setLang(descriptions[i + 1]);
                descriptionList.add(resume);
            }
        }
        //Case of more than one description but not well formed (pair of a description with its language)
        else if(descriptions.length>0){
            throw new MalformedScriptException(DescriptionTypeAttribute.class, "description", "The description should " +
                    "be composed of pairs of String : the description and its language.");
        }
        descriptionType.getAbstract().clear();
        descriptionType.getAbstract().addAll(descriptionList);

        //Identifier
        if(!descriptionTypeAttribute.identifier().isEmpty()){
            CodeType codeType = new CodeType();
            if(processIdentifier.isEmpty()){
                codeType.setValue(descriptionTypeAttribute.identifier().trim());
            }
            else {
                codeType.setValue(processIdentifier.trim() + ":" + descriptionTypeAttribute.identifier().trim());
            }
            descriptionType.setIdentifier(codeType);
        }

        //Keywords
        String[] keywords = descriptionTypeAttribute.keywords();
        //Case of only one keyword language (i.e. keywords="key1,key2,key3")
        if(keywords.length == 1) {
            LinkedList<KeywordsType> keywordsTypeList = new LinkedList<>();
            //Splits the keyword string with the ',' character
            String[] split = keywords[0].split(",");
            //For each keyword :
            for(String str : split){
                //Create the LanguageStringType containing the keyword as value and nothing as language
                LanguageStringType keywordString = new LanguageStringType();
                keywordString.setValue(str.trim());
                //Creates the keywordList which will contains one keyword, but in several languages.
                // (In this case there is only one language)
                List<LanguageStringType> keywordList = new ArrayList<>();
                keywordList.add(keywordString);
                //Creates the KeywordsType object containing the list of translation of a keyword
                KeywordsType keywordsType = new KeywordsType();
                keywordsType.getKeyword().addAll(keywordList);
                //Add it to the keywordsType list
                keywordsTypeList.add(keywordsType);
            }
            descriptionType.getKeywords().clear();
            descriptionType.getKeywords().addAll(keywordsTypeList);
        }
        //Case of several keyword language (i.e. keywords=["key1,key2,key3","en","clef1,clef2,clef3","fr"])
        else if(keywords.length != 0 && keywords.length%2 == 0) {
            LinkedList<KeywordsType> keywordTypeList = new LinkedList<>();
            //Each time take a pair of string : the keywords and the language.
            //Then split the keywords string with the ',' character, put the keyword in a LanguageStringType object
            // with its language and then put the first keyword LanguageStringType in the first KeywordsType object,
            // the second keyword in the second KeywordsType object ...
            //Repeat the operation for each language.
            //
            //Example :
            // keywords=["key1,key2,key3","en","clef1,clef2,clef3","fr"] becomes
            //
            //List<LanguageStringType> {
            //          KeywordsType{LanguageStringType{"key1","en"},LanguageStringType{"clef1","fr"}},
            //          KeywordsType{LanguageStringType{"key2","en"},LanguageStringType{"clef2","fr"}},
            //          KeywordsType{LanguageStringType{"key3","en"},LanguageStringType{"clef3","fr"}}
            // }
            for(int i=0; i<keywords.length; i+=2){
                String language = keywords[i+1];
                String[] split = keywords[i].split(",");
                //If the KeywordsType object haven't already been created, create them
                if(keywordTypeList.isEmpty()){
                    for (String ignored : split) {
                        KeywordsType keywordsType = new KeywordsType();
                        keywordsType.getKeyword().addAll(new ArrayList<LanguageStringType>());
                        keywordTypeList.add(keywordsType);
                    }
                }
                //Adds all the keywords to the good KeywordsType
                for(int j=0; j<split.length; j++){
                    String str = split[j];
                    LanguageStringType keywordString = new LanguageStringType();
                    keywordString.setValue(str.trim());
                    keywordString.setLang(language);

                    List<LanguageStringType> keywordList = keywordTypeList.get(j).getKeyword();
                    keywordList.add(keywordString);
                }
            }
            descriptionType.getKeywords().clear();
            descriptionType.getKeywords().addAll(keywordTypeList);
        }
        //Case of more than one keyword but not well formed (pair of a keyword with its language)
        else if(keywords.length>0){
            throw new MalformedScriptException(DescriptionTypeAttribute.class, "keywords", "The keywords should " +
                    "be composed of pairs of String : the coma separated keywords and their language.");
        }

        String[] metadata = descriptionTypeAttribute.metadata();
        //Check if the metadata is composed of pairs of string
        if(metadata.length != 0) {
            if (metadata.length % 2 == 0){
                List<MetadataType> metadataList = new ArrayList<>();
                for (int i = 0; i < metadata.length; i += 2) {
                    MetadataType metadataType = new MetadataType();
                    metadataType.setRole(metadata[i]);
                    metadataType.setTitle(metadata[i + 1]);
                    metadataList.add(metadataType);
                }
                descriptionType.getMetadata().clear();
                descriptionType.getMetadata().addAll(metadataList);
            }
            else{
                throw new MalformedScriptException(DescriptionTypeAttribute.class, "metadata", "The metadata should " +
                        "be composed of pairs of String : the property and its value.");
            }
        }
    }

    /**
     * Builds an {@link Enumeration} Object from an {@link EnumerationAttribute} annotation.
     * @param enumAttribute Groovy annotation to decode to build the Java object.
     * @param format {@link Format} of the {@link Enumeration} ComplexType.
     * @return An {@link Enumeration} object with the data from the {@link EnumerationAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static Enumeration annotationToObject(EnumerationAttribute enumAttribute, Format format)
            throws MalformedScriptException {
        //Creates the format list
        format.setDefault(true);
        List<Format> formatList = new ArrayList<>();
        formatList.add(format);
        //Creates the enumeration Object and set it
        Enumeration enumeration = new Enumeration(formatList, enumAttribute.values());
        enumeration.setEditable(enumAttribute.isEditable());
        enumeration.setMultiSelection(enumAttribute.multiSelection());

        //Decodes the Groovy annotation 'names' attribute and store each name in a LanguageStringType with its language
        String[] names = enumAttribute.names();
        //In the case where is only one language
        if(names.length == 1) {
            //Splits the names
            String[] split = names[0].split(",");
            TranslatableString[] translatableStrings = new TranslatableString[split.length];
            //Populate the TranslatableString array with all the names
            for(int i=0; i<split.length; i++){
                //Creates the LanguageStringType Object containing the name
                LanguageStringType type = new LanguageStringType();
                type.setValue(split[i].trim());
                //Store the LanguageStringType Object in an array
                LanguageStringType[] types = new LanguageStringType[1];
                types[0] = type;
                //Store the array in a TranslatableString object
                TranslatableString string = new TranslatableString();
                string.setStrings(types);
                //Store the TranslatableString
                translatableStrings[i] = string;
            }
            enumeration.setValuesNames(translatableStrings);
        }
        //Case of several names language (i.e. names=["name1,name2,name3","en","nom1,nom2,nom3","fr"])
        else if(names.length != 0 && names.length%2==0) {
            TranslatableString[] translatableStringArray = new TranslatableString[names[0].split(",").length];
            //Each time take a pair of string : the names and the language.
            //Then split the names string with the ',' character, put the name in a LanguageStringType object
            // with its language and then put the first name LanguageStringType in the first TranslatableString object,
            // the second name in the second TranslatableString object ...
            //Repeat the operation for each language.
            //
            //Example :
            // names=["name1,name2,name3","en","nom1,nom2,nom3","fr"] becomes
            //
            //TranslatableString {
            //          LanguageStringType[]{LanguageStringType{"name1","en"},LanguageStringType{"nom1","fr"}},
            //          LanguageStringType[]{LanguageStringType{"name2","en"},LanguageStringType{"nom2","fr"}},
            //          LanguageStringType[]{LanguageStringType{"name3","en"},LanguageStringType{"nom3","fr"}}
            // }
            //For each languages, uses the a pair of String : the names and the language.
            for(int i=0; i< names.length; i+=2) {
                String[] splitNames = names[i].split(",");
                String language = names[i + 1];
                //For each name
                for (int j = 0; j < splitNames.length; j++) {
                    //Gets the TranslatableString object that should contains the name
                    TranslatableString translatableString = translatableStringArray[j];
                    //If the TranslatableString at the index of the name is null, then creates it
                    if(translatableString == null){
                        translatableString = new TranslatableString();
                    }
                    //Gets the LanguageStringType array that should contains the name
                    LanguageStringType[] languageStringArray = translatableString.getStrings();
                    //If the TranslatableString LanguageStringType array is null, then creates it
                    if(languageStringArray == null || languageStringArray.length == 0) {
                        languageStringArray = new LanguageStringType[names.length/2];
                    }
                    //Creates the name LanguageStringType Object
                    LanguageStringType languageString = new LanguageStringType();
                    //Sets the name
                    languageString.setValue(splitNames[j].trim());
                    //Sets the language
                    languageString.setLang(language);
                    //Store it in the LanguageStringType array
                    languageStringArray[i/2] = languageString;
                    //Store the LanguageStringType array in the TranslatableString
                    translatableString.setStrings(languageStringArray);
                    translatableStringArray[j] = translatableString;
                }
            }
            enumeration.setValuesNames(translatableStringArray);
        }
        return enumeration;
    }

    /**
     * Builds an {@link GeometryData} Object from an {@link GeometryAttribute} annotation.
     * @param geometryAttribute Groovy annotation to decode to build the Java object.
     * @param format {@link Format} of the {@link Enumeration} ComplexType.
     * @return An {@link GeometryData} object with the data from the {@link GeometryAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static GeometryData annotationToObject(GeometryAttribute geometryAttribute, Format format)
            throws MalformedScriptException {
        format.setDefault(true);
        List<DataType> geometryTypeList = new ArrayList<>();
        //For each field type value from the groovy annotation, test if it is contain in the FieldType enumeration.
        for(String type : Arrays.asList(geometryAttribute.geometryTypes())){
            geometryTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        List<DataType> excludedTypeList = new ArrayList<>();
        //For each excluded type value from the groovy annotation, test if it is contain in the FieldType enumeration.
        for(String type : Arrays.asList(geometryAttribute.excludedTypes())){
            excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        List<Format> formatList = new ArrayList<>();
        formatList.add(format);
        GeometryData geometryData = new GeometryData(formatList, geometryTypeList);
        geometryData.setDimension(geometryAttribute.dimension());
        geometryData.setExcludedTypeList(excludedTypeList);
        return geometryData;
    }

    /**
     * Builds an {@link InputDescriptionType} Object from an {@link InputAttribute} annotation.
     * @param inputAttribute Groovy annotation to decode to build the Java object.
     * @param input The {@link InputDescriptionType} object with the data from the {@link InputAttribute} annotation.
     */
    public static void annotationToObject(InputAttribute inputAttribute, InputDescriptionType input){
        input.setMaxOccurs(""+inputAttribute.maxOccurs());
        input.setMinOccurs(BigInteger.valueOf(inputAttribute.minOccurs()));
    }

    /**
     * Builds a {@link JDBCTable} Object from an {@link JDBCTableAttribute} annotation.
     * @param jdbcTableAttribute Groovy annotation to decode to build the Java object.
     * @param formatList The list of the {@link Format} for the {@link JDBCTable}.
     * @return The {@link JDBCTable} object with the data from the {@link JDBCTableAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static JDBCTable annotationToObject(JDBCTableAttribute jdbcTableAttribute, List<Format> formatList)
            throws MalformedScriptException {
        JDBCTable jdbcTable = new JDBCTable(formatList);
        List<DataType> dataTypeList = new ArrayList<>();
        for(String type : Arrays.asList(jdbcTableAttribute.dataTypes())){
            dataTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        jdbcTable.setDataTypeList(dataTypeList);
        List<DataType> excludedTypeList = new ArrayList<>();
        for(String type : Arrays.asList(jdbcTableAttribute.excludedTypes())){
            excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        jdbcTable.setExcludedTypeList(excludedTypeList);
        return jdbcTable;
    }

    /**
     * Builds a {@link JDBCColumn} Object from an {@link JDBCColumnAttribute} annotation.
     * @param jDBCColumnAttribute Groovy annotation to decode to build the Java object.
     * @param format The {@link Format} for the {@link JDBCTable}.
     * @param jdbcTableUri The URI of the parent {@link JDBCTable} object.
     * @return The {@link JDBCColumn} object with the data from the {@link JDBCColumnAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static JDBCColumn annotationToObject(JDBCColumnAttribute jDBCColumnAttribute, Format format,
                                                    URI jdbcTableUri) throws MalformedScriptException {
        format.setDefault(true);
        List<DataType> dataTypeList = new ArrayList<>();
        for(String type : Arrays.asList(jDBCColumnAttribute.dataTypes())){
            dataTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        List<DataType> excludedTypeList = new ArrayList<>();
        for(String type : Arrays.asList(jDBCColumnAttribute.excludedTypes())){
            excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        List<String> excludedNameList = new ArrayList<>();
        for(String name : Arrays.asList(jDBCColumnAttribute.excludedNames())){
            excludedNameList.add(name);
        }
        List<Format> formatList = new ArrayList<>();
        formatList.add(format);
        JDBCColumn jdbcColumn = new JDBCColumn(formatList, dataTypeList, jdbcTableUri);
        jdbcColumn.setExcludedTypeList(excludedTypeList);
        jdbcColumn.setExcludedNameList(excludedNameList);
        jdbcColumn.setMultiSelection(jDBCColumnAttribute.multiSelection());
        return jdbcColumn;
    }

    /**
     * Builds a {@link JDBCValue} Object from an {@link JDBCValueAttribute} annotation.
     * @param jdbcValueAttribute Groovy annotation to decode to build the Java object.
     * @param format The {@link Format} for the {@link JDBCTable}.
     * @param jdbcColumndUri The URI of the parent {@link JDBCColumn} object.
     * @return The {@link JDBCValue} object with the data from the {@link JDBCValueAttribute}
     *          annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static JDBCValue annotationToObject(JDBCValueAttribute jdbcValueAttribute,
                                                         Format format, URI jdbcColumndUri)
            throws MalformedScriptException {
        format.setDefault(true);
        List<Format> formatList = new ArrayList<>();
        formatList.add(format);
        return new JDBCValue(formatList, jdbcColumndUri, jdbcValueAttribute.multiSelection());
    }

    /**
     * Builds a {@link LiteralDataType} Object from an {@link LiteralDataAttribute} annotation.
     * @param literalDataAttribute Groovy annotation to decode to build the Java object.
     * @param dataType The {@link DataType} for the {@link LiteralDataType}.
     * @param defaultValueStr
     * @return The {@link LiteralDataType} object with the data from the {@link LiteralDataAttribute}
     *          annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static LiteralDataType annotationToObject(LiteralDataAttribute literalDataAttribute, DataType dataType, Object defaultValueStr)
            throws MalformedScriptException {
        LiteralDataType literalDataType = new LiteralDataType();

        //Sets the format of the literalData
        List<Format> formatList = new ArrayList<>();
        formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
        formatList.get(0).setDefault(true);
        literalDataType.getFormat().clear();
        literalDataType.getFormat().addAll(formatList);

        //Sets the data domain of the literalData
        List<LiteralDataDomain> lddList = new ArrayList<>();
        //Build the literalDataDomain list

        //Sets the default domain
        LiteralDataDomain dataDomain = createLiteralDataDomain(dataType, literalDataAttribute.defaultDomain(), true);
        if(dataDomain == null){
            throw new MalformedScriptException(LiteralDataAttribute.class, "validDomains", "The valid " +
                    "domains should be a coma separated list of ranges with this pattern : min;;max / " +
                    "min;spacing;max or a simple value");
        }
        if(defaultValueStr!= null) {
            ValueType defaultValueType = new ValueType();
            defaultValueType.setValue(defaultValueStr.toString());
            dataDomain.setDefaultValue(defaultValueType);
        }
        lddList.add(dataDomain);

        //Sets the others domains if defined
        if(literalDataAttribute.validDomains().length != 0){
            for(String validDomain : literalDataAttribute.validDomains()){
                dataDomain = createLiteralDataDomain(dataType, validDomain, false);
                if(dataDomain == null){
                    throw new MalformedScriptException(LiteralDataAttribute.class, "validDomains", "The valid " +
                            "domains should be a coma separated list of ranges with this pattern : min;;max / " +
                            "min;spacing;max or a simple value");
                }
                if(defaultValueStr!= null) {
                    ValueType defaultValueType = new ValueType();
                    defaultValueType.setValue(defaultValueStr.toString());
                    dataDomain.setDefaultValue(defaultValueType);
                }
                lddList.add(dataDomain);
            }
        }

        literalDataType.getLiteralDataDomain().clear();
        literalDataType.getLiteralDataDomain().addAll(lddList);

        return literalDataType;
    }

    /**
     * Builds a {@link RawData} Object from an {@link RawDataAttribute} annotation.
     * @param rawDataAttribute Groovy annotation to decode to build the Java object.
     * @param format The {@link Format} for the {@link RawData}.
     * @return The {@link RawData} object with the data from the {@link RawDataAttribute}
     *          annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static RawData annotationToObject(RawDataAttribute rawDataAttribute, Format format)
            throws MalformedScriptException {
        //Instantiate the RawData
        format.setDefault(true);
        List<Format> formatList = new ArrayList<>();
        formatList.add(format);
        RawData rawData = new RawData(formatList);
        rawData.setFile(rawDataAttribute.isFile());
        rawData.setDirectory(rawDataAttribute.isDirectory());
        rawData.setMultiSelection(rawDataAttribute.multiSelection());
        rawData.setFileTypes(rawDataAttribute.fileTypes());
        rawData.setExcludedTypes(rawDataAttribute.excludedTypes());
        return rawData;
    }

    /**
     * Sets a {@link ProcessOffering} Object from an {@link ProcessAttribute} annotation.
     * @param processAttribute Groovy annotation to decode to build the Java object.
     * @param processOffering The {@link ProcessOffering} Object to set.
     */
    public static void annotationToObject(ProcessAttribute processAttribute, ProcessOffering processOffering){
        processOffering.getProcess().setLang(Locale.forLanguageTag(processAttribute.language()).toString());
        processOffering.setProcessVersion(processAttribute.version());
        String[] properties = processAttribute.properties();
        List<MetadataType> metadataList = processOffering.getProcess().getMetadata();
        for(int i=0; i<properties.length; i+=2){
            MetadataType metadata = new MetadataType();
            metadata.setRole(properties[i]);
            metadata.setTitle(properties[i+1]);
            metadataList.add(metadata);
        }
    }

    /**
     * Creates a {@link LiteralDataDomain} object from its string representation.
     * @param dataType DataType of the domain.
     * @param literalDataDomainStr String representation of the domain.
     * @param isDefault True if the domain is the default one, false otherwise.
     * @return The LiteralDataDomain.
     */
    private static LiteralDataDomain createLiteralDataDomain(DataType dataType, String literalDataDomainStr,
                                                             boolean isDefault){
        LiteralDataDomain LiteralDataDomain = new LiteralDataDomain();
        LiteralDataDomain.setDefault(isDefault);
        DomainMetadataType domainMetadataType = new DomainMetadataType();
        domainMetadataType.setValue(dataType.name());
        domainMetadataType.setReference(dataType.getUri().toString());
        LiteralDataDomain.setDataType(domainMetadataType);
        //If no values was specified, allow any value
        if(literalDataDomainStr.isEmpty()){
            LiteralDataDomain.setAnyValue(new AnyValue());
        }
        else{
            AllowedValues allowedValues = new AllowedValues();
            List<Object> valueOrRangeList = new ArrayList<>();
            String[] splitDomains = literalDataDomainStr.split(",");
            for(String domain : splitDomains){
                Object allowedValue = createAllowedValue(domain);
                if(allowedValue == null){
                    return null;
                }
                valueOrRangeList.add(allowedValue);
            }
            allowedValues.getValueOrRange().addAll(valueOrRangeList);
            LiteralDataDomain.setAllowedValues(allowedValues);
        }
        return LiteralDataDomain;
    }

    /**
     * Create the allowed value Object ({@link RangeType} or {@link ValueType}) from its string representation.
     * @param allowedValue String representation of the allowedValue.
     * @return {@link RangeType} or {@link ValueType} Object.
     */
    private static Object createAllowedValue(String allowedValue){
        String allowedValueStr = allowedValue.trim();
        if(allowedValueStr.contains(";")){
            String[] domainValues = allowedValueStr.split(";");
            //Test if the domain is well formed (min;;max or min;spacing;max). If not, throw an exception
            if(domainValues[0].isEmpty() || domainValues[2].isEmpty()){
                return null;
            }
            RangeType rangeType = new RangeType();
            ValueType minValue = new ValueType();
            minValue.setValue(domainValues[0]);
            rangeType.setMinimumValue(minValue);
            if(!domainValues[1].isEmpty()) {
                ValueType spacingValue = new ValueType();
                spacingValue.setValue(domainValues[1]);
                rangeType.setSpacing(spacingValue);
            }
            ValueType maxValue = new ValueType();
            maxValue.setValue(domainValues[0]);
            rangeType.setMaximumValue(maxValue);

            return rangeType;
        }
        else{
            ValueType value = new ValueType();
            value.setValue(allowedValueStr);

            return value;
        }
    }
}
