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
package org.contrib.gdms.triangulation;


import org.contrib.algorithm.triangulation.core.TriangulatedIrregularNetwork;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class TINTest {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	
	public static void main(String[] args) throws ParseException {
	
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(
			    PrecisionModel.FIXED));
		
		
		WKTReader wkReader = new WKTReader();
		Polygon geom = (Polygon) wkReader.read("POLYGON (( 106 82, 100 311, 205 225, 337 319, 355 91, 106 82 ))");		
		
			 
		TriangulatedIrregularNetwork tin = new TriangulatedIrregularNetwork(factory, geom.buffer(20).getEnvelopeInternal());
				 
		for (int i = 0; i < geom.getCoordinates().length; i++) {
			Coordinate c = geom.getCoordinates()[i];
			tin.insertNode(c);
			
		}
		tin.buildIndex();
		
	    
		System.out.println(tin.getTriangles());
		

	}

}
