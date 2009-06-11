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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;


/**
 * Whether an attribute on a feature is a string, geometry, integer, etc.
 *
 * @see com.vividsolutions.jump.feature.FeatureSchema
 */
public class AttributeType implements Serializable {
    private static final long serialVersionUID = -8443945292593638566L;
    private static HashMap nameToAttributeTypeMap = new HashMap();
    /**
     * Returns all instances of AttributeType.
     */
    public static Collection allTypes() {
        return nameToAttributeTypeMap.values();
    }
    /** For strings */
    public final static AttributeType STRING = new AttributeType("STRING", String.class);

    /** For spatial data */
    public final static AttributeType GEOMETRY = new AttributeType("GEOMETRY", Geometry.class);

    /** For long values (64-bit) */
    public final static AttributeType INTEGER = new AttributeType("INTEGER", Integer.class);
    
    /**
     * @see http://www.javaworld.com/javaworld/javatips/jw-javatip122.html
     */
    private Object readResolve() {
        return nameToAttributeTypeMap.get(name);
    }    
    
    /** 
     * For dates 
     * @see java.util.Date 
     */
    public final static AttributeType DATE= new AttributeType("DATE", Date.class);

    //<<TODO:IMPROVE>> If it's a long, perhaps we should name it LONG instead
    //of INTEGER. [Jon Aquino] 
    //Why was it necessary to use long values? [Jon Aquino]

    /** For double-precision values (64-bit) */
    public final static AttributeType DOUBLE = new AttributeType("DOUBLE", Double.class);

    /** Experimental */            
    public final static AttributeType OBJECT = new AttributeType("OBJECT", Object.class );

    private String name;

    private AttributeType(String name, Class javaClass) {
        this.name = name;
        this.javaClass = javaClass;
        nameToAttributeTypeMap.put(name, this);
    }

    public String toString() {
        return name;
    }

    /**
     * Converts a type name to an AttributeType.
     * @param typename the name of the AttributeType to retrieve
     * @return the corresponding AttributeType
     * @throws InvalidAttributeTypeException if the type name is unrecognized
     */
    public final static AttributeType toAttributeType(String name) {
        AttributeType type = (AttributeType) nameToAttributeTypeMap.get(name);

        if (type == null) {
            throw new IllegalArgumentException();
        }

        return type;
    }
    
    /**
     * Returns the Java class of attributes that are said to be of this AttributeType.
     */
	public Class toJavaClass() {
		return javaClass;
	}
	
	private Class javaClass;

    /**
     * Returns the AttributeType of attributes that are instances of the given class,
     * or null if it is not associated with any AttributeType.
     */
    public static AttributeType toAttributeType(Class javaClass) {
        for (Iterator i = allTypes().iterator(); i.hasNext(); ) {
            AttributeType type = (AttributeType) i.next();
            if (type.toJavaClass() == javaClass) {
                return type;
            }
        }
        return null;        
    }
    
}
