package org.gdms.sql.customQuery.spatial.geometry.tin;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;
import org.gdms.triangulation.sweepLine4CDT.CDTTriangle;
import org.gdms.triangulation.sweepLine4CDT.PSLG;
import org.orbisgis.progress.IProgressMonitor;

public class Cdt implements CustomQuery {

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		SpatialDataSourceDecorator inSds = new SpatialDataSourceDecorator(
				tables[0]);
		try {
			// populate and mesh the Planar Straight-Line Graph using the unique
			// table as input data
			PSLG pslg = new PSLG(inSds);
			pslg.mesh(pm);

			// convert the resulting TIN into a data source
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));
			long index = 0;
			for (CDTTriangle cdtTriangle : pslg.getTriangles()) {
				driver.addValues(new Value[] {
						ValueFactory.createValue(index++),
						ValueFactory.createValue(cdtTriangle.getPolygon()) });
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "This custom query is an implementation of the Seep-line algorithm for"
				+ "Constrained Delaunay Triangulation (V Domiter and B Zalik, IJGIS, 2008)";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		try {
			return new DefaultMetadata(new Type[] {
					TypeFactory.createType(Type.INT),
					TypeFactory.createType(Type.GEOMETRY,
							new Constraint[] { new GeometryConstraint(
									GeometryConstraint.POLYGON) }) },
					new String[] { "gid", "the_geom" });
		} catch (InvalidTypeException e) {
			throw new DriverException(
					"InvalidTypeException in metadata instantiation", e);
		}
	}

	public String getName() {
		return "Cdt";
	}

	public String getSqlOrder() {
		return "select cdt() from mydatasource";
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 1);
		FunctionValidator.failIfNotSpatialDataSource(this, tables[0], 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 0);
	}
}