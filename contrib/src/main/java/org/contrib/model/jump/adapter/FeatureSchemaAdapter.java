/* OrbisCAD. The Community cartography editor
 *
 * Copyright (C) 2005, 2006 OrbisCAD development team
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  OrbisCAD development team
 *   elgallego@users.sourceforge.net
 */
package org.contrib.model.jump.adapter;

import org.contrib.model.jump.model.AttributeType;
import org.contrib.model.jump.model.FeatureSchema;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;


public class FeatureSchemaAdapter extends FeatureSchema {

	private SpatialDataSourceDecorator ds;

	public FeatureSchemaAdapter(SpatialDataSourceDecorator ds) {
		this.ds = ds;
	}

	public int getAttributeCount() {
		try {
			return ds.getMetadata().getFieldCount();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public String getAttributeName(int attributeIndex) {
		try {
			return ds.getMetadata().getFieldName(attributeIndex);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public int getAttributeIndex(String attributeName) {
		try {
			return ds.getFieldIndexByName(attributeName);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public int getGeometryIndex() {
		try {
			return ds.getSpatialFieldIndex();
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
	}

	public SpatialDataSourceDecorator getDs() {
		return ds;
	}

	@Override
	public AttributeType getAttributeType(int attributeIndex) {

		Type fieldType;
		try {
			fieldType = ds.getMetadata().getFieldType(attributeIndex);
		} catch (DriverException e) {
			throw new RuntimeException(e);
		}
		int fieldTYpe = fieldType.getTypeCode();

		switch (fieldTYpe) {

		case Type.RASTER:
		case Type.BINARY:
			return AttributeType.OBJECT;
		case Type.STRING:
		case Type.BOOLEAN:
			return AttributeType.STRING;
		case Type.DOUBLE:
		case Type.FLOAT:
			return AttributeType.DOUBLE;
		case Type.GEOMETRY:
			return AttributeType.GEOMETRY;
		case Type.BYTE:
		case Type.INT:
		case Type.LONG:
		case Type.SHORT:
			return AttributeType.INTEGER;
		default:
			return AttributeType.OBJECT;
		}

	}

}
