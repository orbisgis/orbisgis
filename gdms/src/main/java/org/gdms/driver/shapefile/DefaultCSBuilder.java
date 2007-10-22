/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2004-2006, GeoTools Project Managment Committee (PMC)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 * Created on 31-dic-2004
 */
package org.gdms.driver.shapefile;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;

/**
 * CSBuilder that generates DefaultCoordinateSequence objects, that is,
 * coordinate sequences backed by Coordinate[]
 * @author wolf
 * @source $URL: http://svn.geotools.org/geotools/tags/2.3.1/module/main/src/org/geotools/geometry/coordinatesequence/DefaultCSBuilder.java $
 */
public class DefaultCSBuilder {

	private Coordinate[] coordinateArray;

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#start(int, int)
	 */
	public void start(int size, int dimensions) {
		coordinateArray = new Coordinate[size];
		for(int i = 0; i < size; i++)
			coordinateArray[i] = new Coordinate();
	}

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#getCoordinateSequence()
	 */
	public CoordinateSequence end() {
		CoordinateSequence cs = new LiteCoordinateSequence(coordinateArray);
		coordinateArray = null;
		return cs;
	}

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#setOrdinate(double, int, int)
	 */
	public void setOrdinate(double value, int ordinateIndex, int coordinateIndex) {
		Coordinate c = coordinateArray[coordinateIndex];
		switch(ordinateIndex) {
			case 0: c.x = value; break;
			case 1: c.y = value; break;
			case 2: c.z = value; break;
		}
	}

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#getOrdinate(int, int)
	 */
	public double getOrdinate(int ordinateIndex, int coordinateIndex) {
		Coordinate c = coordinateArray[coordinateIndex];
		switch(ordinateIndex) {
			case 0: return c.x;
			case 1: return c.y;
			case 2: return c.z;
			default: return 0.0;
		}
	}

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#getSize()
	 */
	public int getSize() {
		if(coordinateArray != null) {
			return coordinateArray.length;
		} else {
			return -1;
		}
	}

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#getDimension()
	 */
	public int getDimension() {
		if(coordinateArray != null) {
			return 2;
		} else {
			return -1;
		}
	}

	/**
	 * @see org.geotools.geometry.coordinatesequence.CSBuilder#setOrdinate(com.vividsolutions.jts.geom.CoordinateSequence, double, int, int)
	 */
	public void setOrdinate(CoordinateSequence sequence, double value, int ordinateIndex, int coordinateIndex) {
		Coordinate c = sequence.getCoordinate(coordinateIndex);
		switch(ordinateIndex) {
			case 0: c.x = value; break;
			case 1: c.y = value; break;
			case 2: c.z = value; break;
		}

	}

}
