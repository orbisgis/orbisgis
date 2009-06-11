
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

package org.gdms.model;

import java.io.Serializable;


/**
 *  Default implementation of the Feature interface.
 */
public class BasicFeature extends AbstractBasicFeature implements Serializable {

    private static final long serialVersionUID = -7891137208054228529L;
    
    private Object[] attributes;

    /**
     * Constructs a BasicFeature with the given FeatureSchema specifying the
     * attribute names and types.
     */
    public BasicFeature(FeatureSchema featureSchema) {
        super(featureSchema);
        attributes = new Object[featureSchema.getAttributeCount()];        
    }


    /**
     * A low-level accessor that is not normally used. It is called by ViewSchemaPlugIn.
     */
    public void setAttributes(Object[] attributes) {
        this.attributes = attributes;
    }

    /**
     *  Sets the specified attribute.
     *
     *@param  attributeIndex  the array index at which to put the new attribute
     *@param  newAttribute    the new attribute
     */
    public void setAttribute(int attributeIndex, Object newAttribute) {
        attributes[attributeIndex] = newAttribute;
    }

    /**
     *  Returns the specified attribute.
     *
     *@param  i the index of the attribute to get
     *@return the attribute
     */
    public Object getAttribute(int i) {
        return attributes[i];
        //We used to eat ArrayOutOfBoundsExceptions here. I've removed this behaviour
        //because ArrayOutOfBoundsExceptions are bugs and should be exposed. [Jon Aquino]
    }

    /**
     * A low-level accessor that is not normally used. It is called by ViewSchemaPlugIn.
     */
    public Object[] getAttributes() {
        return attributes;
    }




}
