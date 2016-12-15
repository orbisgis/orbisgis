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

import net.opengis.wps._2_0.Format;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;

/**
 * This test class perform three test on the Format annotation parsing.
 * The test are done on converting a @FormatAttribute annotation into the model java object.
 * Each test are done on different annotation complexity : a full annotation and a simple one.
 *
 * @author Sylvain PALOMINOS
 */
public class FormatConvertTest {


    /***************
     * FULL FORMAT *
     ***************/

    @FormatAttribute(
            mimeType = "mimetype",
            encoding = "simple",
            schema = "schema",
            maximumMegaBytes = 1,
            isDefaultFormat = true
    )
    public Object fullFormatAttribute;
    private final static String FULL_FORMAT_ATTRIBUTE_FIELD_NAME = "fullFormatAttribute";

    @Test
    public void testFullFormatAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the Format object
            Format format = new Format();
            //Inspect all the annotation of the field to get the FormatAttribute one
            Field formatField = this.getClass().getDeclaredField(FULL_FORMAT_ATTRIBUTE_FIELD_NAME);
            for(Annotation annotation : formatField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof FormatAttribute){
                    annotationFound = true;
                    FormatAttribute formatAnnotation = (FormatAttribute) annotation;
                    format = ObjectAnnotationConverter.annotationToObject(formatAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound){
                Assert.fail("Unable to get the annotation '@FormatAttribute' from the field '" +
                        FULL_FORMAT_ATTRIBUTE_FIELD_NAME +"'.");
            }

            //////////////////////////////
            // Build the Format to test //
            //////////////////////////////

            Format toTest = new Format();

            toTest.setDefault(true);
            toTest.setEncoding("simple");
            toTest.setMaximumMegabytes(BigInteger.valueOf(1));
            toTest.setMimeType("mimetype");
            toTest.setSchema("schema");


            //////////////////////
            // Tests the Format //
            //////////////////////

            //test the default
            String messageDefault = "The default state is not the one expected ("+
                    format.isDefault()+ " instead of "+toTest.isDefault();
            boolean conditionDefault = format.isDefault() == toTest.isDefault();
            Assert.assertTrue(messageDefault, conditionDefault);

            //test the encoding
            String messageEncoding = "The encoding is not the one expected ("+
                    format.getEncoding()+ " instead of "+toTest.getEncoding();
            boolean conditionEncoding = format.getEncoding().equals(toTest.getEncoding());
            Assert.assertTrue(messageEncoding, conditionEncoding);

            //test the maximum megabyte
            String messageMaximumMegabytes = "The maximum megabyte value is not the one expected ("+
                    format.getMaximumMegabytes()+ " instead of "+toTest.getMaximumMegabytes();
            boolean conditionMaximumMegabytes = format.getMaximumMegabytes().equals(toTest.getMaximumMegabytes());
            Assert.assertTrue(messageMaximumMegabytes, conditionMaximumMegabytes);

            //test the encoding
            String messageMimeType = "The encoding is not the one expected ("+
                    format.getMimeType()+ " instead of "+toTest.getMimeType();
            boolean conditionMimeType = format.getMimeType().equals(toTest.getMimeType());
            Assert.assertTrue(messageMimeType, conditionMimeType);

            //test the schema
            String messageSchema = "The schema value is not the one expected ("+
                    format.getSchema()+ " instead of "+toTest.getSchema();
            boolean conditionSchema = format.getSchema().equals(toTest.getSchema());
            Assert.assertTrue(messageSchema, conditionSchema);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '"+ FULL_FORMAT_ATTRIBUTE_FIELD_NAME +"' from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }
    }


    /*****************
     * SIMPLE FORMAT *
     *****************/

    @FormatAttribute(
            mimeType = "mimetype",
            schema = "schema"
    )
    public Object simpleFormatAttribute;
    private final static String SIMPLE_FORMAT_ATTRIBUTE_FIELD_NAME = "simpleFormatAttribute";

    @Test
    public void testSimpleFormatAttributeConvert(){
        try {
            boolean annotationFound = false;
            //Retrieve the Format object
            Format format = new Format();
            //Inspect all the annotation of the field to get the FormatAttribute one
            Field formatField = this.getClass().getDeclaredField(SIMPLE_FORMAT_ATTRIBUTE_FIELD_NAME);
            for(Annotation annotation : formatField.getDeclaredAnnotations()){
                //Once the annotation is get, decode it.
                if(annotation instanceof FormatAttribute){
                    annotationFound = true;
                    FormatAttribute formatAnnotation = (FormatAttribute) annotation;
                    format = ObjectAnnotationConverter.annotationToObject(formatAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if(!annotationFound){
                Assert.fail("Unable to get the annotation '@FormatAttribute' from the field '" +
                        FULL_FORMAT_ATTRIBUTE_FIELD_NAME +"'.");
            }

            //////////////////////////////
            // Build the Format to test //
            //////////////////////////////

            Format toTest = new Format();

            toTest.setMimeType("mimetype");
            toTest.setSchema("schema");

            toTest.setDefault(false);
            toTest.setEncoding("simple");
            toTest.setMaximumMegabytes(null);


            //////////////////////
            // Tests the Format //
            //////////////////////

            //test the default
            String messageDefault = "The default state is not the one expected ("+
                    format.isDefault()+ " instead of "+toTest.isDefault();
            boolean conditionDefault = format.isDefault() == toTest.isDefault();
            Assert.assertTrue(messageDefault, conditionDefault);

            //test the encoding
            String messageEncoding = "The encoding is not the one expected ("+
                    format.getEncoding()+ " instead of "+toTest.getEncoding();
            boolean conditionEncoding = format.getEncoding().equals(toTest.getEncoding());
            Assert.assertTrue(messageEncoding, conditionEncoding);

            //test the maximum megabyte
            String messageMaximumMegabytes = "The maximum megabyte value is not the one expected ("+
                    format.getMaximumMegabytes()+ " instead of "+toTest.getMaximumMegabytes();
            boolean conditionMaximumMegabytes = format.getMaximumMegabytes() == toTest.getMaximumMegabytes();
            Assert.assertTrue(messageMaximumMegabytes, conditionMaximumMegabytes);

            //test the encoding
            String messageMimeType = "The encoding is not the one expected ("+
                    format.getMimeType()+ " instead of "+toTest.getMimeType();
            boolean conditionMimeType = format.getMimeType().equals(toTest.getMimeType());
            Assert.assertTrue(messageMimeType, conditionMimeType);

            //test the schema
            String messageSchema = "The schema value is not the one expected ("+
                    format.getSchema()+ " instead of "+toTest.getSchema();
            boolean conditionSchema = format.getSchema().equals(toTest.getSchema());
            Assert.assertTrue(messageSchema, conditionSchema);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '"+ SIMPLE_FORMAT_ATTRIBUTE_FIELD_NAME +"' from the class '" +
                    this.getClass().getCanonicalName()+"'.");
        }
    }
}
