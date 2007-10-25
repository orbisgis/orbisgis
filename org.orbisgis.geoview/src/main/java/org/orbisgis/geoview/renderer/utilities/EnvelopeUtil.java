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

package org.orbisgis.geoview.renderer.utilities;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Utility functions for {@link Envelope}s.
 */
public class EnvelopeUtil {
	private static GeometryFactory factory = new GeometryFactory();

	/**
	 * Expands an Envelope by a given distance. Both positive and negative
	 * distances are handled.
	 */
	public static Envelope expand(Envelope env, double distance) {
		/**
		 * If creating a negative buffer, check if Envelope becomes null
		 * (0-size)
		 */
		if (distance < 0) {
			double minSize = 2.0 * -distance;

			if (env.getWidth() < minSize) {
				return new Envelope();
			}

			if (env.getHeight() < minSize) {
				return new Envelope();
			}
		}

		return new Envelope(env.getMinX() - distance, env.getMaxX() + distance,
				env.getMinY() - distance, env.getMaxY() + distance);
	}

	public static void translate(Envelope e, Coordinate displacement) {
		if (e.isNull()) {
			return;
		}

		e.init(e.getMinX() + displacement.x, e.getMaxX() + displacement.x, e
				.getMinY()
				+ displacement.y, e.getMaxY() + displacement.y);
	}

	/**
	 * @param originalEnvelope
	 *            the original envelope
	 * @param extentFraction
	 *            the buffer distance expressed as a fraction of the average
	 *            envelope extent
	 */
	public static Envelope bufferByFraction(Envelope originalEnvelope,
			double extentFraction) {
		Envelope bufferedEnvelope = new Envelope(originalEnvelope);
		double averageExtent = (bufferedEnvelope.getWidth() + bufferedEnvelope
				.getHeight()) / 2d;
		double buffer = averageExtent * extentFraction;

		if (averageExtent == 0) {
			// Point feature. Set the buffer to something reasonable. [Jon
			// Aquino]
			buffer = 10;
		}

		bufferedEnvelope.expandToInclude(bufferedEnvelope.getMaxX() + buffer,
				bufferedEnvelope.getMaxY() + buffer);
		bufferedEnvelope.expandToInclude(bufferedEnvelope.getMinX() - buffer,
				bufferedEnvelope.getMinY() - buffer);

		return bufferedEnvelope;
	}

	public static Coordinate centre(Envelope e) {
		return new Coordinate(MathUtil.avg(e.getMinX(), e.getMaxX()), MathUtil
				.avg(e.getMinY(), e.getMaxY()));
	}

	public static Geometry toGeometry(Envelope envelope) {
		if ((envelope.getWidth() == 0) && (envelope.getHeight() == 0)) {
			return factory.createPoint(new Coordinate(envelope.getMinX(),
					envelope.getMinY()));
		}

		if ((envelope.getWidth() == 0) || (envelope.getHeight() == 0)) {
			return factory.createLineString(new Coordinate[] {
					new Coordinate(envelope.getMinX(), envelope.getMinY()),
					new Coordinate(envelope.getMaxX(), envelope.getMaxY()) });
		}

		return factory.createLinearRing(new Coordinate[] {
				new Coordinate(envelope.getMinX(), envelope.getMinY()),
				new Coordinate(envelope.getMinX(), envelope.getMaxY()),
				new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
				new Coordinate(envelope.getMaxX(), envelope.getMinY()),
				new Coordinate(envelope.getMinX(), envelope.getMinY()) });
	}
}