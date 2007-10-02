package org.urbsat.custom;
/***
 * Rapport entre la surface lat�rale cumul�e de l'ensemble des batiments et 
 * la surface totale. Choisir une direction de vent et calculer la distance
 *  entre les deux vecteurs orthogonaux au vecteur du vent qui passent par le 
 *  sommet le plus proche de l�origine du vent pour l�un et le plus �loign� de 
 *  l�origine du vent pour l�autre de chaque parties de b�timent  appartenant 
 *  a la zone �tudi�e et multiplier par la hauteur. Faire le rapport de cette 
 *  valeur par l�aire totale de la zone �tudi�e.
 */

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
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import com.vividsolutions.jts.geom.LinearRing;

public class FrontalDensity implements CustomQuery{
	public DataSource evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values) throws ExecutionException {

		if (tables.length != 2)
			throw new ExecutionException(
					"LateralDensity only operates on two tables");
		if (values.length != 3)
			throw new ExecutionException(
					"LateralDensity only operates with three values");

		DataSource resultDs = null;
		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					new String[] { "index", "LateralDensity" }, new Type[] {
							TypeFactory.createType(Type.INT),
							TypeFactory.createType(Type.DOUBLE) });
			
			resultDs = dsf.getDataSource(driver);
			resultDs.open();
			SpatialDataSourceDecorator parcels = new SpatialDataSourceDecorator(
					tables[0]);
			SpatialDataSourceDecorator grid = new SpatialDataSourceDecorator(
					tables[1]);
			String parcelFieldName = values[0].toString();
			String gridFieldName = values[1].toString();
			String windS = values[2].toString();
			double angle = Double.parseDouble(windS);
			
		/*	if (angle > 360 || angle < 0) {
				angle = angle % 360;
			}

			double nangle = 0;

			nangle = (450 - angle) % 360;
			System.out.println(nangle);

			double enradian = Math.toRadians(nangle);
			double corx = Math.cos(enradian);
			BigDecimal enra = new BigDecimal(corx);
			BigDecimal enra2 = enra.setScale(8, BigDecimal.ROUND_DOWN);
			corx = enra2.doubleValue();

			double sinx = Math.sin(enradian);
			BigDecimal senra = new BigDecimal(sinx);
			BigDecimal senra2 = senra.setScale(8, BigDecimal.ROUND_DOWN);
			sinx = senra2.doubleValue();

			Coordinate c1 = new Coordinate(corx * 10, sinx * 10);
			Coordinate[] mals = new Coordinate[2];
			mals[0] = new Coordinate(0, 0);
			mals[1] = c1;
			GeometryFactory fact = new GeometryFactory();
			LineString ligneangle = fact.createLineString(mals);*/

			
			
			
			grid.open();
			parcels.open();
			grid.setDefaultGeometry(gridFieldName);

			for (int i = 0; i < grid.getRowCount(); i++) {
				Geometry cell = grid.getGeometry(i);
				Value k = grid.getFieldValue(i, 1);
				IndexQuery query = new SpatialIndexQuery(cell
						.getEnvelopeInternal(), parcelFieldName);
				Iterator<PhysicalDirection> iterator = parcels
						.queryIndex(query);
				double lenght = 0;
				int number = 0;
				while (iterator.hasNext()) {
					PhysicalDirection dir = (PhysicalDirection) iterator.next();
					Value geom = dir.getFieldValue(parcels
							.getFieldIndexByName(parcelFieldName));
					Geometry g = ((GeometryValue) geom).getGeom();
					if (g.intersects(cell)) {
						Geometry env = g.getEnvelope();
						Coordinate[] tab = env.getCoordinates();
						double minx = Integer.MAX_VALUE;
						double maxx =0;
						double miny = Integer.MAX_VALUE;
						double maxy =0;
						for (int l=0; l<tab.length;l++) {
							   if (tab[i].x<minx) {
								   minx=tab[i].x;
							   }
							   if (tab[i].y<miny) {
								   miny=tab[i].y;
							   }
							   if (tab[i].x>maxx) {
								   minx=tab[i].y;
							   }
							   if (tab[i].y<maxy) {
								   minx=tab[i].y;
							   }
						}
						GeometryFactory geof = new GeometryFactory();
						Coordinate[] tab1 = {new Coordinate(minx,miny),new Coordinate(minx,maxy)};
					
						LineString lr1 = geof.createLineString(tab1);
						Coordinate[] tab2 = {new Coordinate(minx,miny),new Coordinate(maxx,miny)};
						LineString lr2 = geof.createLineString(tab2);
						double hauteur = 7.0;
						
						if (angle==90 || angle==270) {
							//false height
							
						double res1 = lr1.getLength()*hauteur;
						lenght+=res1;
						System.out.println(lr1);
						}
						if (angle==180 || angle==0) {
						double res2 = lr2.getLength()*hauteur;
						lenght+=res2;
						System.out.println(lr2);
						}
						number++;
						
					
					}
				}
				resultDs.insertFilledRow(new Value[] { k,
						ValueFactory.createValue(lenght/number) });
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
		// call BUILDLENGHT from landcover2000, gdbms1182439943162 values('the_geom', 'the_geom');

	}

	public String getName() {
		return "FRONTALDENSITY";
	}
}
