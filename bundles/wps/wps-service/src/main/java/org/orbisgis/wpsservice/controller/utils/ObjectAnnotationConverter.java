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

package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.*;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.Format;
import net.opengis.wps._2_0.LiteralDataType.LiteralDataDomain;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.model.*;
import org.orbisgis.wpsservice.model.Enumeration;
import org.orbisgis.wpsservice.model.TranslatableString;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.URI;
import java.util.*;

/**
 * Class able to convert groovy annotation into java object and object into annotation.
 *
 * @author Sylvain PALOMINOS
 **/

public class ObjectAnnotationConverter {

    /**
     * Builds a {@link BoundingBoxData} Object from a BoundingBoxAttribute annotation.
     * @param boundingBoxAttribute Groovy annotation to decode to build the Java object.
     * @return A {@link BoundingBoxData} object with the data from the {@link BoundingBoxAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static BoundingBoxData annotationToObject(BoundingBoxAttribute boundingBoxAttribute)
            throws MalformedScriptException {
        BoundingBoxData boundingBoxData = new BoundingBoxData();
        List<SupportedCRS> supportedCRSList = boundingBoxData.getSupportedCRS();
        SupportedCRS defaultCRS = getCRS(boundingBoxAttribute.defaultCRS(), true);
        if(defaultCRS == null){
            throw new MalformedScriptException(BoundingBoxAttribute.class, "CRS", "The default CRS should be defined.");
        }
        supportedCRSList.add(defaultCRS);
        if(boundingBoxAttribute.supportedCRS().length != 0){
            for(String crsStr : boundingBoxAttribute.supportedCRS()) {
                SupportedCRS crs = getCRS(crsStr, false);
                if(crs != null){
                    supportedCRSList.add(crs);
                }
            }
        }
        return boundingBoxData;
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
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    public static void annotationToObject(DescriptionTypeAttribute descriptionTypeAttribute,
                                          DescriptionType descriptionType) throws MalformedScriptException {
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
            codeType.setValue(descriptionTypeAttribute.identifier().trim());
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
        if(metadata.length != 0 && metadata.length%2==0){
            List<MetadataType> metadataList = new ArrayList<>();
            for(int i=0; i<metadata.length; i+=2){
                MetadataType metadataType = new MetadataType();
                metadataType.setRole(metadata[i]);
                metadataType.setTitle(metadata[i+1]);
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
     * Builds an {@link JDBCTable} Object from an {@link JDBCTableAttribute} annotation.
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
     * Builds an {@link JDBCTable} Object from an {@link JDBCTableAttribute} annotation.
     * @param jdbcTableAttribute Groovy annotation to decode to build the Java object.
     * @param format The {@link Format} for the {@link JDBCTable}.
     * @return The {@link JDBCTable} object with the data from the {@link JDBCTableAttribute} annotation.
     * @throws MalformedScriptException Exception thrown in case of a malformed Groovy annotation.
     */
    /**
     *
     * @param jdbcTableFieldAttribute
     * @param format
     * @param jdbcTableUri
     * @return
     */
    public static JDBCTableField annotationToObject(JDBCTableFieldAttribute jdbcTableFieldAttribute, Format format,
                                                    URI jdbcTableUri) throws MalformedScriptException {
        format.setDefault(true);
        List<DataType> dataTypeList = new ArrayList<>();
        for(String type : Arrays.asList(jdbcTableFieldAttribute.dataTypes())){
            dataTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        List<DataType> excludedTypeList = new ArrayList<>();
        for(String type : Arrays.asList(jdbcTableFieldAttribute.excludedTypes())){
            excludedTypeList.add(DataType.getDataTypeFromFieldType(type));
        }
        List<Format> formatList = new ArrayList<>();
        formatList.add(format);
        JDBCTableField jdbcTableField = new JDBCTableField(formatList, dataTypeList, jdbcTableUri);
        jdbcTableField.setExcludedTypeList(excludedTypeList);
        jdbcTableField.setMultiSelection(jdbcTableFieldAttribute.multiSelection());
        return jdbcTableField;
    }

/*
    public static Format annotationToObject(FormatAttribute formatAttribute){
        Format format = new Format();
        format.setMimeType(formatAttribute.mimeType());
        format.setSchema(URI.create(formatAttribute.schema()).toString());
        format.setDefault(formatAttribute.isDefaultFormat());
        if(formatAttribute.maximumMegaBytes() == 0) {
            format.setMaximumMegabytes(null);
        }
        else{
            format.setMaximumMegabytes(BigInteger.valueOf(formatAttribute.maximumMegaBytes()));
        }
        format.setEncoding(formatAttribute.encoding());
        return format;
    }

    public static MetadataType annotationToObject(MetadataAttribute descriptionTypeAttribute){
        URI href = URI.create(descriptionTypeAttribute.href());
        URI role = URI.create(descriptionTypeAttribute.role());
        String title = descriptionTypeAttribute.title();

        MetadataType metadata = new MetadataType();
        metadata.setHref(href.toString());
        metadata.setRole(role.toString());
        metadata.setTitle(title);

        return metadata;
    }

    public static Object annotationToObject(ValuesAttribute valueAttribute){
        if(valueAttribute.type().toUpperCase().equals(ValuesType.VALUE.name())){
            if(valueAttribute.value().equals(ValuesAttribute.defaultValue)){
                return null;
            }
            ValueType value = new ValueType();
            value.setValue(valueAttribute.value().trim());
            return value;
        }
        else if(valueAttribute.type().toUpperCase().equals(ValuesType.RANGE.name())){
            RangeType range = new RangeType();
            if(!valueAttribute.spacing().equals(ValuesAttribute.defaultSpacing)) {
                ValueType spacing = new ValueType();
                spacing.setValue(valueAttribute.spacing().trim());
                range.setSpacing(spacing);
            }
            if(!valueAttribute.maximum().equals(ValuesAttribute.defaultMaximum)){
                ValueType max = new ValueType();
                max.setValue(valueAttribute.maximum().trim());
                range.setMaximumValue(max);
            }
            if(!valueAttribute.minimum().equals(ValuesAttribute.defaultMinimum)){
                ValueType min = new ValueType();
                min.setValue(valueAttribute.minimum().trim());
                range.setMinimumValue(min);
            }
            return range;
        }
        return null;
    }*/
/*
    public static LiteralDataDomain annotationToObject(LiteralDataDomainAttribute literalDataDomainAttribute){
        LiteralDataDomain literalDataDomain = new LiteralDataDomain();
        literalDataDomain.setDefault(literalDataDomainAttribute.isDefault());
        if(!literalDataDomainAttribute.uom().isEmpty()){
            DomainMetadataType uom = new DomainMetadataType();
            URI uomUri = URI.create(literalDataDomainAttribute.uom());
            uom.setValue(uomUri.getPath());
            uom.setReference(uomUri.toString());
            literalDataDomain.setUOM(uom);
        }
        DataType dataType = DataType.valueOf(literalDataDomainAttribute.dataType());
        DomainMetadataType domainDataType = new DomainMetadataType();
        domainDataType.setReference(dataType.getUri().toString());
        domainDataType.setValue(dataType.name().trim());
        literalDataDomain.setDataType(domainDataType);

        Object value = ObjectAnnotationConverter.annotationToObject(literalDataDomainAttribute.possibleLiteralValues());
        if(value instanceof AllowedValues){
            literalDataDomain.setAllowedValues((AllowedValues)value);
        }
        else if(value instanceof AnyValue){
            literalDataDomain.setAnyValue((AnyValue) value);
        }
        else if(value instanceof ValuesReference){
            literalDataDomain.setValuesReference((ValuesReference) value);
        }
        ValueType defaultValue = new ValueType();
        defaultValue.setValue(literalDataDomainAttribute.defaultValue().trim());
        literalDataDomain.setDefaultValue(defaultValue);

        return literalDataDomain;
    }*/

    public static LiteralDataType annotationToObject(LiteralDataAttribute literalDataAttribute,
                                                     DomainMetadataType domainMetadataType) {
        LiteralDataType literalDataType = new LiteralDataType();

        List<Format> formatList = new ArrayList<>();
        formatList.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
        formatList.get(0).setDefault(true);
        literalDataType.getFormat().clear();
        literalDataType.getFormat().addAll(formatList);

        List<LiteralDataType.LiteralDataDomain> lddList = new ArrayList<>();
        if(literalDataAttribute.defaultDomain().isEmpty()){
            LiteralDataDomain literalDataDomain = new LiteralDataDomain();
            literalDataDomain.setDefault(true);
            literalDataDomain.setAnyValue(new AnyValue());
            lddList.add(literalDataDomain);
        }
        /*else {
            LiteralDataDomain ldd = getLiteralDataDomain(literalDataAttribute.defaultDomain(), domainMetadataType);
            ldd.setDefault(true);
            lddList.add(ldd);
            if (literalDataAttribute.validDomains().length != 0) {
                for (String literalDataDomain : literalDataAttribute.validDomains()) {
                    lddList.add(getLiteralDataDomain(literalDataDomain, domainMetadataType));
                }
            }
        }*/
        literalDataType.getLiteralDataDomain().clear();
        literalDataType.getLiteralDataDomain().addAll(lddList);

        return literalDataType;
    }
/*
    private static LiteralDataDomain getLiteralDataDomain(String literalDataDomain, DomainMetadataType domainMetadataType){
        LiteralDataDomain ldd = new LiteralDataDomain();
        String[] split = literalDataDomain.split(",");
        List<Object> listAllowedValues = ldd.getAllowedValues().getValueOrRange();
        for (String domain : split) {
            if (domain.contains(";")) {
                String[] rangeAttributes = domain.split(";");
                RangeType range = new RangeType();
                ValueType minimum = new ValueType();
                minimum.setValue(rangeAttributes[0]);
                range.setMinimumValue(minimum);
                if (!rangeAttributes[1].isEmpty()) {
                    ValueType spacing = new ValueType();
                    spacing.setValue(rangeAttributes[1]);
                    range.setSpacing(spacing);
                }
                ValueType maximum = new ValueType();
                maximum.setValue(rangeAttributes[2]);
                range.setMaximumValue(maximum);
                listAllowedValues.add(range);
            } else {
                ValueType value = new ValueType();
                value.setValue(domain);
                listAllowedValues.add(value);
            }
        }
        ldd.setDataType(domainMetadataType);
        return ldd;
    }*/

    public static RawData annotationToObject(RawDataAttribute rawDataAttribute, Format format) {
        try {
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
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }
/*
    public static Object annotationToObject(
            PossibleLiteralValuesChoiceAttribute possibleLiteralValuesChoiceAttribute){
        if(possibleLiteralValuesChoiceAttribute.anyValues() ||
                (possibleLiteralValuesChoiceAttribute.allowedValues().length != 0 &&
                        !possibleLiteralValuesChoiceAttribute.reference().isEmpty())){
            return new AnyValue();
        }
        else if(possibleLiteralValuesChoiceAttribute.allowedValues().length != 0){
            AllowedValues allowedValues = new AllowedValues();
            List<Object> valueList = new ArrayList<>();
            for(ValuesAttribute va : possibleLiteralValuesChoiceAttribute.allowedValues()){
                valueList.add(ObjectAnnotationConverter.annotationToObject(va));
            }
            allowedValues.getValueOrRange().clear();
            allowedValues.getValueOrRange().addAll(valueList);
            return allowedValues;
        }
        else if (!possibleLiteralValuesChoiceAttribute.reference().isEmpty()){
            ValuesReference valuesReference = new ValuesReference();
            URI uri = URI.create(possibleLiteralValuesChoiceAttribute.reference());
            valuesReference.setValue(uri.getPath());
            valuesReference.setReference(uri.toString());
            return valuesReference;
        }
        return new AnyValue();
    }*/

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

    public static JDBCTableFieldValue annotationToObject(JDBCTableFieldValueAttribute jdbcTableFieldvalueAttribute, Format format, URI jdbcTableFieldUri) {
        try {
            format.setDefault(true);
            List<Format> formatList = new ArrayList<>();
            formatList.add(format);
            return new JDBCTableFieldValue(formatList, jdbcTableFieldUri, jdbcTableFieldvalueAttribute.multiSelection());
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(ObjectAnnotationConverter.class).error(e.getMessage());
            return null;
        }
    }
}
