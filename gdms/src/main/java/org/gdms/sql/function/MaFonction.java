package org.gdms.sql.function;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

public class MaFonction implements Function {

	@Override
	public Value evaluate(Value... args) throws FunctionException {

		Geometry geom = args[0].getAsGeometry();		
		

		return ValueFactory.createValue(geom.getNumPoints());
	}

	@Override
	public Value getAggregateResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return "Elle fait de l'hydrologie";
	}

	@Override
	public Arguments[] getFunctionArguments() {

		return new Arguments[] { new Arguments(Argument.GEOMETRY) };
	}

	public String getName() {
		return "TheKatiaFunction";
	}

	@Override
	public String getSqlOrder() {
		return "select TheKatiaFunction() from matable;";
	}

	@Override
	public Type getType(Type[] argsTypes) throws InvalidTypeException {

		return TypeFactory.createType(Type.INT);
	}

	@Override
	public boolean isAggregate() {
		return false;
	}

}
