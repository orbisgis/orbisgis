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
 * JUMP is Copyright (C) 2003 Vivid Solutions
 *
 * This program implements extensions to JUMP and is
 * Copyright (C) Stefan Steiniger.
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
 * Stefan Steiniger
 * perriger@gmx.de
 */
/*****************************************************
 * created:  		30.05.2005
 * last modified:  	31.05.2005
 * 					
 * 
 * @author sstein
 * 
 * description:
 * 		simplifies a selected line, criterion is a maximal line displacement <p>
 * 		It is used the JTS 1.5 douglas peucker simplification with topology 
 * 		preservation for polygons and DouglasPeuckerSimplifier for linestrings.<p>
 * 		n.b.: the jts-algorithm handles all geometry types
 *   
 *****************************************************/

package org.geoalgorithm.jts.operation;

import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

public class DouglasPeuckerGeneralization {

	private Geometry geometry;
	private double tolerance = 0;

	/**
	 * @description: simplifies a selected line, criterion is a maximal line
	 *               displacement
	 *               <p>
	 *               it is used the JTS 1.9 douglas peucker simplification with
	 *               topology preservation for polygons and
	 *               DouglasPeuckerSimplifier for linestrings n.b.: the
	 *               jts-algorithm handles all geometry types
	 * 
	 * @author sstein , modified by Erwan
	 * 
	 */

	public DouglasPeuckerGeneralization(Geometry geometry, double tolerance) {
		this.geometry = geometry;
		this.tolerance = tolerance;
	}

	public Geometry reducePoints() throws FunctionException {
		Geometry resultgeom = null;

		/** ************************************* */
		if (geometry.getDimension() > 0) {
			// -- update geometry --------
			if (geometry.getDimension() == 1) { // linestring
				resultgeom = DouglasPeuckerSimplifier.simplify(geometry, Math
						.abs(tolerance));
			} else if (geometry.getDimension() == 2) { // polygon
				// poly = (Polygon)geom.clone();
				resultgeom = TopologyPreservingSimplifier.simplify(geometry,
						Math.abs(tolerance));
			}
		}

		else {
			throw new FunctionException("Not a line or a polygon");
		}

		return resultgeom;

	}
}
