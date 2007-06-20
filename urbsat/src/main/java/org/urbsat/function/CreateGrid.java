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
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.DefaultType;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class CreateGrid implements Function {
	static int gasp =0;
	public Function cloneFunction() {

		return new CreateGrid();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String ar1 = args[0].toString();
		String ar2 = args[1].toString();
		int x = Integer.parseInt(ar1);
		int y = Integer.parseInt(ar2);
		
		if (gasp == 0) {
		GeometryValue gv = null;
		try {
		DataSource ds1 =Appli2.dsf.executeSQL("select * from enveloppe");
		ds1.open();
		gv = (GeometryValue) ds1.getFieldValue(0,0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		List<List<Geometry>> theGrid = new ArrayList<List<Geometry>>();
		Geometry geo = gv.getGeom();
		double lx = geo.getCoordinates()[1].x - geo.getCoordinates()[0].x;
		double ly = geo.getCoordinates()[2].y - geo.getCoordinates()[1].y;

		

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
			theGrid.add(line);

		}

		ObjectMemoryDriver omd = new ObjectMemoryDriver(new String[] { "index",
				"index_X","index_Y","geom" }, new Type[] {
				new DefaultType(null, "INDEX", Type.INT),
				new DefaultType(null, "X", Type.INT),
				new DefaultType(null, "Y", Type.INT),
				new DefaultType(null, "GEOM", Type.INT)
				});
				DataSourceFactory dsf = Appli2.dsf;
				
				dsf.registerDataSource("grid", new ObjectSourceDefinition(omd));
				try {
				DataSource ds = dsf.getDataSource("grid");
				ds.open();
				int i=0;
				int j=0;
				
				while (i<theGrid.size()) {
					
					while(j<theGrid.get(i).size()) {
					ds.insertEmptyRow();
					System.out.println("hu");
					ds.setFieldValue((i*theGrid.get(i).size()+j), 0, ValueFactory.createValue((i*theGrid.get(i).size()+j)));
					ds.setFieldValue((i*theGrid.get(i).size()+j), 1, ValueFactory.createValue(j));
					ds.setFieldValue((i*theGrid.get(i).size()+j), 2, ValueFactory.createValue(i));
					ds.setFieldValue((i*theGrid.get(i).size()+j), 3, ValueFactory.createValue(theGrid.get(i).get(j)));
					j++;
					}
					i++;
					j=0;
				}
				
				
				ds.commit();
				ds.open();
				System.out.println(ds.getAsString());
				ObjectMemoryDriver omd2 = new ObjectMemoryDriver(ds);
				Appli2.dsf.registerDataSource("grid", new ObjectSourceDefinition(omd2));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
		gasp++;
		}
		return ValueFactory.createValue("une grille de " + x * y
				+ " mailles a ete cree");
	}

	public String getName() {

		return "CreateGrid";
	}

	public int getType(int[] types) {
		return Type.INT;
	}

	public boolean isAggregate() {

		return true;
	}

}