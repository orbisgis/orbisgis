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
import java.util.ArrayList;
import java.util.HashMap;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.util.Assert;

/**
 * Metadata for a FeatureCollection: attribute names and types.
 *
 * @see FeatureCollection
 */
public class FeatureSchema implements Cloneable, Serializable {
	private static final long serialVersionUID = -8627306219650589202L;

	private CoordinateReferenceSystem coordinateSystem = null;

	private HashMap attributeNameToIndexMap = new HashMap();

	private int geometryIndex = -1;

	private int attributeCount = 0;

	private ArrayList attributeNames = new ArrayList();

	private ArrayList attributeTypes = new ArrayList();

	// todo Deep-copy! [Jon Aquino]
	// deep copy done 25. Juli 2005 [sstein]
	public Object clone() {
		try {
			FeatureSchema fs = new FeatureSchema();
			for (int i = 0; i < this.attributeCount; i++) {
				AttributeType at = (AttributeType) this.attributeTypes.get(i);
				String aname = (String) this.attributeNames.get(i);
				fs.addAttribute(aname, at);
				// fs.setCoordinateSystem(this.coordinateSystem);
			}
			return fs;
		} catch (Exception ex) {
			Assert.shouldNeverReachHere();
			return null;
		}
	}

	/**
	 * Returns the zero-based index of the attribute with the given name
	 * (case-sensitive)
	 *
	 * @throws IllegalArgumentException
	 *             if attributeName is unrecognized
	 */
	public int getAttributeIndex(String attributeName) {
		// <<TODO:RECONSIDER>> Attribute names are currently case sensitive.
		// I wonder whether or not this is desirable. [Jon Aquino]
		Integer index = (Integer) attributeNameToIndexMap.get(attributeName);
		if (index == null) {
			throw new IllegalArgumentException();
			// I18N.get("feature.FeatureSchema.unrecognized-attribute-name")+" "
			// + attributeName);
		}
		return index.intValue();
	}

	/**
	 * Returns whether this FeatureSchema has an attribute with this name
	 *
	 * @param attributeName
	 *            the name to look up
	 * @return whether this FeatureSchema has an attribute with this name
	 */
	public boolean hasAttribute(String attributeName) {
		return attributeNameToIndexMap.get(attributeName) != null;
	}

	/**
	 * Returns the attribute index of the Geometry, or -1 if there is no
	 * Geometry attribute
	 */
	public int getGeometryIndex() {
		return geometryIndex;
	}

	/**
	 * Returns the (case-sensitive) name of the attribute at the given
	 * zero-based index.
	 */
	public String getAttributeName(int attributeIndex) {
		return (String) attributeNames.get(attributeIndex);
	}

	/**
	 * Returns whether the attribute at the given zero-based index is a string,
	 * integer, double, etc.
	 */
	public AttributeType getAttributeType(int attributeIndex) {
		return (AttributeType) attributeTypes.get(attributeIndex);
	}

	/**
	 * Returns whether the attribute with the given name (case-sensitive) is a
	 * string, integer, double, etc.
	 */
	public AttributeType getAttributeType(String attributeName) {
		return getAttributeType(getAttributeIndex(attributeName));
	}

	/**
	 * Returns the total number of spatial and non-spatial attributes in this
	 * FeatureSchema. There are 0 or 1 spatial attributes and 0 or more
	 * non-spatial attributes.
	 */
	public int getAttributeCount() {
		return attributeCount;
	}

	/**
	 * Adds an attribute with the given case-sensitive name.
	 *
	 * @throws AssertionFailedException
	 *             if a second Geometry is being added
	 */
	public void addAttribute(String attributeName, AttributeType attributeType) {
		if (AttributeType.GEOMETRY == attributeType) {
			Assert.isTrue(geometryIndex == -1);
			geometryIndex = attributeCount;
		}
		attributeNames.add(attributeName);
		attributeNameToIndexMap.put(attributeName, new Integer(attributeCount));
		attributeTypes.add(attributeType);
		attributeCount++;
	}

	/**
	 * Returns whether the two FeatureSchemas have the same attribute names with
	 * the same types and in the same order.
	 */
	public boolean equals(Object other) {
		return this.equals(other, false);
	}

	/**
	 * Returns whether the two FeatureSchemas have the same attribute names with
	 * the same types and (optionally) in the same order.
	 */
	public boolean equals(Object other, boolean orderMatters) {
		if (!(other instanceof FeatureSchema)) {
			return false;
		}
		FeatureSchema otherFeatureSchema = (FeatureSchema) other;
		if (attributeNames.size() != otherFeatureSchema.attributeNames.size()) {
			return false;
		}
		for (int i = 0; i < attributeNames.size(); i++) {
			String attributeName = (String) attributeNames.get(i);
			if (!otherFeatureSchema.attributeNames.contains(attributeName)) {
				return false;
			}
			if (orderMatters
					&& !otherFeatureSchema.attributeNames.get(i).equals(
							attributeName)) {
				return false;
			}
			if (getAttributeType(attributeName) != otherFeatureSchema
					.getAttributeType(attributeName)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Sets the CoordinateSystem associated with this FeatureSchema, but does
	 * not perform any reprojection.
	 *
	 * @return this FeatureSchema
	 */
	public FeatureSchema setCoordinateSystem(CoordinateReferenceSystem coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
		return this;
	}

	/**
	 * @see #setCoordinateSystem(CoordinateSystem)
	 */
	public CoordinateReferenceSystem getCoordinateSystem() {
		return coordinateSystem;
	}

}
