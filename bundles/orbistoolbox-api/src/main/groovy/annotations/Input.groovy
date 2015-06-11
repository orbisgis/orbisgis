package annotations

import groovy.transform.AnnotationCollector
import groovy.transform.Field

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation in used to declare the WPS Input fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@interface WpsInput {
    //DataDescription dataDescription() default @DataDescription(test = "test")
    String title()

    String abstrac() default ""

    String keywords() default ""

    String identifier() default ""

    Metadata[] metadata() default []

    int minOccurs() default 1

    int maxOccurs() default 1
}

/**
 * Thanks to the annotation AnnotationCollector, this annotation combine the annotation Field and WpsInput.
 */
@AnnotationCollector([Field, WpsInput])
public @interface Input {}