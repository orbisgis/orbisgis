// License: GPL. For details, see LICENSE file.
package org.gdms.driver.gpx.josm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;

public class ImmutableGpxTrack implements GpxTrack {

	private final Map<String, Object> attributes;
	private final Collection<GpxTrackSegment> segments;
	private final Envelope bounds;

	public ImmutableGpxTrack(Collection<Collection<WayPoint>> trackSegs,
			Map<String, Object> attributes) {
		List<GpxTrackSegment> newSegments = new ArrayList<GpxTrackSegment>();
		for (Collection<WayPoint> trackSeg : trackSegs) {
			if (trackSeg != null && !trackSeg.isEmpty()) {
				newSegments.add(new ImmutableGpxTrackSegment(trackSeg));
			}
		}
		this.attributes = Collections
				.unmodifiableMap(new HashMap<String, Object>(attributes));
		this.segments = Collections.unmodifiableCollection(newSegments);
		this.bounds = calculateBounds();
	}

	private Envelope calculateBounds() {
		Envelope result = null;
		for (GpxTrackSegment segment : segments) {
			Envelope segBounds = segment.getBounds();
			if (segBounds != null) {
				if (result == null) {
					result = new Envelope(segBounds);
				} else {
					result.expandToInclude(segBounds);
				}
			}
		}
		return result;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Envelope getBounds() {
		if (bounds == null)
			return null;
		else
			return new Envelope(bounds);
	}

	public Collection<GpxTrackSegment> getSegments() {
		return segments;
	}
}
