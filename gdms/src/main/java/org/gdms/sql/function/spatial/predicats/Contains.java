package org.gdms.sql.function.spatial.predicats;

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.SpatialIndexQuery;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.ComplexFunction;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.ParamRelationship;

public class Contains implements ComplexFunction {

	public Function cloneFunction() {
		return new Contains();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		GeometryValue gv1 = (GeometryValue) args[1];
		boolean contains = gv.getGeom().contains(gv1.getGeom());

		return ValueFactory.createValue(contains);
	}

	public String getName() {
		return "Contains";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

	public Iterator<PhysicalDirection> filter(Value[] args,
			String[] fieldNames, DataSource tableToFilter,
			ArrayList<Integer> argsFromTableToIndex) throws DriverException {
		int argFromTableToIndex = argsFromTableToIndex.get(0);
		int knownValue = (argFromTableToIndex + 1) % 2;
		GeometryValue value = (GeometryValue) args[knownValue];
		SpatialIndexQuery query = new SpatialIndexQuery(value.getGeom()
				.getEnvelopeInternal(), fieldNames[argFromTableToIndex]);
		return tableToFilter.queryIndex(query);
	}

	public ParamRelationship getRelations() {
		// TODO Remove from the interface
		return null;
	}
}
