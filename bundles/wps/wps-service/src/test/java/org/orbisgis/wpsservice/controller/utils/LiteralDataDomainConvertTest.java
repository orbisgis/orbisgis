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

import net.opengis.ows._2.AnyValue;
import net.opengis.ows._2.DomainMetadataType;
import net.opengis.ows._2.ValueType;
import net.opengis.wps._2_0.LiteralDataType.LiteralDataDomain;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.LiteralDataDomainAttribute;
import org.orbisgis.wpsgroovyapi.attributes.PossibleLiteralValuesChoiceAttribute;
import org.orbisgis.wpsgroovyapi.attributes.ValuesAttribute;
import org.orbisgis.wpsservice.model.DataType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class LiteralDataDomainConvertTest {

    /****************************
     * FULL LITERAL DATA DOMAIN *
     ****************************/

    /** Field containing the full LiteralDataDomain annotation. */
    @LiteralDataDomainAttribute(
            possibleLiteralValues = @PossibleLiteralValuesChoiceAttribute(
                    allowedValues = {
                            @ValuesAttribute(value = "value", type = "VALUE"),
                            @ValuesAttribute(maximum = "maximum", minimum = "minimum", type = "RANGE")
                    },
                    reference = "reference",
                    anyValues = true
            ),
            dataType = "STRING",
            uom = "uom://test/uom",
            defaultValue = "value",
            isDefault = true
    )
    public Object fullLiteralDataDomainAttribute;
    /** Name of the field containing the fullLiteralDataDomainAttribute annotation. */
    private static final String FULL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME = "fullLiteralDataDomainAttribute";

    /**
     * Test if the decoding and convert of the full literalDataDomain annotation into its java object is valid.
     */
    @Test
    public void testFullLiteralDataDomainAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the LiteralDataDomain object
            LiteralDataDomain literalDataDomain = null;
            //Inspect all the annotation of the field to get the LiteralDataDomainAttribute one
            Field valuesField = this.getClass().getDeclaredField(FULL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof LiteralDataDomainAttribute) {
                    annotationFound = true;
                    LiteralDataDomainAttribute literalDataDomainAnnotation = (LiteralDataDomainAttribute) annotation;
                    literalDataDomain = ObjectAnnotationConverter.annotationToObject(literalDataDomainAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || literalDataDomain == null) {
                Assert.fail("Unable to get the annotation '@LiteraldataDomainAttribute' from the field '" +
                        FULL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME + "'.");
            }

            /////////////////////////////////////////
            // Build the LiteralDataDomain to test //
            /////////////////////////////////////////

            LiteralDataDomain toTest = new LiteralDataDomain();
            toTest.setDefault(true);
            toTest.setAnyValue(new AnyValue());
            DomainMetadataType domainMetadataType = new DomainMetadataType();
            domainMetadataType.setReference(DataType.STRING.getUri().toString());
            domainMetadataType.setValue(DataType.STRING.name());
            toTest.setDataType(domainMetadataType);
            ValueType defaultValue = new ValueType();
            defaultValue.setValue("value");
            toTest.setDefaultValue(defaultValue);
            DomainMetadataType uom = new DomainMetadataType();
            uom.setValue("/uom");
            uom.setReference("uom://test/uom");
            toTest.setUOM(uom);

            /////////////////////////////////
            // Tests the LiteralDataDomain //
            /////////////////////////////////

            //Test the value
            String messageValue = "The value is not the one expected (AnyValue object expected)";
            boolean conditionValue = literalDataDomain.getAnyValue()!= null;
            Assert.assertTrue(messageValue, conditionValue);

            //Test the default state
            String messageDefaultState = "The default state is not the one expected (" +
                    literalDataDomain.isDefault() + " instead of " + toTest.isDefault();
            boolean conditionDefaultState = literalDataDomain.isDefault() == toTest.isDefault();
            Assert.assertTrue(messageDefaultState, conditionDefaultState);

            //Test the dataType
            String messageDataType = "The data type is not the one expected (" +
                    literalDataDomain.getDataType().getValue()+"/"+ literalDataDomain.getDataType().getReference() +
                    " instead of " + toTest.getDataType().getValue()+"/"+ toTest.getDataType().getReference();
            boolean conditionDataType =
                    literalDataDomain.getDataType().getValue().equals(toTest.getDataType().getValue()) &&
                            literalDataDomain.getDataType().getReference().equals(toTest.getDataType().getReference());
            Assert.assertTrue(messageDataType, conditionDataType);

            //Test the default value
            String messageDefaultValue = "The default value is not the one expected (" +
                    literalDataDomain.getDefaultValue().getValue()+" instead of "+toTest.getDefaultValue().getValue();
            boolean conditionDefaultValue =
                    literalDataDomain.getDefaultValue().getValue().equals(toTest.getDefaultValue().getValue());
            Assert.assertTrue(messageDefaultValue, conditionDefaultValue);

            //Test the uom
            String messageUom = "The uom is not the one expected (" +
                    literalDataDomain.getUOM().getValue()+" , "+ literalDataDomain.getUOM().getReference() +
                    " instead of " + toTest.getUOM().getValue()+" , "+ toTest.getUOM().getReference();
            boolean conditionUom =
                    literalDataDomain.getUOM().getValue().equals(toTest.getUOM().getValue()) &&
                            literalDataDomain.getUOM().getReference().equals(toTest.getUOM().getReference());
            Assert.assertTrue(messageUom, conditionUom);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /*******************************
     * MINIMAL LITERAL DATA DOMAIN *
     *******************************/

    /** Field containing the minimal LiteralDataDomain annotation. */
    @LiteralDataDomainAttribute(
            possibleLiteralValues = @PossibleLiteralValuesChoiceAttribute(),
            dataType = "STRING",
            defaultValue = "value"
    )
    public Object minimalLiteralDataDomainAttribute;
    /** Name of the field containing the minimalLiteralDataDomainAttribute annotation. */
    private static final String MINIMAL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME = "minimalLiteralDataDomainAttribute";

    /**
     * Test if the decoding and convert of the minimal literalDataDomain annotation into its java object is valid.
     */
    @Test
    public void testMinimalLiteralDataDomainAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the LiteralDataDomain object
            LiteralDataDomain literalDataDomain = null;
            //Inspect all the annotation of the field to get the LiteralDataDomainAttribute one
            Field valuesField = this.getClass().getDeclaredField(MINIMAL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof LiteralDataDomainAttribute) {
                    annotationFound = true;
                    LiteralDataDomainAttribute literalDataDomainAnnotation = (LiteralDataDomainAttribute) annotation;
                    literalDataDomain = ObjectAnnotationConverter.annotationToObject(literalDataDomainAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || literalDataDomain == null) {
                Assert.fail("Unable to get the annotation '@LiteraldataDomainAttribute' from the field '" +
                        MINIMAL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME + "'.");
            }

            /////////////////////////////////////////
            // Build the LiteralDataDomain to test //
            /////////////////////////////////////////

            LiteralDataDomain toTest = new LiteralDataDomain();
            toTest.setDefault(false);
            toTest.setAnyValue(new AnyValue());
            DomainMetadataType domainMetadataType = new DomainMetadataType();
            domainMetadataType.setReference(DataType.STRING.getUri().toString());
            domainMetadataType.setValue(DataType.STRING.name());
            toTest.setDataType(domainMetadataType);
            ValueType defaultValue = new ValueType();
            defaultValue.setValue("value");
            toTest.setDefaultValue(defaultValue);

            /////////////////////////////////
            // Tests the LiteralDataDomain //
            /////////////////////////////////

            //Test the value
            String messageValue = "The value is not the one expected (AnyValue object expected)";
            boolean conditionValue = literalDataDomain.getAnyValue()!= null;
            Assert.assertTrue(messageValue, conditionValue);

            //Test the default state
            String messageDefaultState = "The default state is not the one expected (" +
                    literalDataDomain.isDefault() + " instead of " + toTest.isDefault();
            boolean conditionDefaultState = literalDataDomain.isDefault() == toTest.isDefault();
            Assert.assertTrue(messageDefaultState, conditionDefaultState);

            //Test the dataType
            String messageDataType = "The data type is not the one expected (" +
                    literalDataDomain.getDataType().getValue()+"/"+ literalDataDomain.getDataType().getReference() +
                    " instead of " + toTest.getDataType().getValue()+"/"+ toTest.getDataType().getReference();
            boolean conditionDataType =
                    literalDataDomain.getDataType().getValue().equals(toTest.getDataType().getValue()) &&
                            literalDataDomain.getDataType().getReference().equals(toTest.getDataType().getReference());
            Assert.assertTrue(messageDataType, conditionDataType);

            //Test the default value
            String messageDefaultValue = "The default value is not the one expected (" +
                    literalDataDomain.getDefaultValue().getValue()+" instead of "+toTest.getDefaultValue().getValue();
            boolean conditionDefaultValue =
                    literalDataDomain.getDefaultValue().getValue().equals(toTest.getDefaultValue().getValue());
            Assert.assertTrue(messageDefaultValue, conditionDefaultValue);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + MINIMAL_LITERAL_DATA_DOMAIN_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
