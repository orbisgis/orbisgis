package org.gdms.sql.strategies;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableFunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

public class GigaCustomQuery extends AbstractTableFunction {

	@Override
	public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
			Value[] values, ProgressMonitor pm) throws FunctionException {
		try {
			Metadata metadata = getMetadata(null);
			DiskBufferDriver dbd = new DiskBufferDriver(dsf, metadata);
			for (int i = 0; i < 1000000; i++) {
				dbd.addValues(new Value[] { ValueFactory
						.createValue("this is row " + i) });
			}
                        dbd.writingFinished();
			dbd.start();
			return dbd.getTable("main");
		} catch (DriverException e) {
			throw new FunctionException("Cannot generate the gigasource", e);
		}
	}

	@Override
	public String getDescription() {
		return "Returns a super big source";
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

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[] {
                        new TableFunctionSignature(TableDefinition.ANY)
                };
        }

}
