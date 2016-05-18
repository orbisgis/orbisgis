package org.orbisgis.wpsservice.controller.utils;

import net.opengis.ows._2.MetadataType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.MetadataAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class MetadataConvertTest {

    /*****************
     * FULL METADATA *
     *****************/

    /** Field containing the full MetadataAttribute annotation. */
    @MetadataAttribute(
            title = "title",
            href = "href",
            linkType = "simple",
            role = "role"
    )
    public Object fullMetadataAttribute;
    /** Name of the field containing the fullMetadataAttribute annotation. */
    private static final String FULL_METADATA_ATTRIBUTE_FIELD_NAME = "fullMetadataAttribute";

    /**
     * Test if the decoding and convert of the full MetadataAttribute annotation into its java object is valid.
     */
    @Test
    public void testFullMetadataAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Metadata object
            MetadataType metadata = new MetadataType();
            //Inspect all the annotation of the field to get the MetadataAttribute one
            Field metadataField = this.getClass().getDeclaredField(FULL_METADATA_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : metadataField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof MetadataAttribute) {
                    annotationFound = true;
                    MetadataAttribute metadataAnnotation = (MetadataAttribute) annotation;
                    metadata = ObjectAnnotationConverter.annotationToObject(metadataAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@MetadataAnnotation' from the field '" +
                        FULL_METADATA_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ////////////////////////////////
            // Build the Metadata to test //
            ////////////////////////////////

            MetadataType toTest = new MetadataType();

            toTest.setHref("href");
            toTest.setRole("role");
            toTest.setTitle("title");


            ////////////////////////////////
            // Build the Metadata to test //
            ////////////////////////////////

            //Test the href
            String messageHref = "The href value is not the one expected (" +
                    metadata.getHref() + " instead of " + toTest.getHref();
            boolean conditionHref = metadata.getHref().equals(toTest.getHref());
            Assert.assertTrue(messageHref, conditionHref);

            //Test the role
            String messageRole = "The role value is not the one expected (" +
                    metadata.getRole() + " instead of " + toTest.getRole();
            boolean conditionRole = metadata.getRole().equals(toTest.getRole());
            Assert.assertTrue(messageRole, conditionRole);

            //Test the title
            String messageTitle = "The title value is not the one expected (" +
                    metadata.getTitle() + " instead of " + toTest.getTitle();
            boolean conditionTitle = metadata.getTitle().equals(toTest.getTitle());
            Assert.assertTrue(messageTitle, conditionTitle);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_METADATA_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }

    /*******************
     * SIMPLE METADATA *
     *******************/

    /** Field containing the simple MetadataTypeAttribute annotation. */
    @MetadataAttribute(
            title = "title",
            href = "href",
            role = "role"
    )
    public Object simpleMetadataAttribute;
    /** Name of the field containing the simpleMetadataAttribute annotation. */
    private static final String SIMPLE_METADATA_ATTRIBUTE_FIELD_NAME = "simpleMetadataAttribute";

    /**
     * Test if the decoding and convert of the simple MetadataAttribute annotation into its java object is valid.
     */
    @Test
    public void testSimpleMetadataAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Metadata object
            MetadataType metadata = new MetadataType();
            //Inspect all the annotation of the field to get the MetadataAttribute one
            Field metadataField = this.getClass().getDeclaredField(SIMPLE_METADATA_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : metadataField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof MetadataAttribute) {
                    annotationFound = true;
                    MetadataAttribute metadataAnnotation = (MetadataAttribute) annotation;
                    metadata = ObjectAnnotationConverter.annotationToObject(metadataAnnotation);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@MetadataAnnotation' from the field '" +
                        SIMPLE_METADATA_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ////////////////////////////////
            // Build the Metadata to test //
            ////////////////////////////////

            MetadataType toTest = new MetadataType();

            toTest.setHref("href");
            toTest.setRole("role");
            toTest.setTitle("title");


            ////////////////////////////////
            // Build the Metadata to test //
            ////////////////////////////////

            //Test the href
            String messageHref = "The href value is not the one expected (" +
                    metadata.getHref() + " instead of " + toTest.getHref();
            boolean conditionHref = metadata.getHref().equals(toTest.getHref());
            Assert.assertTrue(messageHref, conditionHref);

            //Test the role
            String messageRole = "The role value is not the one expected (" +
                    metadata.getRole() + " instead of " + toTest.getRole();
            boolean conditionRole = metadata.getRole().equals(toTest.getRole());
            Assert.assertTrue(messageRole, conditionRole);

            //Test the title
            String messageTitle = "The title value is not the one expected (" +
                    metadata.getTitle() + " instead of " + toTest.getTitle();
            boolean conditionTitle = metadata.getTitle().equals(toTest.getTitle());
            Assert.assertTrue(messageTitle, conditionTitle);


        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + SIMPLE_METADATA_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
