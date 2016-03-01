package org.orbisgis.wpsgroovyapi.attributes

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Attributes for the Geometry complex data.
 * The Geometry is a complex data that represents a geometry.
 *
 * The following fields can be defined (optional) :
 *  - geometryType : String[]
 *      Array of geometry type allowed. If no types are specified, accept all.
 *  - excludedTypes : String[]
 *      Array of the type not allowed for the geometry.
 *  - dimension : int
 *      Dimension of the geometry (can be 2 or 3).
 *
 * @author Sylvain PALOMINOS
 */

@Retention(RetentionPolicy.RUNTIME)
@interface GeometryAttribute {

    /** Array of geometry type allowed. If no types are specified, accept all.*/
    String[] geometryType() default []

    /** Array of the type not allowed for the geometry.*/
    String[] excludedTypes() default []

    /** Dimension of the geometry (can be 2 or 3). */
    int dimension() default 2



    /********************/
    /** default values **/
    /********************/
    public static final boolean defaultGeometryType = []
    public static final boolean defaultExcludedType = []
    public static final boolean defaultDimension = 2

}