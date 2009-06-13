
/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jump.qa;



/**
 * The types of validation errors detected by Validator.
 * @see Validator
 */
public class ValidationErrorType {
    /** Geometry class not allowed */
    public final static ValidationErrorType GEOMETRY_CLASS_DISALLOWED = new ValidationErrorType(
           "geometry-class-not-allowed");

    /** Basic topology is invalid */
    public final static ValidationErrorType BASIC_TOPOLOGY_INVALID = new ValidationErrorType(
    		"basic-topology-is-invalid");

    /** Polygon shell is oriented counter-clockwise */
    public final static ValidationErrorType EXTERIOR_RING_CCW = new ValidationErrorType(
    		"polygon-shell-is-oriented-counter-clockwise");

    /** Polygon hole is oriented clockwise */
    public final static ValidationErrorType INTERIOR_RING_CW = new ValidationErrorType(
    		"polygon-hole-is-oriented-clockwise");

    /** Linestring not simple */
    public final static ValidationErrorType NONSIMPLE_LINESTRING = new ValidationErrorType(
    		"linestring-not-simple");

    /** Contains segment with length below minimum */
    public final static ValidationErrorType SMALL_SEGMENT = new ValidationErrorType(
    		"contains-segment-with-length-below-minimum");

    /** Is/contains polygon with area below minimum */
    public final static ValidationErrorType SMALL_AREA = new ValidationErrorType(
    		"is-contain-polygon-with-area-below-minimum");

    /** Contains segments with angle below minimum */
    public final static ValidationErrorType SMALL_ANGLE = new ValidationErrorType(
    		"contains-segments-with-angle-below-minimum");

    /** Polygon has holes */
    public final static ValidationErrorType POLYGON_HAS_HOLES = new ValidationErrorType(
    		"polygon-has-holes");

    /** Consecutive points are the same */
    public final static ValidationErrorType REPEATED_CONSECUTIVE_POINTS = new ValidationErrorType(
    		"consecutive-points-are-the-same");
    private String message;

    private ValidationErrorType(String message) {
        this.message = message;
    }

    /**
     * Returns a description of the error.
     * @return a description of the error
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return getMessage();
    }
}
