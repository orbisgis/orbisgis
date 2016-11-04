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
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.PossibleLiteralValuesChoiceAttribute;
import org.orbisgis.wpsgroovyapi.attributes.ValuesAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class PossibleLiteralValueConvertTest {


    /*************
     * FULL PLVC *
     *************/

    /** Field containing the full annotation. */
    @PossibleLiteralValuesChoiceAttribute()
    public Object fullPLVCAttribute;
    /** Name of the field containing the full PossibleLiteralValueChoice annotation. */
    private static final String FULL_PLVC_ATTRIBUTE_FIELD_NAME = "fullPLVCAttribute";

    /**
     * Test if the decoding and convert of the full PossibleLiteralValueChoice annotation into its java object is valid.
     */
    @Test
    public void testFullPLVCAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the plvc object
            Object object = null;
            //Inspect all the annotation of the field to get the PLVCAttribute one
            Field plvcField = this.getClass().getDeclaredField(FULL_PLVC_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : plvcField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof PossibleLiteralValuesChoiceAttribute) {
                    annotationFound = true;
                    PossibleLiteralValuesChoiceAttribute plvcAnnotation = (PossibleLiteralValuesChoiceAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(plvcAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        FULL_PLVC_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ///////////////////////////
            // Tests the PLVC Values //
            ///////////////////////////

            String messageClass = "The parsed value should be a '"+AnyValue.class.getCanonicalName()+"' instead of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof AnyValue;
            Assert.assertTrue(messageClass, conditionClass);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_PLVC_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /*****************
     * MINIMAL PLVC *
     *****************/

    /** Field containing the full annotation. */
    @PossibleLiteralValuesChoiceAttribute()
    public Object minimalPLVCAttribute;
    /** Name of the field containing the minimalRangeAttribute annotation. */
    private static final String MINIMAL_PLVC_ATTRIBUTE_FIELD_NAME = "minimalPLVCAttribute";

    /**
     * Test if the decoding and convert of the minimal possibleLiteralValuesChoice annotation into its java object is
     * valid.
     */
    @Test
    public void testMinimalPLVCAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the plvc object
            Object object = null;
            //Inspect all the annotation of the field to get the PLVC one
            Field plvcField = this.getClass().getDeclaredField(MINIMAL_PLVC_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : plvcField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof PossibleLiteralValuesChoiceAttribute) {
                    annotationFound = true;
                    PossibleLiteralValuesChoiceAttribute plvcAnnotation = (PossibleLiteralValuesChoiceAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(plvcAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        MINIMAL_PLVC_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ///////////////////////////
            // Tests the PLVC Values //
            ///////////////////////////

            String messageClass = "The parsed value should be a '"+AnyValue.class.getCanonicalName()+"' instead of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof AnyValue;
            Assert.assertTrue(messageClass, conditionClass);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + MINIMAL_PLVC_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /******************
     * REFERENCE PLVC *
     ******************/

    /** Field containing the plvc annotation. */
    @PossibleLiteralValuesChoiceAttribute(
            reference = "file://file/path"
    )
    public Object referencePLVCAttribute;
    /** Name of the field containing the referencePLVCAttribute annotation. */
    private static final String REFERENCE_PLVC_ATTRIBUTE_FIELD_NAME = "referencePLVCAttribute";

    /**
     * Test if the decoding and convert of the reference possibleLiteralValuesChoice annotation into its java object is
     * valid.
     */
    @Test
    public void testReferencePLVCAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the plvc object
            Object object = null;
            //Inspect all the annotation of the field to get the PLVC one
            Field plvcField = this.getClass().getDeclaredField(REFERENCE_PLVC_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : plvcField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof PossibleLiteralValuesChoiceAttribute) {
                    annotationFound = true;
                    PossibleLiteralValuesChoiceAttribute plvcAnnotation = (PossibleLiteralValuesChoiceAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(plvcAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        REFERENCE_PLVC_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ///////////////////////////
            // Tests the PLVC Values //
            ///////////////////////////

            String reference = "file://file/path";
            ValuesReference toTest = new ValuesReference();
            toTest.setReference(reference);
            toTest.setValue("/path");

            String messageClass = "The parsed value should be a '"+ValuesReference.class.getCanonicalName()+"' instead of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof ValuesReference;
            Assert.assertTrue(messageClass, conditionClass);

            ValuesReference valuesReference = (ValuesReference)object;

            //Test the value
            String messageValue = "The value is not the one expected (" +
                    valuesReference.getValue() + " instead of " + toTest.getValue();
            boolean conditionValue = valuesReference.getValue().equals(toTest.getValue());
            Assert.assertTrue(messageValue, conditionValue);

            //Test the reference
            String messageReference = "The reference is not the one expected (" +
                    valuesReference.getReference() + " instead of " + toTest.getReference();
            boolean conditionReference = valuesReference.getReference().equals(toTest.getReference());
            Assert.assertTrue(messageReference, conditionReference);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + REFERENCE_PLVC_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /***********************
     * ALLOWED VALUES PLVC *
     ***********************/

    /** Field containing the plvc annotation. */
    @PossibleLiteralValuesChoiceAttribute(
            allowedValues = {@ValuesAttribute(type = "RANGE"),@ValuesAttribute()}
    )
    public Object allowedValuesPLVCAttribute;
    /** Name of the field containing the allowedValuesPLVCAttribute annotation. */
    private static final String ALLOWED_VALUES_PLVC_ATTRIBUTE_FIELD_NAME = "allowedValuesPLVCAttribute";

    /**
     * Test if the decoding and convert of the allowed values possibleLiteralValuesChoice annotation into its java
     * object is valid.
     */
    @Test
    public void testAllowedValuesPLVCAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the plvc object
            Object object = null;
            //Inspect all the annotation of the field to get the PLVC one
            Field plvcField = this.getClass().getDeclaredField(ALLOWED_VALUES_PLVC_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : plvcField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof PossibleLiteralValuesChoiceAttribute) {
                    annotationFound = true;
                    PossibleLiteralValuesChoiceAttribute plvcAnnotation = (PossibleLiteralValuesChoiceAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(plvcAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        ALLOWED_VALUES_PLVC_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ///////////////////////////
            // Tests the PLVC Values //
            ///////////////////////////

            String messageClass = "The parsed value should be a '"+AllowedValues.class.getCanonicalName()+"' instead of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof AllowedValues;
            Assert.assertTrue(messageClass, conditionClass);

            AllowedValues allowedValues = (AllowedValues)object;

            boolean conditionClasses = false;
            for(Object o : allowedValues.getValueOrRange()){
                if(!(o instanceof RangeType) && !(o instanceof ValueType)){
                    conditionClasses = true;
                }
            }


            String messageClasses = "The allowed values should be an instance of '"+
                    RangeType.class.getCanonicalName()+"' or '"+ValueType.class.getCanonicalName()+"'.";
            Assert.assertTrue(messageClasses, conditionClasses);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + ALLOWED_VALUES_PLVC_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
