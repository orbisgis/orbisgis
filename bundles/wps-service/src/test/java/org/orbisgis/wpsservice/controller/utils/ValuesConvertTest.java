package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows.v_2_0.RangeType;
import net.opengis.ows.v_2_0.ValueType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class ValuesConvertTest {


    /**************
     * FULL RANGE *
     **************/

    /** Field containing the full annotation. */
    @ValuesAttribute(
            type = ValuesType.RANGE,
            maximum = "maximum",
            minimum = "minimum",
            spacing = "spacing",
            value = "value"
    )
    public Object fullRangeAttribute;
    /** Name of the field containing the fullRangeAttribute annotation. */
    private static final String FULL_RANGE_ATTRIBUTE_FIELD_NAME = "fullRangeAttribute";

    /**
     * Test if the decoding and convert of the full range annotation into its java object is valid.
     */
    @Test
    public void testFullRangeAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Values object
            Object object = null;
            //Inspect all the annotation of the field to get the ValuesAttribute one
            Field valuesField = this.getClass().getDeclaredField(FULL_RANGE_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof ValuesAttribute) {
                    annotationFound = true;
                    ValuesAttribute valuesAnnotation = (ValuesAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(valuesAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        FULL_RANGE_ATTRIBUTE_FIELD_NAME + "'.");
            }
            String messageClass = "The parsed value should be a '"+RangeType.class.getCanonicalName()+"' instance of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof RangeType;
            Assert.assertTrue(messageClass, conditionClass);

            RangeType rangeType = (RangeType) object;

            ////////////////////////////////////
            // Build the Range Values to test //
            ////////////////////////////////////

            RangeType toTest = new RangeType();

            ValueType maximumValue = new ValueType();
            maximumValue.setValue("maximum");
            toTest.setMaximumValue(maximumValue);

            ValueType minimumValue = new ValueType();
            minimumValue.setValue("minimum");
            toTest.setMinimumValue(minimumValue);

            ValueType spacingValue = new ValueType();
            spacingValue.setValue("spacing");
            toTest.setSpacing(spacingValue);

            ////////////////////////////
            // Tests the Range Values //
            ////////////////////////////

            //Test the maximum
            String messageMaximum = "The maximum value is not the one expected (" +
                    rangeType.getMaximumValue().getValue() + " instead of " + toTest.getMaximumValue().getValue();
            boolean conditionMaximum = rangeType.getMaximumValue().getValue().equals(toTest.getMaximumValue().getValue());
            Assert.assertTrue(messageMaximum, conditionMaximum);

            //Test the minimum
            String messageMinimum = "The minimum value is not the one expected (" +
                    rangeType.getMinimumValue().getValue() + " instead of " + toTest.getMinimumValue().getValue();
            boolean conditionMinimum = rangeType.getMinimumValue().getValue().equals(toTest.getMinimumValue().getValue());
            Assert.assertTrue(messageMinimum, conditionMinimum);

            //Test the spacing
            String messageSpacing = "The spacing value is not the one expected (" +
                    rangeType.getSpacing().getValue() + " instead of " + toTest.getSpacing().getValue();
            boolean conditionSpacing = rangeType.getSpacing().getValue().equals(toTest.getSpacing().getValue());
            Assert.assertTrue(messageSpacing, conditionSpacing);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_RANGE_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /*****************
     * MINIMAL RANGE *
     *****************/

    /** Field containing the minimal Range annotation. */
    @ValuesAttribute(
            type = ValuesType.RANGE
    )
    public Object minimalRangeAttribute;
    /** Name of the field containing the minimalRangeAttribute annotation. */
    private static final String MINIMAL_RANGE_ATTRIBUTE_FIELD_NAME = "minimalRangeAttribute";

    /**
     * Test if the decoding and convert of the minimal range annotation into its java object is valid.
     */
    @Test
    public void testMinimalRangeAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Values object
            Object object = null;
            //Inspect all the annotation of the field to get the ValuesAttribute one
            Field valuesField = this.getClass().getDeclaredField(MINIMAL_RANGE_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof ValuesAttribute) {
                    annotationFound = true;
                    ValuesAttribute valuesAnnotation = (ValuesAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(valuesAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        MINIMAL_RANGE_ATTRIBUTE_FIELD_NAME + "'.");
            }
            String messageClass = "The parsed value should be a '"+RangeType.class.getCanonicalName()+"' instance of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof RangeType;
            Assert.assertTrue(messageClass, conditionClass);

            RangeType rangeType = (RangeType) object;

            ////////////////////////////////////
            // Build the Range Values to test //
            ////////////////////////////////////

            RangeType toTest = new RangeType();

            ////////////////////////////
            // Tests the Range Values //
            ////////////////////////////

            //Test the maximum
            String messageMaximum = "The maximum value is not the one expected (" +
                    rangeType.getMaximumValue() + " instead of " + null;
            boolean conditionMaximum = rangeType.getMaximumValue() == null;
            Assert.assertTrue(messageMaximum, conditionMaximum);

            //Test the minimum
            String messageMinimum = "The minimum value is not the one expected (" +
                    rangeType.getMinimumValue() + " instead of " + null;
            boolean conditionMinimum = rangeType.getMinimumValue() == null;
            Assert.assertTrue(messageMinimum, conditionMinimum);

            //Test the spacing
            String messageSpacing = "The spacing value is not the one expected (" +
                    rangeType.getSpacing() + " instead of " + null;
            boolean conditionSpacing = rangeType.getSpacing() == null;
            Assert.assertTrue(messageSpacing, conditionSpacing);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + MINIMAL_RANGE_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /***************
     * FULL VALUES *
     ***************/

    /** Field containing the full Values annotation. */
    @ValuesAttribute(
            type = ValuesType.VALUE,
            maximum = "maximum",
            minimum = "minimum",
            spacing = "spacing",
            value = "value"
    )
    public Object fullValueAttribute;
    /** Name of the field containing the fullValueAttribute annotation. */
    private static final String FULL_VALUE_ATTRIBUTE_FIELD_NAME = "fullValueAttribute";

    /**
     * Test if the decoding and convert of the full value annotation into its java object is valid.
     */
    @Test
    public void testFullRangeValuesAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Values object
            Object object = null;
            //Inspect all the annotation of the field to get the ValuesAttribute one
            Field valuesField = this.getClass().getDeclaredField(FULL_VALUE_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof ValuesAttribute) {
                    annotationFound = true;
                    ValuesAttribute valuesAnnotation = (ValuesAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(valuesAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || object == null) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        FULL_VALUE_ATTRIBUTE_FIELD_NAME + "'.");
            }
            String messageClass = "The parsed value should be a '"+ValueType.class.getCanonicalName()+"' instance of '"+
                    object.getClass().getCanonicalName()+"'.";
            boolean conditionClass = object instanceof ValueType;
            Assert.assertTrue(messageClass, conditionClass);

            ValueType valueType = (ValueType) object;

            ////////////////////////////////////
            // Build the Range Values to test //
            ////////////////////////////////////

            ValueType toTest = new ValueType();
            toTest.setValue("value");

            ////////////////////////////
            // Tests the Range Values //
            ////////////////////////////

            //Test the value
            String messageValue = "The value is not the one expected (" +
                    valueType.getValue() + " instead of " + toTest.getValue();
            boolean conditionValue = valueType.getValue().equals(toTest.getValue());
            Assert.assertTrue(messageValue, conditionValue);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_VALUE_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }


    /*****************
     * MINIMAL VALUE *
     *****************/

    /** Field containing the minimal Value annotation. */
    @ValuesAttribute(
            type = ValuesType.VALUE
    )
    public Object minimalValueAttribute;
    /** Name of the field containing the minimalValueAttribute annotation. */
    private static final String MINIMAL_VALUE_ATTRIBUTE_FIELD_NAME = "minimalValueAttribute";

    /**
     * Test if the decoding and convert of the minimal value annotation into its java object is valid.
     */
    @Test
    public void testMinimalValueAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Values object
            Object object = null;
            //Inspect all the annotation of the field to get the ValuesAttribute one
            Field valuesField = this.getClass().getDeclaredField(MINIMAL_VALUE_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof ValuesAttribute) {
                    annotationFound = true;
                    ValuesAttribute valuesAnnotation = (ValuesAttribute) annotation;
                    object = ObjectAnnotationConverter.annotationToObject(valuesAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        MINIMAL_VALUE_ATTRIBUTE_FIELD_NAME + "'.");
            }
            boolean conditionValue = object == null;
            if(!conditionValue) {
                String messageValue = "The value is not the one expected (" +
                        ((ValueType) object).getValue() + " instead of " + null;
                Assert.assertTrue(messageValue, conditionValue);
            }

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + MINIMAL_VALUE_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
