package org.gdms.utilities;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
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
import org.orbisgis.IProgressMonitor;

public class RandomGeometry implements CustomQuery {
	// private static final RandomGeometryUtilities rgu = new
	// RandomGeometryUtilities();
	private static final RandomGeometryUtilities rgu = new RandomGeometryUtilities();

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		final String choice = values[0].getAsString();
		final int numberOfItems = (1 == values.length) ? 1 : values[1]
				.getAsInt();

		try {
			final ObjectMemoryDriver driver = new ObjectMemoryDriver(
					getMetadata(null));

			if (choice.equalsIgnoreCase("point")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextPoint()) });
				}
			} else if (choice.equalsIgnoreCase("linestring")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextLineString()) });
				}
			} else if (choice.equalsIgnoreCase("linearring")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextLinearRing()) });
				}
			} else if (choice.equalsIgnoreCase("polygon")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextPolygon()) });
					// .nextNoHolePolygon()) });
				}
			} else if (choice.equalsIgnoreCase("misc")) {
				for (int i = 0; i < numberOfItems; i++) {
					driver.addValues(new Value[] { ValueFactory.createValue(i),
							ValueFactory.createValue(rgu.nextGeometry()) });
				}
			} else {
				throw new ExecutionException(
						"Given type must be misc, point, linestring, linearring or polygon !");
			}
			return driver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}
	}

	public String getDescription() {
		return "Returns randomly choosen geometries of given type";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] {
				TypeFactory.createType(Type.INT),
				TypeFactory.createType(Type.GEOMETRY) }, new String[] { "gid",
				"the_geom" });
	}

	public String getName() {
		return "RandomGeometry";
	}

	public String getSqlOrder() {
		return "select RandomGeometry('misc|point|linestring|linearring|polygon'[, number]);";
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
		FunctionValidator.failIfBadNumberOfTables(this, tables, 0);
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 1, 2);
		FunctionValidator.failIfNotOfType(this, types[0], Type.STRING);
		if (2 == types.length) {
			FunctionValidator.failIfNotOfType(this, types[1], Type.INT);
		}
	}
}