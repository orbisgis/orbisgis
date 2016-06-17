package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.*;
import net.opengis.wfs._2_1.PropertyType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.PossibleLiteralValuesChoiceAttribute;
import org.orbisgis.wpsgroovyapi.attributes.ValuesAttribute;
import org.orbisgis.wpsgroovyapi.attributes.ValuesType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

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
