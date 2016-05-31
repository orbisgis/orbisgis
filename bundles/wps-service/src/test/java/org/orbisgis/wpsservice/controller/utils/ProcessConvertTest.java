package org.orbisgis.wpsservice.controller.utils;

import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.ProcessAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class ProcessConvertTest {
    /****************
     * FULL PROCESS *
     ****************/

    /** Field containing the full annotation. */
    @ProcessAttribute(
            language = "en"
    )
    public Object fullProcessAttribute;
    /** Name of the field containing the full ProcessAttribute annotation. */
    private static final String FULL_PROCESS_ATTRIBUTE_FIELD_NAME = "fullProcessAttribute";

    /**
     * Test if the decoding and convert of the full process annotation into its java object is valid.
     */
    @Test
    public void testFullProcessAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Values object
            ProcessDescriptionType process = new ProcessDescriptionType();
            ProcessOffering processOffering = new ProcessOffering();
            processOffering.setProcess(process);
            //Inspect all the annotation of the field to get the ValuesAttribute one
            Field valuesField = this.getClass().getDeclaredField(FULL_PROCESS_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof ProcessAttribute) {
                    annotationFound = true;
                    ProcessAttribute processAnnotation = (ProcessAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(processAnnotation, processOffering);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        FULL_PROCESS_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ////////////////////////////
            // Tests the Range Values //
            ////////////////////////////

            String language = "en";

            //Test the maximum
            String messageLanguage = "The process language is not the one expected (" + process.getLang() +
                    " instead of " + language;
            boolean conditionLanguage = process.getLang().equals(language);
            Assert.assertTrue(messageLanguage, conditionLanguage);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_PROCESS_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
