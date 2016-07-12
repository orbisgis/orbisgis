package org.orbisgis.wpsservice.controller.utils;

import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import net.opengis.wps._2_0.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;

/**
 * @author Sylvain PALOMINOS
 */
public class InputDescriptionTypeConvertTest {

    /*******************************
     * FULL INPUT DESCRIPTION TYPE *
     *******************************/

    /** Field containing the full annotation. */
    @InputAttribute(
            minOccurs = 0,
            maxOccurs = 10
    )
    public Object fullInputAttribute;
    /** Name of the field containing the fullRangeAttribute annotation. */
    private static final String FULL_INPUT_ATTRIBUTE_FIELD_NAME = "fullInputAttribute";

    /**
     * Test if the decoding and convert of the full inputDescriptionType annotation into its java object is valid.
     */
    @Test
    public void testFullInputDescriptionTypeAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the InputDescriptionType object
            InputDescriptionType inputDescriptionType = new InputDescriptionType();
            //Inspect all the annotation of the field to get the InputAttribute one
            Field inputField = this.getClass().getDeclaredField(FULL_INPUT_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : inputField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof InputAttribute) {
                    annotationFound = true;
                    InputAttribute inputAnnotation = (InputAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(inputAnnotation, inputDescriptionType);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        FULL_INPUT_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ////////////////////////////////////////////
            // Build the InputDescriptionType to test //
            ////////////////////////////////////////////

            InputDescriptionType toTest = new InputDescriptionType();
            toTest.setMaxOccurs("10");
            toTest.setMinOccurs(new BigInteger("0"));

            ////////////////////////////////////
            // Tests the InputDescriptionType //
            ////////////////////////////////////

            //Test the max occurs
            String messageMaxOccurs = "The max occurs value is not the one expected (" +
                    inputDescriptionType.getMaxOccurs() + " instead of " + toTest.getMaxOccurs();
            boolean conditionMaxOccurs = inputDescriptionType.getMaxOccurs().equals(toTest.getMaxOccurs());
            Assert.assertTrue(messageMaxOccurs, conditionMaxOccurs);

            //Test the min occurs
            String messageMinOccurs = "The min occurs value is not the one expected (" +
                    inputDescriptionType.getMinOccurs() + " instead of " + toTest.getMinOccurs();
            boolean conditionMinOccurs = inputDescriptionType.getMinOccurs().equals(toTest.getMinOccurs());
            Assert.assertTrue(messageMinOccurs, conditionMinOccurs);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_INPUT_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }

    /**********************************
     * MINIMAL INPUT DESCRIPTION TYPE *
     **********************************/

    /** Field containing the minimal annotation. */
    @InputAttribute()
    public Object minimalInputAttribute;
    /** Name of the field containing the minimalRangeAttribute annotation. */
    private static final String MINIMAL_INPUT_ATTRIBUTE_FIELD_NAME = "minimalInputAttribute";

    /**
     * Test if the decoding and convert of the minimal inputDescriptionType annotation into its java object is valid.
     */
    @Test
    public void testMinimalInputDescriptionTypeAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the InputDescriptionType object
            InputDescriptionType inputDescriptionType = new InputDescriptionType();
            //Inspect all the annotation of the field to get the InputAttribute one
            Field inputField = this.getClass().getDeclaredField(MINIMAL_INPUT_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : inputField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof InputAttribute) {
                    annotationFound = true;
                    InputAttribute inputAnnotation = (InputAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(inputAnnotation, inputDescriptionType);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        MINIMAL_INPUT_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ////////////////////////////////////////////
            // Build the InputDescriptionType to test //
            ////////////////////////////////////////////

            InputDescriptionType toTest = new InputDescriptionType();
            toTest.setMaxOccurs(""+InputAttribute.defaultMaxOccurs);
            toTest.setMinOccurs(new BigInteger(""+InputAttribute.defaultMinOccurs));

            ////////////////////////////////////
            // Tests the InputDescriptionType //
            ////////////////////////////////////

            //Test the max occurs
            String messageMaxOccurs = "The max occurs value is not the one expected (" +
                    inputDescriptionType.getMaxOccurs() + " instead of " + toTest.getMaxOccurs();
            boolean conditionMaxOccurs = inputDescriptionType.getMaxOccurs().equals(toTest.getMaxOccurs());
            Assert.assertTrue(messageMaxOccurs, conditionMaxOccurs);

            //Test the min occurs
            String messageMinOccurs = "The min occurs value is not the one expected (" +
                    inputDescriptionType.getMinOccurs() + " instead of " + toTest.getMinOccurs();
            boolean conditionMinOccurs = inputDescriptionType.getMinOccurs().equals(toTest.getMinOccurs());
            Assert.assertTrue(messageMinOccurs, conditionMinOccurs);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + MINIMAL_INPUT_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
