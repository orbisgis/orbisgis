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
package org.orbisgis.orbiswpsservice.model;

import net.opengis.wps._2_0.Format;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.orbiswpsgroovyapi.attributes.*;
import org.orbisgis.orbiswpsservice.controller.utils.FormatFactory;
import org.orbisgis.orbiswpsservice.controller.utils.ObjectAnnotationConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *@author Sylvain PALOMINOS
 *@author Erwan Bocher
 */
public class WpsModelTest {
    /** Field containing the JDBCColumnAttribute annotation. */
    @JDBCColumnAttribute(
            jdbcTableReference = "jdbcTable title",
            dataTypes = {"GEOMETRY", "NUMBER"},
            excludedTypes = {"MULTILINESTRING", "LONG"},
            multiSelection = true
    )
    public Object jdbcColumnInput;

    /**
     * Test if the decoding and convert of the JDBCColumn annotation into its java object is valid.
     * @throws org.orbisgis.wpsservice.model.MalformedScriptException
     */
    @Test
    public void testJDBCColumnAttributeConvert() throws MalformedScriptException {
        try {
            boolean annotationFound = false;
            //Retrieve the JDBCColumn object
            JDBCColumn jdbcColumn = null;
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field jdbcColumnField = this.getClass().getDeclaredField("jdbcColumnInput");
            for(Annotation annotation : jdbcColumnField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof JDBCColumnAttribute){
                    annotationFound = true;
                    JDBCColumnAttribute descriptionTypeAnnotation = (JDBCColumnAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    jdbcColumn = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format,
                            URI.create("jdbctable:uri"));
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || jdbcColumn == null){
                Assert.fail("Unable to get the annotation '@JDBCColumnAttribute' from the field.");
            }

            /////////////////////////////
            // Test the JDBCColumn //
            /////////////////////////////

            String errorMessage = "Error, the JDBCColumn 'jdbcTableUri' field should be 'jdbctable:uri' instead of "+
                    jdbcColumn.getJDBCTableIdentifier().toString();
            Assert.assertEquals(errorMessage, URI.create("jdbctable:uri"), jdbcColumn.getJDBCTableIdentifier());

            errorMessage = "Error, the JDBCColumn 'isMultiSelection' field should be 'true' instead of "+
                    jdbcColumn.isMultiSelection();
            Assert.assertTrue(errorMessage, jdbcColumn.isMultiSelection());

            errorMessage = "Error, the JDBCColumn 'isSourceModified' field should be 'true' instead of "+
                    jdbcColumn.isSourceModified();
            Assert.assertTrue(errorMessage, jdbcColumn.isMultiSelection());

            errorMessage = "Error, the JDBCColumn 'getExcludedTypeList' field should contain two value : " +
                    "'MULTILINESTRING' and 'LONG'.";
            boolean condition = jdbcColumn.getExcludedTypeList().size() == 2 &&
                    jdbcColumn.getExcludedTypeList().contains(DataType.MULTILINESTRING) &&
                    jdbcColumn.getExcludedTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the JDBCColumn 'getDataTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'.";
            condition = jdbcColumn.getDataTypeList().size() == 2 &&
                    jdbcColumn.getDataTypeList().contains(DataType.GEOMETRY) &&
                    jdbcColumn.getDataTypeList().contains(DataType.NUMBER);
            Assert.assertTrue(errorMessage, condition);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the JDBCTableAttribute annotation. */
    @JDBCTableAttribute(
            dataTypes = {"GEOMETRY", "NUMBER"},
            excludedTypes = {"MULTILINESTRING", "LONG"}
    )
    public Object jdbcTableInput;

    /**
     * Test if the decoding and convert of the JDBCTable annotation into its java object is valid.
     */
    @Test
    public void testJDBCTableAttributeConvert() throws MalformedScriptException {
        try {
            boolean annotationFound = false;
            //Retrieve the JDBCTable object
            JDBCTable jdbcTable = null;
            //Inspect all the annotation of the field to get the DescriptionTypeAttribute one
            Field jdbcTableField = this.getClass().getDeclaredField("jdbcTableInput");
            for(Annotation annotation : jdbcTableField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof JDBCTableAttribute){
                    annotationFound = true;
                    JDBCTableAttribute descriptionTypeAnnotation = (JDBCTableAttribute) annotation;
                    List<Format> format = new ArrayList<>();
                    format.add(FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION));
                    format.get(0).setDefault(true);
                    jdbcTable = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || jdbcTable == null){
                Assert.fail("Unable to get the annotation '@JDBCTableAttribute' from the field.");
            }

            ////////////////////////
            // Test the JDBCTable //
            ////////////////////////

            String errorMessage = "Error, the JDBCTable 'excludedTypeList' field should contain two value : " +
                    "'MULTILINESTRING' and 'LONG'.";
            boolean condition = jdbcTable.getExcludedTypeList().size() == 2 &&
                    jdbcTable.getExcludedTypeList().contains(DataType.MULTILINESTRING) &&
                    jdbcTable.getExcludedTypeList().contains(DataType.LONG);
            Assert.assertTrue(errorMessage, condition);

            errorMessage = "Error, the JDBCTable 'dataTypeList' field should contain two value : " +
                    "'GEOMETRY' and 'NUMBER'.";
            condition = jdbcTable.getDataTypeList().size() == 2 &&
                    jdbcTable.getDataTypeList().contains(DataType.GEOMETRY) &&
                    jdbcTable.getDataTypeList().contains(DataType.NUMBER);
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
            values = {"value1, value2, value3"}
    )
    public String[] enumerationInput = {"value1, value2"};

    /**
     * Test if the decoding and convert of the Enumeration annotation into its java object is valid.
     */
    @Test
    public void testEnumerationAttributeConvert() throws MalformedScriptException {
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
            // Test the JDBCTableField //
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

            /*errorMessage = "Error, the enumeration 'defaultValues' field should contain two value : " +
                    "'value1' and 'value2'.";
            Assert.assertArrayEquals(errorMessage, enumeration.getDefaultValues(), new String[]{"value1, value2"});*/


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }

    }



    /** Field containing the JDBCValueAttribute annotation. */
    @JDBCValueAttribute(
            multiSelection = true,
            jdbcColumnReference = "jdbcValueTitle"
    )
    public Object jdbcValueInput;

    /**
     * Test if the decoding and convert of the JDBCValueAttribute annotation into its java object is valid.
     * @throws org.orbisgis.wpsservice.model.MalformedScriptException
     */
    @Test
    public void testFieldValueAttributeConvert() throws MalformedScriptException {
        try {
            boolean annotationFound = false;
            //Retrieve the JDBCValue object
            JDBCValue jdbcValue = null;
            //Inspect all the annotation of the field to get the JDBCValueAttribute one
            Field jdbcValueField = this.getClass().getDeclaredField("jdbcValueInput");
            for(Annotation annotation : jdbcValueField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof JDBCValueAttribute){
                    annotationFound = true;
                    JDBCValueAttribute descriptionTypeAnnotation = (JDBCValueAttribute) annotation;
                    Format format = FormatFactory.getFormatFromExtension(FormatFactory.TEXT_EXTENSION);
                    format.setDefault(true);
                    jdbcValue = ObjectAnnotationConverter.annotationToObject(descriptionTypeAnnotation, format,
                            URI.create("uri:jdbcvalue"));
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound || jdbcValue == null){
                Assert.fail("Unable to get the annotation '@JDBCValueAttribute' from the field.");
            }

            //////////////////////////////////
            // Test the JDBCValue //
            //////////////////////////////////

            String errorMessage = "Error, the JDBCValue 'isJDBCColumnModified' field should be 'true' instead of "+
                    jdbcValue.isJDBCColumnModified();
            Assert.assertTrue(errorMessage, jdbcValue.isJDBCColumnModified());

            errorMessage = "Error, the JDBCValue 'isJDBCTableModified' field should be 'true' instead of "+
                    jdbcValue.isJDBCTableModified();
            Assert.assertTrue(errorMessage, jdbcValue.isJDBCTableModified());

            errorMessage = "Error, the JDBCValue 'multiSelection' field should be 'true' instead of "+
                    jdbcValue.isMultiSelection();
            Assert.assertTrue(errorMessage, jdbcValue.isJDBCTableModified());

            errorMessage = "Error, the JDBCValue 'getJDBCColumnIdentifier' field should be " +
                    URI.create("uri:jdbcvalue");
            Assert.assertEquals(errorMessage, jdbcValue.getJDBCColumnIdentifier(), URI.create("uri:jdbcvalue"));


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
     * @throws org.orbisgis.wpsservice.model.MalformedScriptException
     */
    @Test
    public void testGeometryAttributeConvert() throws MalformedScriptException {
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
     * @throws org.orbisgis.wpsservice.model.MalformedScriptException
     */
    @Test
    public void testRawDataAttributeConvert() throws MalformedScriptException {
        try {
            boolean annotationFound = false;
            //Retrieve the RawData object
            RawData rawData = null;
            //Inspect all the annotation of the field to get the RawDataAttribute one
            Field rawjdbcTableField = this.getClass().getDeclaredField("rawDataInput");
            for(Annotation annotation : rawjdbcTableField.getDeclaredAnnotations()){
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
