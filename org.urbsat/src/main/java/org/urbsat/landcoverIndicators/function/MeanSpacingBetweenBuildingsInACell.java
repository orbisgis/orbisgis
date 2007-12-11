package org.urbsat.landcoverIndicators.function;

import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;

/*
 * select creategrid(...,...) from build1;
 * => grid
 * select intersection(g.the_geom,b.the_geom),g.index from grid as g, build1 as b where intersects(g.the_geom,b.the_geom);
 * => build2
 * select geomUnion(the_geom),index from build2 group by index; 
 * => build3
 * select MeanSpace(g.the_geom,b.the_geom),g.index from grid as g, build3 as b;
 */

public class MeanSpacingBetweenBuildingsInACell implements Function {
	public Function cloneFunction() {
		return new MeanSpacingBetweenBuildingsInACell();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		final Geometry geomGrid = ((GeometryValue) args[0]).getGeom();
		final Geometry geomBuild = ((GeometryValue) args[1]).getGeom();

		final Geometry noBuildSpace = geomGrid.difference(geomBuild);
		final double s = noBuildSpace.getArea();
		final double p = noBuildSpace.getLength();

		final double result = 0.25 * p - 0.5 * Math.sqrt(0.25 * p * p - 4 * s);
		return ValueFactory.createValue(result);
	}

	public String getDescription() {
		return "Calculate mean spacing between buildings (grid.the_geom, build.the_geom)";
	}

	public String getName() {
		return "MeanSpacing";
	}

	public int getType(int[] paramTypes) {
		return Type.DOUBLE;
	}

	public boolean isAggregate() {
		return false;
	}

	public String getSqlOrder() {
		return "select MeanSpacing(a.the_geom,intersection(a.the_geom,b.the_geom)) from grid as a, build as b where intersects(a.the_geom,b.the_geom);";
		// return "select MeanSpacing(a.the_geom,b.the_geom) from grid as a,
		// build as b where intersects(a.the_geom,b.the_geom);";
	}
}