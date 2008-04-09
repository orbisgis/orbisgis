package org.gdms.sql.strategies;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.customQuery.CustomQuery;
import org.orbisgis.IProgressMonitor;

public class FieldReferenceQuery implements CustomQuery {

	private Type[] validateTypes;
	private Value[] evaluateValues;

	public ObjectDriver evaluate(DataSourceFactory dsf, DataSource[] tables,
			Value[] values, IProgressMonitor pm) throws ExecutionException {
		evaluateValues = values;
		return new ObjectMemoryDriver();
	}

	public String getDescription() {
		return null;
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return null;
	}

	public String getName() {
		return "fieldReferenceQuery";
	}

	public String getSqlOrder() {
		return null;
	}

	public void validateTables(Metadata[] tables) throws SemanticException,
			DriverException {
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		validateTypes = types;
	}

	public Type[] getValidateTypes() {
		return validateTypes;
	}

	public Value[] getEvaluateValues() {
		return evaluateValues;
	}

}
