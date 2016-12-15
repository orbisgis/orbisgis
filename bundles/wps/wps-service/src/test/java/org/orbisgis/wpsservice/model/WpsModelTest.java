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

import net.opengis.wps._2_0.Format;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *@author Sylvain PALOMINOS
 */
public class WpsModelTest {
    /** Field containing the DataFieldAttribute annotation. */
    @JDBCTableFieldAttribute(
            jdbcTableReference = "data store title",
            dataTypes = {"GEOMETRY", "NUMBER"},
            excludedTypes = {"MULTILINESTRING", "LONG"},
            multiSelection = true
    )
    public Object dataFieldInput;

    /**
     * Test if the decoding and convert of the DataField annotation into its java object is valid.
     */
    @Test
    public void testDataFieldAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DataField object
            DataField datafield = null;
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field dataFieldField = this.getClass().getDeclaredField("dataFieldInput");
            for(Annotation annotation : dataFieldField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof JDBCTableFieldAttribute){
                    annotationFound = true;
                    JDBCTableFieldAttribute descriptionTypeAnnotation = (JDBCTableFieldAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    datafield = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format,
                            URI.create("datastore:uri"));
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || datafield == null){
                Assert.fail("Unable to get the annotation '@DataFieldAttribute' from the field.");
            }

            ////////////////////////
            // Test the DataField //
            ////////////////////////

            String errorMessage = "Error, the DataField 'dataStoreUri' field should be 'datastore:uri' instead of "+
                    datafield.getDataStoreIdentifier().toString();
            Assert.assertEquals(errorMessage, URI.create("datastore:uri"), datafield.getDataStoreIdentifier());

            errorMessage = "Error, the DataField 'isMultiSelection' field should be 'true' instead of "+
                    datafield.isMultiSelection();
            Assert.assertTrue(errorMessage, datafield.isMultiSelection());

            errorMessage = "Error, the DataField 'isSourceModified' field should be 'true' instead of "+
                    datafield.isSourceModified();
            Assert.assertTrue(errorMessage, datafield.isMultiSelection());

            errorMessage = "Error, the DataField 'getExcludedTypeList' field should contain two value : " +
                    "'MULTILINESTRING' and 'LONG'.";
            boolean condition = datafield.getExcludedTypeList().size() == 2 &&
                    datafield.getExcludedTypeList().contains(DataType.MULTILINESTRING) &&
                    datafield.getExcludedTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the DataField 'getFieldTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'.";
            condition = datafield.getFieldTypeList().size() == 2 &&
                    datafield.getFieldTypeList().contains(DataType.GEOMETRY) &&
                    datafield.getFieldTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the DataStoreAttribute annotation. */
    @JDBCTableAttribute(
            dataTypes = {"GEOMETRY", "NUMBER"},
            excludedTypes = {"MULTILINESTRING", "LONG"}
    )
    public Object dataStoreInput;

    /**
     * Test if the decoding and convert of the DataStore annotation into its java object is valid.
     */
    @Test
    public void testDataStoreAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the DataField object
            DataStore dataStore = null;
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field dataStoreField = this.getClass().getDeclaredField("dataStoreInput");
            for(Annotation annotation : dataStoreField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof JDBCTableAttribute){
                    annotationFound = true;
                    JDBCTableAttribute descriptionTypeAnnotation = (JDBCTableAttribute) annotation;
                    List<Format> format = new ArrayList<>();
                    format.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
                    format.get(0).setDefault(true);
                    dataStore = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || dataStore == null){
                Assert.fail("Unable to get the annotation '@DataFieldAttribute' from the field.");
            }

            ////////////////////////
            // Test the DataStore //
            ////////////////////////

