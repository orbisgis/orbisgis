package org.gdms.triangulation;


import org.gdms.triangulation.core.TriangulatedIrregularNetwork;

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
