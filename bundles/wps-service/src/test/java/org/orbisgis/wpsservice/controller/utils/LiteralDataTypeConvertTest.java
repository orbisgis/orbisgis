package org.orbisgis.wpsservice.controller.utils;

import net.opengis.wps._2_0.Format;
import net.opengis.wps._2_0.LiteralDataType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.*;

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
            formats = {
                    @FormatAttribute(mimeType = "mimetype", schema = "schema"),
                    @FormatAttribute(mimeType = "mimetype", schema = "schema")
            },
            validDomains = {
                    @LiteralDataDomainAttribute(
                            possibleLiteralValues = @PossibleLiteralValuesChoiceAttribute(),
                            dataType = "STRING",
                            selectedValues = "value"
                    ),
                    @LiteralDataDomainAttribute(
                            possibleLiteralValues = @PossibleLiteralValuesChoiceAttribute(),
                            dataType = "STRING",
                            selectedValues = "value"
                    )
            }
    )
    public Object fullLiteralDataAttribute;
    /** Name of the field containing the fullLiteralDataAttribute annotation. */
    private static final String FULL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME = "fullLiteralDataAttribute";

    /**
     * Test if the decoding and convert of the full literalData annotation into its java object is valid.
     */
    @Test
    public void testFullLiteralDataAttributeConvert() {
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
                    literalDataType = ObjectAnnotationConverter.annotationToObject(literalDataAnnotation, null);
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
    public Object minimalLiteralDataAttribute;
    /** Name of the field containing the minimalLiteralDataAttribute annotation. */
    private static final String MINIMAL_LITERAL_DATA_ATTRIBUTE_FIELD_NAME = "minimalLiteralDataAttribute";

    /**
     * Test if the decoding and convert of the minimal literalData annotation into its java object is valid.
     */
    @Test
    public void testMinimalLiteralDataAttributeConvert() {
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
                    literalDataType = ObjectAnnotationConverter.annotationToObject(literalDataAnnotation, null);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound || literalDataType == null) {
                Assert.fail("Unable to get the annotation '@LiteraldataDomainAttribute' from the field '" +
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
