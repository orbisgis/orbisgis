package org.urbsat.landcoverIndicators.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.function.WarningException;

import com.vividsolutions.jts.geom.Geometry;

/*
 * select explode(the_geom) from build where type='bati';
 * => build1
 * select compacity(the_geom) from build1;
 */
public class Compacity implements Function {

	public Function cloneFunction() {
		return new Compacity();
	}

	public Value evaluate(Value[] args) throws FunctionException,
			WarningException {
		FunctionValidator.failIfBadNumberOfArguments(this, args, 1);
		FunctionValidator.warnIfNull(args[0]);
		FunctionValidator.warnIfNotOfType(args[0], Type.GEOMETRY);
		FunctionValidator.warnIfGeometryNotValid(args[0]);

		final Geometry geomBuild = ((GeometryValue) args[0]).getGeom();
		final double sBuild = geomBuild.getArea();
		final double pBuild = geomBuild.getLength();
		// final double ratioBuild = sBuild / pBuild;

		final double correspondingCircleRadius = Math.sqrt(sBuild / Math.PI);
		// final double sCircle = sBuild;
		final double pCircle = 2 * Math.PI * correspondingCircleRadius;
		// final double ratioCircle = sCircle / pCircle;

		// return ValueFactory.createValue(ratioCircle / ratioBuild);
		return ValueFactory.createValue(pBuild / pCircle);
	}

	public String getDescription() {
		return "Calculate the compacity of each building's geometry : select Compacity(the_geom) from buildings";
	}

	public String getName() {
		return "Compacity";
	}

	public int getType(int[] paramTypes) {
		return Type.DOUBLE;
	}

	public boolean isAggregate() {
		return false;
	}
}