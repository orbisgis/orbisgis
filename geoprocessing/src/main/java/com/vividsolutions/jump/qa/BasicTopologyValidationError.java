
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
import com.vividsolutions.jts.operation.valid.TopologyValidationError;



/**
 * Adapts a JTS TopologyValidationError to ValidationError. JTS
 * TopologyValidationErrors are created during basic JTS validation,
 * performed by Validator.
 * @see Validator
 */
public class BasicTopologyValidationError extends ValidationError {
    private TopologyValidationError basicTopologyError;

    /**
     * Creates a BasicTopologyValidationError that wraps a JTS TopologyValidationError.
     * @param basicTopologyError the JTS error to wrap
     * @param feature the feature with the error
     */
    public BasicTopologyValidationError(
        TopologyValidationError basicTopologyError, Feature feature) {
        super(ValidationErrorType.BASIC_TOPOLOGY_INVALID, feature);
        this.basicTopologyError = basicTopologyError;
    }

    public String getMessage() {
        return basicTopologyError.getMessage();
    }

    public Coordinate getLocation() {
        return basicTopologyError.getCoordinate();
    }
}
