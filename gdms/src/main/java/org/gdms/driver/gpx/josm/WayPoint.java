//License: GPLv2 or later
//Copyright 2007 by Raphael Mack and others

package org.gdms.driver.gpx.josm;

import java.awt.Color;
import java.util.Date;

import com.vividsolutions.jts.geom.Coordinate;

public class WayPoint extends WithAttributes implements Comparable<WayPoint> {
	public double time;
	public Color customColoring;
	public boolean drawLine;
	public int dir;

	private static ThreadLocal<PrimaryDateParser> dateParser = new ThreadLocal<PrimaryDateParser>() {
		@Override
		protected PrimaryDateParser initialValue() {
			return new PrimaryDateParser();
		}
	};

	private final Coordinate coor;

	public final Coordinate getCoor() {
		return coor;
	}

	public WayPoint(Coordinate ll) {
		coor = ll;
	}

	@Override
	public String toString() {
		return "WayPoint ("
				+ (attr.containsKey("name") ? attr.get("name") + ", " : "")
				+ coor.toString() + ", " + attr + ")";
	}

	/**
	 * Convert the time stamp of the waypoint into seconds from the epoch
	 */
	public void setTime() {
		if (attr.containsKey("time")) {
			try {
				time = dateParser.get().parse(attr.get("time").toString())
						.getTime() / 1000.; /* ms => seconds */
			} catch (Exception e) {
				time = 0;
			}
		}
	}

	public int compareTo(WayPoint w) {
		return Double.compare(time, w.time);
	}

	public Date getTime() {
		return new Date((long) (time * 1000));
	}
}
