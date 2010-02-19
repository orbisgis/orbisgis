package org.gdms.sql.strategies;

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
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.orbisgis.progress.IProgressMonitor;

public class GigaCustomQuery implements CustomQuery {

	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		try {
			Metadata metadata = getMetadata(null);
			DiskBufferDriver dbd = new DiskBufferDriver(dsf, metadata);
			for (int i = 0; i < 1000000; i++) {
				dbd.addValues(new Value[] { ValueFactory
						.createValue("this is row " + i) });
			}
			dbd.writingFinished();
			return dbd;
		} catch (DriverException e) {
			throw new ExecutionException("Cannot generate the gigasource", e);
		}
	}

	@Override
	public TableDefinition[] geTablesDefinitions() {
		return new TableDefinition[0];
	}

	@Override
	public String getDescription() {
		return "Returns a super big source";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		DefaultMetadata metadata = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.STRING) }, new String[] { "string" });
		return metadata;
	}

	@Override
	public String getName() {
		return "gigaquery";
	}

	@Override
	public String getSqlOrder() {
		return "select gigaquery()";
	}

}
