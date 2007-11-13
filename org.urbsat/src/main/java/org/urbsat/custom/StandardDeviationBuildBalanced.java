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
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.CustomQuery;

import com.vividsolutions.jts.geom.Geometry;
/**
 *@author thebaud
 *On calcule tout d'abord la hauteur moyenne dans la zone �tudi�e puis on fait 
 *la somme des �carts entre la hauteur de chaque batiment et 
 *la hauteur moyenne au carr�, pond�r� par la surface. L'�cart type est ensuite donn� par la 
 *racine de cette somme d'�cart, divis� par le nombre de batiments
 */

public class StandardDeviationBuildBalanced implements CustomQuery {

	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables, Value[] values)
			throws ExecutionException {

		if (tables.length != 3)
			throw new ExecutionException(
					"AverageBuildHeight only operates on three tables");
		if (values.length != 2)
			throw new ExecutionException(
					"AverageBuildHeight only operates with two values");
		
		DataSource resultDs = null;
		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "StandardDeviationBuildVolume" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE) });

			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			SpatialDataSourceDecorator parcels = new SpatialDataSourceDecorator(
					tables[0]);
			SpatialDataSourceDecorator grid = new SpatialDataSourceDecorator(tables[1]);
			SpatialDataSourceDecorator buildHeight = new SpatialDataSourceDecorator(tables[2]);
			String parcelFieldName = values[0].toString();
			String gridFieldName = values[1].toString();
			//String buildVolumeFieldName = values[2].toString();
			grid.open();
			parcels.open();
			buildHeight.open();
			grid.setDefaultGeometry(gridFieldName);

			for (int i = 0; i < grid.getRowCount(); i++) {
				Geometry cell = grid.getGeometry(i);
				int intfield = grid.getFieldIndexByName("index");
				Value t = grid.getFieldValue(i, intfield);
				
				int intfield3 = buildHeight.getFieldIndexByName("AverageBuildHeight");
				Value buildValue = buildHeight.getFieldValue(i, intfield3);
				double avHeight = Double.parseDouble(buildValue.toString());
				IndexQuery query = new SpatialIndexQuery(cell
						.getEnvelopeInternal(), parcelFieldName);
				Iterator<PhysicalDirection> iterator = parcels
						.queryIndex(query);
			
				int number = 0;
				double totaldeviation = 0;
				while (iterator.hasNext()) {
					PhysicalDirection dir = (PhysicalDirection) iterator.next();
					Value geom = dir.getFieldValue(parcels
							.getFieldIndexByName(parcelFieldName));
					Geometry g = ((GeometryValue) geom).getGeom();
					Value height = dir.getFieldValue(parcels.getFieldIndexByName("hauteur"));
					
					double hei = Double.parseDouble(height.toString());
					if (g.intersects(cell)) {
						double deviation = hei-avHeight;
					
						totaldeviation+=Math.abs(deviation)/g.getLength();
					number++;
				
					}
					
				}
				double result = Math.sqrt(totaldeviation);
				resultDs.insertFilledRow(new Value[]{t,
						ValueFactory.createValue(result/number)});
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
		return "STANDARDDEVIATIONBUILDBALANCED";
	}
}