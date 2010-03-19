//License: GPLv2 or later
//Copyright 2007 by Raphael Mack and others

package org.gdms.driver.gpx.josm;

import java.util.Collection;
import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Read-only gpx track. Implementations doesn't have to be immutable, but should
 * always be thread safe.
 * 
 */

public interface GpxTrack {

	Collection<GpxTrackSegment> getSegments();

	Map<String, Object> getAttributes();

	Envelope getBounds();

}
