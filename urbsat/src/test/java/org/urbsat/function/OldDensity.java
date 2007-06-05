package org.urbsat.function;

import org.gdms.data.values.Value;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
/**
 * calculation of the proportion of an element for one cell of the grid
 * @author thebaud
 * 
 */


public class OldDensity {
	private static double airebuild = 0;
	private static double result =0;
	
/**
 * 
 * @param args array of Value wich contains the abscissa, 
 * @param type
 * @return
 */
	public static double tari (Value[] args) {
	
		String ts = args[3].toString();
		int x = Integer.parseInt(args[0].toString());
		int y = Integer.parseInt(args[1].toString());
		String tog = args[2].toString(); 
		String type= args[4].toString();
		Geometry geom = null;
		try {
			geom = new WKTReader().read(tog);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Geometry maillon = TestAppli.getMaillon(x, y);
		double airemaillon=maillon.getArea();
		if (ts.equals(type)) {
			if (geom.intersects(maillon)) {
				Geometry enco =geom.intersection(maillon); 
				double are =enco.getArea();
				airebuild+=are;
				result=airebuild/airemaillon;
			}
		}
		return result;
	}
	public static void clear() {
		airebuild=0;
		result=0;
	}
}
