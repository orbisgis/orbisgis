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
import net.opengis.wps._2_0.LiteralDataType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.*;
import org.orbisgis.wpsservice.model.DataType;
import org.orbisgis.wpsservice.model.MalformedScriptException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class LiteralDataTypeConvertTest {
    /*********************
     * FULL LITERAL DATA *
     *********************/

    /** Field containing the full LiteralDataAttribute annotation. */
    @LiteralDataAttribute(
            defaultDomain = "1;1;100",
            validDomains = {"5,10,15","20;;30"}
    )
    public int fullLiteralDataAttribute;
    /** Name of the field containing the fullLiteralDataAttribute annotation. */
    private static final String FULL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME = "fullLiteralDataAttribute";

    /**
     * Test if the decoding and convert of the full literalData annotation into its java object is valid.
     */
    @Test
    public void testFullLiteralDataAttributeConvert() throws MalformedScriptException {
        try {
            boolean annotationFound = false;
            //Retrieve the LiteralData object
            LiteralDataType literalDataType = null;
            //Inspect all the annotation of the field to get the LiteralDataDomainAttribute one
            Field valuesField = this.getClass().getDeclaredField(FULL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof LiteralDataAttribute) {
                    annotationFound = true;
                    LiteralDataAttribute literalDataAnnotation = (LiteralDataAttribute) annotation;
                    literalDataType = ObjectAnnotationConverter.annotationToObject(literalDataAnnotation, DataType.INTEGER, null);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || literalDataType == null) {
                Assert.fail("Unable to get the annotation '@LiteraldataDomainAttribute' from the field '" +
                        FULL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME + "'.");
            }

            /////////////////////////////////
            // Tests the LiteralDataDomain //
            /////////////////////////////////

            //Test there is only one default format
            String messageFormat = "Only one format should be set as the default one.";
            boolean isDefaultFormat = false;
            boolean onlyOneDefaultFormat = true;
            for(Format format : literalDataType.getFormat()){
                if(format.isDefault() && isDefaultFormat){
                    onlyOneDefaultFormat = false;
                }
                if(format.isDefault()){
                    isDefaultFormat = true;
                }
            }
            boolean conditionFormat = isDefaultFormat && onlyOneDefaultFormat;
            Assert.assertTrue(messageFormat, conditionFormat);

            //Test there is only one default literalDataDomain
            String messageLdd = "Only one literalDataDomain should be set as the default one.";
            boolean isDefaultLdd = false;
            boolean onlyOneDefaultLdd = true;
            for(LiteralDataType.LiteralDataDomain ldd : literalDataType.getLiteralDataDomain()){
                if(ldd.isDefault() && isDefaultLdd){
                    onlyOneDefaultLdd = false;
                }
                if(ldd.isDefault()){
                    isDefaultLdd = true;
                }
            }
            boolean conditionLdd = isDefaultLdd && onlyOneDefaultLdd;
            Assert.assertTrue(messageLdd, conditionLdd);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /************************
     * MINIMAL LITERAL DATA *
     ************************/

    /** Field containing the minimal LiteralDataAttribute annotation. */
    @LiteralDataAttribute()
    public int minimalLiteralDataAttribute;
    /** Name of the field containing the minimalLiteralDataAttribute annotation. */
    private static final String MINIMAL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME = "minimalLiteralDataAttribute";

    /**
     * Test if the decoding and convert of the minimal literalData annotation into its java object is valid.
     */
    @Test
    public void testMinimalLiteralDataAttributeConvert() throws MalformedScriptException {
        try {
            boolean annotationFound = false;
            //Retrieve the LiteralData object
            LiteralDataType literalDataType = null;
            //Inspect all the annotation of the field to get the LiteralDataDomainAttribute one
            Field valuesField = this.getClass().getDeclaredField(MINIMAL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof LiteralDataAttribute) {
                    annotationFound = true;
                    LiteralDataAttribute literalDataAnnotation = (LiteralDataAttribute) annotation;
                    literalDataType = ObjectAnnotationConverter.annotationToObject(literalDataAnnotation, DataType.INTEGER, null);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || literalDataType == null) {
                Assert.fail("Unable to get the annotation '@LiteralDataDomainAttribute' from the field '" +
                        MINIMAL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME + "'.");
            }

            /////////////////////////////////
            // Tests the LiteralDataDomain //
            /////////////////////////////////

            //Test there is only one default format
            String messageFormat = "Only one format should be set as the default one.";
            boolean isDefaultFormat = false;
            boolean onlyOneDefaultFormat = true;
            for(Format format : literalDataType.getFormat()){
                if(format.isDefault() && isDefaultFormat){
                    onlyOneDefaultFormat = false;
                }
                if(format.isDefault()){
                    isDefaultFormat = true;
                }
            }
            boolean conditionFormat = isDefaultFormat && onlyOneDefaultFormat;
            Assert.assertTrue(messageFormat, conditionFormat);

            //Test there is only one default literalDataDomain
            String messageLdd = "Only one literalDataDomain should be set as the default one.";
            boolean isDefaultLdd = false;
            boolean onlyOneDefaultLdd = true;
            for(LiteralDataType.LiteralDataDomain ldd : literalDataType.getLiteralDataDomain()){
                if(ldd.isDefault() && isDefaultLdd){
                    onlyOneDefaultLdd = false;
                }
                if(ldd.isDefault()){
                    isDefaultLdd = true;
                }
            }
            boolean conditionLdd = isDefaultLdd && onlyOneDefaultLdd;
            Assert.assertTrue(messageLdd, conditionLdd);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + MINIMAL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
