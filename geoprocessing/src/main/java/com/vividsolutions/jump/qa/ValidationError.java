
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


import org.gdms.model.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jump.geom.InteriorPointFinder;


/**
 * An error with a Feature, found by Validator.
 * @see Validator
 */
public class ValidationError {
    private static InteriorPointFinder interiorPointFinder = new InteriorPointFinder();
    private ValidationErrorType type;
    private Feature feature;
    private Coordinate location;

    /**
     * Creates a ValidationError with location unspecified.
     * @param type the kind of error found
     * @param feature the feature with the error
     */
    public ValidationError(ValidationErrorType type, Feature feature) {
        this(type, feature, location(feature.getGeometry()));
    }

    /**
     * Creates a ValidationError.
     * @param type the kind of error found
     * @param feature the feature with the error
     * @param location a point near the error
     */
    public ValidationError(ValidationErrorType type, Feature feature,
        Coordinate location) {
        this.type = type;
        this.feature = feature;
        this.location = location;
    }

    /**
     * Creates a ValidationError with location unspecified.
     * @param type the kind of error found
     * @param feature the feature with the error
     * @param badPart the part of the feature having the error
     */
    public ValidationError(ValidationErrorType type, Feature feature,
        Geometry badPart) {
        this(type, feature, location(badPart));
    }

    /**
     * Returns the kind of error found.
     * @return the kind of error found
     */
    public ValidationErrorType getType() {
        return type;
    }

    /**
     * Returns a description of the error.
     * @return a description of the error
     */
    public String getMessage() {
        return type.getMessage();
    }

    /**
     * Returns the feature with the error.
     * @return the feature with the error
     */
    public Feature getFeature() {
        return feature;
    }

    /**
     * Returns a point near the error.
     * @return a point near the error
     */
    public Coordinate getLocation() {
        return location;
    }

    private static Coordinate location(Geometry g) {
        try {
            return interiorPointFinder.findPoint(g);
        } catch (Exception ex) {
            //InteriorPointFinder may fail because the geometry has bad topology [Jon Aquino]
            return interiorPointFinder.centre(g.getEnvelopeInternal());
        }
    }
}
