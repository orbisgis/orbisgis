package org.urbsat.function;

import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class MakeGrid implements Function{
private Value result = null;
	
	private int constante = 12;
	
	public Function cloneFunction() {
		
		return new MakeGrid();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String dataname = args[2].toString();
		GeometryValue gv=null;
		try {
			gv = DataSaved.getEnveloppe(dataname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<ArrayList<Geometry>> grille = new ArrayList<ArrayList<Geometry>>();
		Geometry geo = gv.getGeom();
		double lx=geo.getCoordinates()[1].x-geo.getCoordinates()[0].x;
		double ly=geo.getCoordinates()[2].y-geo.getCoordinates()[1].y;
		
		String ar1 =  args[0].toString();
		String ar2 =  args[1].toString();
		int x= Integer.parseInt(ar1);
		int y = Integer.parseInt(ar2);
		
		double pasx=lx/x;
		double pasy=ly/y;
		
		double xdeb=geo.getCoordinates()[0].x;
		double ydeb=geo.getCoordinates()[2].y;
		double xcour=xdeb;
		double ycour=ydeb;
		GeometryFactory fact = new GeometryFactory();
		for (int i=0;i<y;i++) {
			ArrayList<Geometry> line = new ArrayList<Geometry>();
			for (int j=0;j<x;j++) {
				Envelope env = new Envelope(xcour,xcour+pasx,ycour-pasy,ycour);
				xcour=xcour+pasx;
				Geometry grid = fact.toGeometry(env);
				
				line.add(grid);
			}
			xcour=xdeb;
			ycour=ycour-pasy;
			grille.add(line);
			
			
		}
		
		try {
			DataSaved.setGrid(dataname,grille);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ValueFactory.createValue("une grille de "+x*y+" mailles a ete cree");
	}
	
	public String getName() {
		
		return "MakeGrid";
	}

	public int getType(int[] types) {
		return Value.INT;
	}

	public boolean isAggregate() {
		
		return true;
	}

}
