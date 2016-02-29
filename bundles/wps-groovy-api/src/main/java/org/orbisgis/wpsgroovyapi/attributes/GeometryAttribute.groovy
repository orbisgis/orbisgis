package org.orbisgis.wpsgroovyapi.attributes

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Attributes for the Geometry complex data.
 * The Geometry is a complex data that represents a geometry.
 *
 * The following fields can be defined (optional) :
 *  - formats : FormatAttribute[]
 *      List of supported formats.
 *  - isDirectory : boolean
 *      Indicates that the RawData can be a directory.
 *  - isFile : boolean
 *      Indicates that the RawData can be a file.
 *
 * @author Sylvain PALOMINOS
 */

@Retention(RetentionPolicy.RUNTIME)
@interface GeometryAttribute {

}