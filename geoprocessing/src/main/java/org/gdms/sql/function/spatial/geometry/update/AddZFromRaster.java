/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function.spatial.geometry.update;

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

public class AddZFromRaster implements Function {
	private GeoRaster dem = null;

	private ImageProcessor demIp = null;


	public Value evaluate(Value[] args) throws FunctionException {
		if ((args[0].isNull()) || (args[1].isNull())) {
			return ValueFactory.createNullValue();
		}

		Geometry geometry = args[0].getAsGeometry();

		try {
			if (null == dem) {
				dem = args[1].getAsRaster();
				dem.open();
				demIp = dem.getImagePlus().getProcessor();
			}

			RasterZFilter zFilter = new RasterZFilter();
			geometry.apply(zFilter);
			if (null != zFilter.exception) {
				throw new FunctionException(zFilter.exception);
			}

			return ValueFactory.createValue(geometry);
		} catch (IOException e) {
			throw new FunctionException(
					"Bug while trying to retrieve the GeoRaster data", e);
		}

	}

	private class RasterZFilter implements CoordinateSequenceFilter {
		private boolean done = false;

		IOException exception = null;

		public void filter(CoordinateSequence seq, int i) {
			double x = seq.getX(i);
			double y = seq.getY(i);
			seq.setOrdinate(i, 0, x);
			seq.setOrdinate(i, 1, y);
			try {
				seq.setOrdinate(i, 2, getGroundZ(x, y));
				if (i == seq.size()) {
					done = true;
				}
			} catch (IOException e) {
				exception = e;
				done = true;
			}
		}

		public boolean isDone() {
			return done;
		}

		public boolean isGeometryChanged() {
			return true;
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
				+ "geometric parameter to the corresponding value given by a raster.";
	}

	public Arguments[] getFunctionArguments() {

		return new Arguments[] { new Arguments(Argument.GEOMETRY,
				Argument.RASTER) };
	}

	public String getName() {
		return "AddZFromRaster";
	}

	public String getSqlOrder() {
		return "select AddZFromRaster(b.the_geom, d.raster) from buildings b, dem d;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.GEOMETRY);
	}

	public boolean isAggregate() {
		return false;
	}

	public Value getAggregateResult() {
		return null;
	}

}