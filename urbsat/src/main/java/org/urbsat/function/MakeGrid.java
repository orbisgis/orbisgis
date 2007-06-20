package org.urbsat.function;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SyntaxException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class MakeGrid implements Function {

	public Function cloneFunction() {

		return new MakeGrid();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String dataName = args[2].toString();
		GeometryValue gv = null;
		try {
			gv = DataSaved.getEnveloppe(dataName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<List<Geometry>> grille = new ArrayList<List<Geometry>>();
		Geometry geo = gv.getGeom();
		double lx = geo.getCoordinates()[1].x - geo.getCoordinates()[0].x;
		double ly = geo.getCoordinates()[2].y - geo.getCoordinates()[1].y;

		String ar1 = args[0].toString();
		String ar2 = args[1].toString();
		int x = Integer.parseInt(ar1);
		int y = Integer.parseInt(ar2);

		double pasx = lx / x;
		double pasy = ly / y;

		double xdeb = geo.getCoordinates()[0].x;
		double ydeb = geo.getCoordinates()[2].y;
		double xcour = xdeb;
		double ycour = ydeb;
		GeometryFactory fact = new GeometryFactory();
		for (int i = 0; i < y; i++) {
			List<Geometry> line = new ArrayList<Geometry>();
			for (int j = 0; j < x; j++) {
				Envelope env = new Envelope(xcour, xcour + pasx, ycour - pasy,
						ycour);
				xcour = xcour + pasx;
				Geometry grid = fact.toGeometry(env);

				line.add(grid);
			}
			xcour = xdeb;
			ycour = ycour - pasy;
			grille.add(line);

		}

		DataSaved.setGrid(dataName, grille);
		DataSourceFactory dsf = DataSaved.getDatasource(dataName);
		DataSource d1 = null;
		try {
			d1 = dsf.getDataSource(dataName);
		} catch (DriverLoadException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchTableException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DataSourceCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			d1.open();
		} catch (DriverException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		System.out.println(dsf.getTempFile());
		System.out.println("jjjihuhu");
		try {
			DataSaved.registerGrid(dataName, grille);
			} catch (SyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 catch (DriverLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FreeingResourcesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			DataSaved.SaveGrid(dataName, grille);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ValueFactory.createValue("une grille de " + x * y
				+ " mailles a ete cree");
	}

	public String getName() {

		return "MakeGrid";
	}

	public int getType(int[] types) {
		return Type.INT;
	}

	public boolean isAggregate() {

		return true;
	}

}