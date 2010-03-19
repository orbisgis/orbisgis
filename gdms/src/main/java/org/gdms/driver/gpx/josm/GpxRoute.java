//License: GPLv2 or later
//Copyright 2007 by Raphael Mack and others

package org.gdms.driver.gpx.josm;

import java.util.Collection;
import java.util.LinkedList;

public class GpxRoute extends WithAttributes {
    public Collection<WayPoint> routePoints = new LinkedList<WayPoint>();
}
