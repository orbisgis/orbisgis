// License: GPL. For details, see LICENSE file.
package org.gdms.driver.gpx.josm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.vividsolutions.jts.geom.Envelope;

public class ImmutableGpxTrackSegment implements GpxTrackSegment {

	private final Collection<WayPoint> wayPoints;
	private final Envelope bounds;

	public ImmutableGpxTrackSegment(Collection<WayPoint> wayPoints) {
		this.wayPoints = Collections
				.unmodifiableCollection(new ArrayList<WayPoint>(wayPoints));
		this.bounds = calculateBounds();
	}

	private Envelope calculateBounds() {
		Envelope result = null;
		for (WayPoint wpt : wayPoints) {
			if (result == null) {
				result = new Envelope(wpt.getCoor());
			} else {
				result.expandToInclude(wpt.getCoor());
			}
		}
		return result;
	}

	public Envelope getBounds() {
		if (bounds == null)
			return null;
		else
			return new Envelope(bounds);
	}

	public Collection<WayPoint> getWayPoints() {
		return wayPoints;
	}

}
