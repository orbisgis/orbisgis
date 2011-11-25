package org.gdms.sql.customQuery.system;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.progress.ProgressMonitor;

public class RegisterFunction implements CustomQuery {

	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, ProgressMonitor pm) throws ExecutionException {
		String className = values[0].getAsString();

		try {
			Class<?> javaClass = Class.forName(className);

			Class<?>[] interfaceName = javaClass.getInterfaces();

			boolean isRegistred = false;
			for (int i = 0; i < interfaceName.length; i++) {
				String inputClassName = interfaceName[i].getName();
				if (inputClassName.equals(CustomQuery.class.getName())) {
					QueryManager
							.registerQuery((Class<? extends CustomQuery>) javaClass);
					isRegistred = true;
				} else if (inputClassName.equals(Function.class.getName())) {
					FunctionManager
							.addFunction((Class<? extends Function>) javaClass);
					isRegistred = true;
				}
			}
			if (!isRegistred)
				throw new ExecutionException("It's not a gdms SQL function");

		} catch (ClassNotFoundException e) {
			throw new ExecutionException("Class not found" + e);
		}
		return null;
	}

	@Override
	public TableDefinition[] getTablesDefinitions() {
		return new TableDefinition[] {};
	}

	@Override
	public String getDescription() {

		return "A simple way to register a function.";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.STRING) };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {

		return null;
	}

	@Override
	public String getName() {
		return "FunctionRegister";
	}

	@Override
	public String getSqlOrder() {
		return "SELECT FunctionRegister('org.gdms.myFunction')";
	}

}
