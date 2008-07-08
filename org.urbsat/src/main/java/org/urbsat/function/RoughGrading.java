package org.urbsat.function;

import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;

public class RoughGrading implements Function {
	private GeoRaster dem = null;
	private ImageProcessor demIp = null;

	public Value evaluate(Value[] args) throws FunctionException {
		if ((args[0].isNull()) || (args[1].isNull())) {
			return ValueFactory.createNullValue();
		}

		Geometry geometry = args[0].getAsGeometry();
		try {
			if (null == dem) {
				// TODO this function is only able to deal with a single raster
				// (see FeatureRequest #4307)
				dem = args[1].getAsRaster();
				dem.open();
				demIp = dem.getImagePlus().getProcessor();
			}

			// To modify the coordinates of the geometry, we need a
			// CoordinateSequenceFilter. To have an explanation, see
			// http://lists.refractions.net/pipermail/jts-devel/2008-June/002534.html
			//
			// Coordinate[] coordinates = geometry.getCoordinates();
			// for (Coordinate coordinate : coordinates) {
			// coordinate.z = getGroundZ(coordinate.x, coordinate.y);
			// }

			geometry.apply(new CoordinateSequenceFilter() {
				private boolean done = false;

				public void filter(CoordinateSequence seq, int i) {
					double x = seq.getX(i);
					double y = seq.getY(i);
					seq.setOrdinate(i, 0, x);
					seq.setOrdinate(i, 1, y);
					try {
						seq.setOrdinate(i, 2, getGroundZ(x, y));
						if (i + 1 == seq.size()) {
							done = true;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				public boolean isDone() {
					return done;
				}

				public boolean isGeometryChanged() {
					return true;
				}
			});

			return ValueFactory.createValue(geometry);
		} catch (IOException e) {
			throw new FunctionException(
					"Bug while trying to retrieve the GeoRaster data", e);
		}
	}

	private double getGroundZ(final double x, final double y)
			throws IOException {
		final Point2D pixelPoint = dem.fromRealWorldToPixel(x, y);
		return demIp.getPixelValue((int) pixelPoint.getX(), (int) pixelPoint
				.getY());
	}

	public String getDescription() {
		return "This function modify (or set) the z component of (each vertex of) the "
				+ "geometric parameter to the corresponding value given by the DEM "
				+ "elevation parameter.";
	}

	public Arguments[] getFunctionArguments() {
		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.RASTER) };
	}

	public String getName() {
		return "RoughGrading";
	}

	public String getSqlOrder() {
		return "select RoughGrading(b.the_geom, d.raster) from buildings b, dem d;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	public boolean isAggregate() {
		return false;
	}
}