            String errorMessage = "Error, the dataStore 'excludedTypeList' field should contain two value : " +
                    "'MULTILINESTRING' and 'LONG'.";
            boolean condition = dataStore.getExcludedTypeList().size() == 2 &&
                    dataStore.getExcludedTypeList().contains(DataType.MULTILINESTRING) &&
                    dataStore.getExcludedTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the dataStore 'dataStoreTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'.";
            condition = dataStore.getDataStoreTypeList().size() == 2 &&
                    dataStore.getDataStoreTypeList().contains(DataType.GEOMETRY) &&
                    dataStore.getDataStoreTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the EnumerationAttribute annotation. */
    @EnumerationAttribute(
            multiSelection = true,
            isEditable = true,
            names = {"name, name, name"},
            selectedValues = {"value1, value2"},
            values = {"value1, value2, value3"}
    )
    public String[] enumerationInput = {"value1, value2"};

    /**
     * Test if the decoding and convert of the Enumeration annotation into its java object is valid.
     */
    @Test
    public void testEnumerationAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the Enumeration object
            Enumeration enumeration = null;
            //Inspect all the annotation of the field to get the EnumerationAttribute one
            Field enumerationField = this.getClass().getDeclaredField("enumerationInput");
            for(Annotation annotation : enumerationField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof EnumerationAttribute){
                    annotationFound = true;
                    EnumerationAttribute descriptionTypeAnnotation = (EnumerationAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    enumeration = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || enumeration == null){
                Assert.fail("Unable to get the annotation '@EnumerationAttribute' from the field.");
            }

            ////////////////////////
            // Test the DataField //
            ////////////////////////

            String errorMessage = "Error, the enumeration 'isMultiSelection' field should be 'true' instead of "+
                    enumeration.isMultiSelection();
            Assert.assertTrue(errorMessage, enumeration.isMultiSelection());

            errorMessage = "Error, the enumeration 'isEditable' field should be 'true' instead of "+
                    enumeration.isEditable();
            Assert.assertTrue(errorMessage, enumeration.isEditable());

            errorMessage = "Error, the enumeration 'values' field should contain three value : " +
                    "'value1', 'value2' and 'value3'.";
            Assert.assertArrayEquals(errorMessage, enumeration.getValues(), new String[]{"value1, value2, value3"});

            boolean valid = true;
            errorMessage = "Error, the enumeration 'valuesNames' field should contain three value : " +
                    "'name', 'name' and 'name'.";
            if(enumeration.getValuesNames().length == 3) {
                for (TranslatableString translatableString : enumeration.getValuesNames()) {
                    if(translatableString.getStrings().length != 1 ||
                            !translatableString.getStrings()[0].getValue().equals("name")){
                        valid = false;
                        break;
                    }
                }
            }
            else{
                valid = false;
            }
            Assert.assertTrue(errorMessage, valid);

            errorMessage = "Error, the enumeration 'defaultValues' field should contain two value : " +
                    "'value1' and 'value2'.";
            Assert.assertArrayEquals(errorMessage, enumeration.getDefaultValues(), new String[]{"value1, value2"});


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the FieldValueAttribute annotation. */
    @JDBCTableFieldValueAttribute(
            multiSelection = true,
            jdbcTableFieldReference = "dataFieldTitle"
    )
    public Object fieldValueInput;

    /**
     * Test if the decoding and convert of the FieldValue annotation into its java object is valid.
     */
    @Test
    public void testFieldValueAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the FieldValue object
            FieldValue fieldValue = null;
            //Inspect all the annotation of the field to get the FieldValueAttribute one
            Field fieldValueField = this.getClass().getDeclaredField("fieldValueInput");
            for(Annotation annotation : fieldValueField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof JDBCTableFieldValueAttribute){
                    annotationFound = true;
                    JDBCTableFieldValueAttribute descriptionTypeAnnotation = (JDBCTableFieldValueAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    fieldValue = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format,
                            URI.create("uri:datafield"));
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || fieldValue == null){
                Assert.fail("Unable to get the annotation '@FieldValueAttribute' from the field.");
            }

            /////////////////////////
            // Test the FieldValue //
            /////////////////////////

            String errorMessage = "Error, the fieldValue 'isDataFieldModified' field should be 'true' instead of "+
                    fieldValue.isDataFieldModified();
            Assert.assertTrue(errorMessage, fieldValue.isDataFieldModified());

            errorMessage = "Error, the fieldValue 'isDataStoreModified' field should be 'true' instead of "+
                    fieldValue.isDataStoreModified();
            Assert.assertTrue(errorMessage, fieldValue.isDataStoreModified());

            errorMessage = "Error, the fieldValue 'multiSelection' field should be 'true' instead of "+
                    fieldValue.getMultiSelection();
            Assert.assertTrue(errorMessage, fieldValue.isDataStoreModified());

            errorMessage = "Error, the fieldValue 'getDataFieldIdentifier' field should be " +
                    URI.create("uri:datafield");
            Assert.assertEquals(errorMessage, fieldValue.getDataFieldIdentifier(), URI.create("uri:datafield"));


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the GeometryDataAttribute annotation. */
    @GeometryAttribute(
            dimension = 1,
            geometryTypes = {"FLOAT", "LONG"},
            excludedTypes = {"GEOMETRY", "NUMBER"}
    )
    public Object geometryInput;

    /**
     * Test if the decoding and convert of the Geometry annotation into its java object is valid.
     */
    @Test
    public void testGeometryAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the Geometry object
            GeometryData geometry = null;
            //Inspect all the annotation of the field to get the GeometryAttribute one
            Field geometryField = this.getClass().getDeclaredField("geometryInput");
            for(Annotation annotation : geometryField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof GeometryAttribute){
                    annotationFound = true;
                    GeometryAttribute descriptionTypeAnnotation = (GeometryAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    geometry = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || geometry == null){
                Assert.fail("Unable to get the annotation '@GeometryAttribute' from the field.");
            }

            ///////////////////////
            // Test the Geometry //
            ///////////////////////

            String errorMessage = "Error, the geometry 'dimension' field should be 1 instead of '"+
                    geometry.getDimension()+"'";
            Assert.assertEquals(errorMessage, geometry.getDimension(), 1);

            errorMessage = "Error, the geometry 'geometryTypeList' field should contain two value : " +
                    "'FLOAT' and 'LONG'";
            boolean condition = geometry.getGeometryTypeList().size() == 2 &&
                    geometry.getGeometryTypeList().contains(DataType.FLOAT) &&
                    geometry.getGeometryTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the geometry 'excludedTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'";
            condition = geometry.getExcludedTypeList().size() == 2 &&
                    geometry.getExcludedTypeList().contains(DataType.GEOMETRY) &&
                    geometry.getExcludedTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the RawDataAttribute annotation. */
    @RawDataAttribute(
            isDirectory = false,
            isFile = false,
            multiSelection = true
    )
    public Object rawDataInput;

    /**
     * Test if the decoding and convert of the RawData annotation into its java object is valid.
     */
    @Test
    public void testRawDataAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the RawData object
            RawData rawData = null;
            //Inspect all the annotation of the field to get the RawDataAttribute one
            Field rawDataField = this.getClass().getDeclaredField("rawDataInput");
            for(Annotation annotation : rawDataField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof RawDataAttribute){
                    annotationFound = true;
                    RawDataAttribute descriptionTypeAnnotation = (RawDataAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    rawData = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || rawData == null){
                Assert.fail("Unable to get the annotation '@RawDataAttribute' from the field.");
            }

            //////////////////////
            // Test the RawData //
            //////////////////////

            String errorMessage = "Error, the rawData 'isDirectory' field should be 'false' instead of '"+
                    rawData.isDirectory()+"'";
            Assert.assertFalse(errorMessage, rawData.isDirectory());

            errorMessage = "Error, the rawData 'isFile' field should be 'false' instead of '"+
                    rawData.isFile()+"'";
            Assert.assertFalse(errorMessage, rawData.isFile());

            errorMessage = "Error, the rawData 'multiSelection' field should be 'true' instead of '"+
                    rawData.multiSelection()+"'";
            Assert.assertTrue(errorMessage, rawData.multiSelection());


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }
}
