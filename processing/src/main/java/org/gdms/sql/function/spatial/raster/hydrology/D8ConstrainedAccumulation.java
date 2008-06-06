package org.gdms.sql.function.spatial.raster.hydrology;

import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.hydrology.D8OpConstrainedAccumulation;
import org.grap.processing.operation.hydrology.D8OpStrahlerStreamOrder;

public class D8ConstrainedAccumulation implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		GeoRaster grD8Direction = args[0].getAsRaster();
		GeoRaster grConstrained = args[1].getAsRaster();

		try {
			final Operation opConstrainedAccumulation = new D8OpConstrainedAccumulation(
					grConstrained);
			return ValueFactory.createValue(grD8Direction
					.doOperation(opConstrainedAccumulation));
		} catch (OperationException e) {
			throw new FunctionException("Cannot do the operation", e);
		}
	}

	//TODO To be complete
	public String getDescription() {
		return "This function compute a constrained grid accumulation based on two grid : a grid direction and a integer grid that represents" +
				"some human constaints as hedgerow or roads.";
	}

	public Metadata getMetadata(Metadata[] tables) throws DriverException {
		return new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
	}

	public String getName() {
		return "D8ConstrainedAccumulation";
	}

	public String getSqlOrder() {
		return "select D8ConstrainedAccumulation(d.raster, a.raster) from directions d, constrainedgrid a;";
	}

	public void validateTypes(Type[] types) throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, types, 2);
		FunctionValidator.failIfNotOfType(this, types[0], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, types[1], Type.RASTER);
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}
}