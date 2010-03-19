// License: GPL. For details, see LICENSE file.
package org.gdms.driver.gpx.josm;

import java.util.Collection;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Read-only gpx track segments. Implementations doesn't have to be immutable,
 * but should always be thread safe.
 * 
 */
public interface GpxTrackSegment {

	Envelope getBounds();

	Collection<WayPoint> getWayPoints();

}
