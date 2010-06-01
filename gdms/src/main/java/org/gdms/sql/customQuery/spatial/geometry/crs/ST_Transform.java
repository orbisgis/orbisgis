package org.gdms.sql.customQuery.spatial.geometry.crs;

import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.crs.CRSUtil;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;

import fr.cts.CoordinateOperation;
import fr.cts.Identifier;
import fr.cts.IllegalCoordinateException;
import fr.cts.crs.GeodeticCRS;
import fr.cts.op.CoordinateOperationFactory;
import fr.cts.op.CoordinateOperationSequence;

public class ST_Transform implements CustomQuery {

	GeodeticCRS targetCRS;

	GeodeticCRS sourceCRS;

	private CoordinateOperationSequence cos;

	private Geometry geom;

	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {

		String name = values[1].toString();

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				tables[0]);

		targetCRS = (GeodeticCRS) CRSUtil.getCRSFromEPSG(name);
		try {
			sds.open();
			sourceCRS = (GeodeticCRS) sds.getCRS();
			List<CoordinateOperation> ops = CoordinateOperationFactory
					.createCoordinateOperations(sourceCRS, targetCRS);
			cos = new CoordinateOperationSequence(new Identifier(
					ST_Transform.class, "" + " to " + ""), ops);

			GenericObjectDriver driver = new GenericObjectDriver(sds
					.getMetadata());

			GeometryTransformer gt = new GeometryTransformer() {
	            protected CoordinateSequence transformCoordinates(CoordinateSequence cs, Geometry geom){
	                Coordinate[] cc = geom.getCoordinates();
	                CoordinateSequence newcs = new CoordinateArraySequence(cc);
	                for (int i = 0 ; i < cc.length ; i++) {
	                    Coordinate c = cc[i];
	                    try {
	                    	//if(cc.length==)
	                        double[] xyz = cos.transform(new double[]{c.x, c.y, c.z});
	                        newcs.setOrdinate(i,0,xyz[0]);
	                        newcs.setOrdinate(i,1,xyz[1]);
	                        if(xyz.length > 2)
	                        	newcs.setOrdinate(i,2,xyz[2]);
	                        else
	                        	newcs.setOrdinate(i,2,Double.NaN);
	                    } catch(IllegalCoordinateException ice) {ice.printStackTrace();}
	                }
	                return newcs;
	            }
	        };

			int geomIndex = sds.getSpatialFieldIndex();
			for (int i = 0; i < sds.getRowCount(); i++) {
				values = sds.getRow(i);
				geom = sds.getGeometry(i);
				values[geomIndex] = ValueFactory
						.createValue(gt.transform(geom));
				driver.addValues(values);
			}

			sds.close();

			return driver;
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[] { TableDefinition.GEOMETRY };
	}

	@Override
	public String getDescription() {

		return null;
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.STRING) };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {

		return null;
	}

	@Override
	public String getName() {
		return "ST_TRANSFORM";
	}

	@Override
	public String getSqlOrder() {
		// TODO Auto-generated method stub
		return null;
	}

}
