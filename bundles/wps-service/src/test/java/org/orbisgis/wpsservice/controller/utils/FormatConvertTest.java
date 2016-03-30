package org.orbisgis.wpsservice.controller.utils;

import net.opengis.wps.v_2_0.Format;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.FormatAttribute;

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
