package org.gdms.sql.function.spatial.geometry.crs;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.crs.SpatialReferenceSystem;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Argument;
import org.gdms.sql.function.Arguments;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.spatial.geometry.AbstractSpatialFunction;

import com.vividsolutions.jts.geom.Geometry;

public class ST_Transform extends AbstractSpatialFunction {

        private SpatialReferenceSystem spatialReferenceSystem;

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... values)
                throws FunctionException {

                Geometry geom = values[0].getAsGeometry();

                int sourceCRS = values[1].getAsInt();

                int targetCRS = values[2].getAsInt();

                if (spatialReferenceSystem == null) {
                        spatialReferenceSystem = new SpatialReferenceSystem(dsf, sourceCRS,
                                targetCRS);
                }
                final Geometry transformedGeom = spatialReferenceSystem.transform(geom);
                transformedGeom.setSRID(targetCRS);
                return ValueFactory.createValue(transformedGeom);
        }

        @Override
        public Value getAggregateResult() {
                return null;
        }

        @Override
        public String getDescription() {
                return "Reproject a geometry from a CRS to new CRS."
                        + " Only EPSG code allowed.";
        }

        @Override
        public Arguments[] getFunctionArguments() {
                return new Arguments[]{new Arguments(Argument.GEOMETRY,
                                Argument.INT, Argument.INT)};
        }

        @Override
        public String getName() {
                return "ST_TRANSFORM";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_TRANSFORM(the_geom, sourceCRSCode, targetCRSCode) from myTable";
        }

        @Override
        public boolean isAggregate() {
                return false;
        }
}
