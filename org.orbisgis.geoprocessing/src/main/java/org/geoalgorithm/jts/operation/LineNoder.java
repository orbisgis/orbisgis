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

package org.geoalgorithm.jts.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.util.LinearComponentExtracter;

public class LineNoder {
	private SpatialDataSourceDecorator sds;
	private GeometryFactory geometryFactory = new GeometryFactory();

	public LineNoder(final SpatialDataSourceDecorator sds) {
		this.sds = sds;
	}

	public Collection getLines() throws DriverException {
		List linesList = new ArrayList();
		LinearComponentExtracter lineFilter = new LinearComponentExtracter(
				linesList);
		for (int i = 0; i < sds.getRowCount(); i++) {

			Geometry g = sds.getGeometry(i);
			g.apply(lineFilter);
		}
		return linesList;
	}

	/**
	 * Nodes a collection of linestrings. Noding is done via JTS union, which is
	 * reasonably effective but may exhibit robustness failures.
	 * 
	 * @param lines
	 *            the linear geometries to node
	 * @return a collection of linear geometries, noded together
	 */
	public Geometry getNodeLines(Collection lines) {
		Geometry linesGeom = geometryFactory.createMultiLineString(geometryFactory
				.toLineStringArray(lines));

		Geometry unionInput = geometryFactory.createMultiLineString(null);
		// force the unionInput to be non-empty if possible, to ensure union is
		// not optimized away
		Geometry minLine = extractPoint(lines);
		if (minLine != null)
			unionInput = minLine;

		Geometry noded = linesGeom.union(unionInput);
		return noded;
	}

	public static List toLines(Geometry geom) {
		List linesList = new ArrayList();
		LinearComponentExtracter lineFilter = new LinearComponentExtracter(
				linesList);
		geom.apply(lineFilter);
		return linesList;
	}

	private Geometry extractPoint(Collection lines) {
		int minPts = Integer.MAX_VALUE;
		Geometry point = null;
		// extract first point from first non-empty geometry
		for (Iterator i = lines.iterator(); i.hasNext();) {
			Geometry g = (Geometry) i.next();
			if (!g.isEmpty()) {
				Coordinate p = g.getCoordinate();
				point = g.getFactory().createPoint(p);
			}
		}
		return point;
	}

}
