/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * JUMP is Copyright (C) 2003 Vivid Solutions
 *
 * This program implements extensions to JUMP and is
 * Copyright (C) 2004 Integrated Systems Analysts, Inc.
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
 * Integrated Systems Analysts, Inc.
 * 630C Anchors St., Suite 101
 * Fort Walton Beach, Florida
 * USA
 *
 * (850)862-7321
 */

package org.gdms.jts.util;

import com.vividsolutions.jts.geom.Coordinate;

public class MathVector implements Cloneable, java.io.Serializable {
	private double x;
	private double y;
	private double mag;

	public MathVector() {
		this(0, 0);
	}

	public MathVector(Coordinate coord) {
		this(coord.x, coord.y);
	}

	public MathVector(double x, double y) {
		if ((new Double(x)).isNaN())
			this.x = 0.0;
		else
			this.x = x;

		if ((new Double(y)).isNaN())
			this.y = 0.0;
		else
			this.y = y;

		mag = Math.sqrt(x * x + y * y);
	}

	public double x() {
		return x;
	}

	public double y() {
		return y;
	}

	public Coordinate getCoord() {
		return new Coordinate(x, y);
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public boolean equals(Object object) {
		if (object instanceof MathVector) {
			MathVector vector = (MathVector) object;

			return x == vector.x() && y == vector.y();
		}
		return false;
	}

	public Object clone() {
		return new MathVector(x, y);
	}

	public double magnitude() {
		return mag;
	}

	public MathVector add(MathVector vector) {
		return new MathVector(x + vector.x(), y + vector.y());
	}

	public MathVector scale(double number) {
		return new MathVector(x * number, y * number);
	}

	public MathVector unit() {
		if (mag == 0)
			return new MathVector();
		else
			return new MathVector(x / mag, y / mag);
	}

	public double dot(MathVector vector) {
		return x * vector.x() + y * vector.y();
	}

	public double distance(MathVector vector) {
		return scale(-1).add(vector).magnitude();
	}

	public MathVector vectorBetween(MathVector vector) {
		return new MathVector(x - vector.x(), y - vector.y());
	}

	public double angleRad(MathVector vector) {
		// compute the angle in radians between vectors from 0 to pi
		// NOTE: this routine returns POSITIVE angles only
		return Math.acos(dot(vector) / (mag * vector.magnitude()));
	}

	public double angleDeg(MathVector vector) {
		// compute the angle in degrees between vectors from 0 to pi
		// NOTE: this routine returns POSITIVE angles only
		return Math.toDegrees(Math.acos(dot(vector)
				/ (mag * vector.magnitude())));
	}

	public MathVector rotateDeg(double angle) { // + clockwise
		double tr = Math.toRadians(angle);
		double ct = Math.cos(tr);
		double st = Math.sin(tr);
		return new MathVector((x * ct + y * st), (y * ct - st * x));
	}

	public MathVector rotateRad(double angle) { // + clockwise
		double tr = angle;
		double ct = Math.cos(tr);
		double st = Math.sin(tr);
		return new MathVector((x * ct + y * st), (y * ct - st * x));
	}
}
