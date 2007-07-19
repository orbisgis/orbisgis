package org.urbsat.custom;

import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.CustomQuery;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
/**
 * return the average height of the build witch intersect the grid for each cell.
 * @author thebaud
 *
 */

public class BalancedBuildVolume implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values)
			throws ExecutionException {

		if (tables.length != 2)
			throw new ExecutionException(
					"AverageBuildHeight only operates on two tables");
		if (values.length != 2)
			throw new ExecutionException(
					"AverageBuildHeight only operates with two values");
		
		DataSource resultDs = null;
		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "BlancedBuildVolume" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE) });

			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			SpatialDataSourceDecorator parcels = new SpatialDataSourceDecorator(
					tables[0]);
			SpatialDataSourceDecorator grid = new SpatialDataSourceDecorator(tables[1]);
			String parcelFieldName = values[0].toString();
			String gridFieldName = values[1].toString();
			grid.open();
			parcels.open();
			grid.setDefaultGeometry(gridFieldName);

			for (int i = 0; i < grid.getRowCount(); i++) {
				Geometry cell = grid.getGeometry(i);
				int intfield = grid.getFieldIndexByName("index");
				Value t = grid.getFieldValue(i, intfield);
			
				
				IndexQuery query = new SpatialIndexQuery(cell
						.getEnvelopeInternal(), parcelFieldName);
				Iterator<PhysicalDirection> iterator = parcels
						.queryIndex(query);
				double totalVolume = 0;
				int number = 0;
				while (iterator.hasNext()) {
					PhysicalDirection dir = (PhysicalDirection) iterator.next();
					Value geom = dir.getFieldValue(parcels
							.getFieldIndexByName(parcelFieldName));
					Geometry g = ((GeometryValue) geom).getGeom();
					Value height = dir.getFieldValue(parcels.getFieldIndexByName("hauteur"));
					System.out.println(height);
					int hei = Integer.parseInt(height.toString());
					if (g.intersects(cell)) {
						double lenght =cell.getLength();
						double area = cell.getArea();
						totalVolume+=(hei*lenght)/area;
					number++;
					}
				}
				resultDs.insertFilledRow(new Value[]{t,
						ValueFactory.createValue(totalVolume/number)});
			}

			resultDs.commit();
			grid.cancel();
			parcels.cancel();
		} catch (DriverException e) {
			e.printStackTrace();
		} catch (InvalidTypeException e) {
			e.printStackTrace();
		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (DataSourceCreationException e) {
			e.printStackTrace();
		} catch (FreeingResourcesException e) {
			e.printStackTrace();
		} catch (NonEditableDataSourceException e) {
			e.printStackTrace();
		}
		return resultDs;
		// call AVERAGEBUILDHEIGHT from landcover2000, gdbms1182439943162 values ('the_geom', 'the_geom');

	}

	public String getName() {
		return "BALANCEDBUILDVOLUME";
	}
}