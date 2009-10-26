/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.geoalgorithm.jts.operation;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geoalgorithm.jts.util.CoordinateArrays;
import org.geoalgorithm.jts.util.LineSegmentUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;

public class UniqueSegmentsExtracter {

	private Set<LineSegment> segmentSet = new TreeSet<LineSegment>();

	public UniqueSegmentsExtracter(Geometry geom) {

		setGeometry(geom);

	}

	public void setGeometry(Geometry geom) {

		List coordArrays = CoordinateArrays.toCoordinateArrays(geom, true);

		for (Iterator i = coordArrays.iterator(); i.hasNext();) {
			Coordinate[] coord = (Coordinate[]) i.next();
			for (int j = 0; j < coord.length - 1; j++) {

				add(coord[j], coord[j + 1]);
			}
		}

	}

	public void add(Coordinate p0, Coordinate p1) {

		LineSegment lineseg = new LineSegment(p0, p1);
		lineseg.normalize();

		segmentSet.add(lineseg);
	}

	public Collection<LineSegment> getSegments() {
		return segmentSet;
	}

	public List<LineString> getSegmentAsLineString() {

		return toLineStrings(getSegments());

	}

	public List<LineString> toLineStrings(Collection<LineSegment> segments) {

		GeometryFactory fact = new GeometryFactory();
		List<LineString> lineStringList = new ArrayList<LineString>();
		for (Iterator i = segments.iterator(); i.hasNext();) {
			LineSegment seg = (LineSegment) i.next();
			LineString ls = LineSegmentUtil.asGeometry(fact, seg);
			lineStringList.add(ls);
		}
		return lineStringList;
	}

}
