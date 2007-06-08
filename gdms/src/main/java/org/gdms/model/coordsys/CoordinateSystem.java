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

package org.gdms.model.coordsys;

import java.io.Serializable;

import com.vividsolutions.jts.util.Assert;

/**
 * This class represents a coordinate system.
 */
public class CoordinateSystem implements Comparable, Serializable {
	private static final long serialVersionUID = -811718450919581831L;

	private Projection projection;

	private String name;

	private int epsgCode;

	public static final CoordinateSystem UNSPECIFIED = new CoordinateSystem(
			"Unspecified", 0, null) {
		private static final long serialVersionUID = -811718450919581831L;

		public Projection getProjection() {
			throw new UnsupportedOperationException();
		}

		public int getEPSGCode() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * @see http://www.javaworld.com/javaworld/javatips/jw-javatip122.html
	 */
	private Object readResolve() {
		return name.equals(UNSPECIFIED.name) ? UNSPECIFIED : this;
	}

	public CoordinateSystem(String name, int epsgCode, Projection projection) {
		this.name = name;
		this.projection = projection;
		this.epsgCode = epsgCode;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Projection getProjection() {
		return projection;
	}

	public int getEPSGCode() {
		return epsgCode;
	}

	public int compareTo(Object o) {
		Assert.isTrue(o instanceof CoordinateSystem);
		if (this == o) {
			return 0;
		}
		if (this == UNSPECIFIED) {
			return -1;
		}
		if (o == UNSPECIFIED) {
			return 1;
		}
		return toString().compareTo(o.toString());
	}

}
