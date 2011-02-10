package org.gdms.sql.customQuery.system;

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
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.TableDefinition;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.progress.IProgressMonitor;

public class FunctionHelp implements CustomQuery {

	@Override
	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {

		String[] queries = QueryManager.getQueryNames();
		String[] functions = FunctionManager.getFunctionNames();

		try {
			DefaultMetadata defaultMetadata = new DefaultMetadata();
			defaultMetadata.addField("name", TypeFactory
					.createType(Type.STRING));
			defaultMetadata.addField("sqlorder", TypeFactory
					.createType(Type.STRING));
			defaultMetadata.addField("description", TypeFactory
					.createType(Type.STRING));
			defaultMetadata.addField("type", TypeFactory
					.createType(Type.STRING));

			GenericObjectDriver genericObjectDriver = new GenericObjectDriver(
					defaultMetadata);
			for (String function : functions) {

				Function fct = FunctionManager.getFunction(function);
				genericObjectDriver.addValues(ValueFactory.createValue(fct
						.getName()), ValueFactory
						.createValue(fct.getSqlOrder()), ValueFactory
						.createValue(fct.getDescription()), ValueFactory
						.createValue("function"));
			}

			for (String query : queries) {

				CustomQuery q = QueryManager.getQuery(query);
				genericObjectDriver.addValues(ValueFactory.createValue(q
						.getName()), ValueFactory.createValue(q.getSqlOrder()),
						ValueFactory.createValue(q.getDescription()),
						ValueFactory.createValue("custom query"));

			}

			return genericObjectDriver;
		} catch (DriverException e) {
			throw new ExecutionException(e);
		}

	}

	@Override
	public TableDefinition[] getTablesDefinitions() {
		return new TableDefinition[0];
	}

	@Override
	public String getDescription() {
		return "Create a table with all functions";
	}

	@Override
	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments() };
	}

	@Override
	public Metadata getMetadata(Metadata[] tables) throws DriverException {

		return null;
	}

	@Override
	public String getName() {
		return "FunctionHelp";
	}

	@Override
	public String getSqlOrder() {
		return "SELECT FunctionHelp()";
	}

}
