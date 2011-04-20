package org.gdms.geometryUtils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import java.util.Set;
import java.util.TreeSet;

public class GeometryConvert {

        private static GeometryFactory factory = new GeometryFactory();

        /**
         * Convert a geometry into a MultiPoint
         * @param geometry
         * @return MutiPoint
         */
        public static MultiPoint toMultiPoint(Geometry geometry) {
                return factory.createMultiPoint(geometry.getCoordinates());
        }

        /**
         * Convert a geometry into a set of simple linestring (segment)
         * @param geometry
         * @return
         */
        public static Set<LineString> toSegmentsLineString(Geometry geometry) {
                if(geometry.getDimension()>0){
                Set<LineString> segmentSet = new TreeSet<LineString>();
                Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(geometry.getCoordinates());
                for (int j = 0; j < coords.length - 1; j++) {
                        LineString lineString = factory.createLineString(new Coordinate[]{coords[j], coords[j + 1]});
                        segmentSet.add(lineString);
                }
                return segmentSet;
                }
                return null;
        }

        /**
         * Convert a geometry into a set of simple linestring (segment) stored in a multilinestring
         * @param geometry
         * @return
         */
        public static MultiLineString toSegmentsMultiLineString(Geometry geometry) {
                Set<LineString> result = toSegmentsLineString(geometry);
                if(result!=null){
                return factory.createMultiLineString(result.toArray(new LineString[result.size()]));
                }
                return null;
        }

        /**
         * Convert from envelope to polygon geometry
         * @param envelope
         * @return
         */
        public static Geometry toGeometry(Envelope envelope) {
                if ((envelope.getWidth() == 0) && (envelope.getHeight() == 0)) {
                        return factory.createPoint(new Coordinate(envelope.getMinX(),
                                envelope.getMinY()));
                }

                if ((envelope.getWidth() == 0) || (envelope.getHeight() == 0)) {
                        return factory.createLineString(new Coordinate[]{
                                        new Coordinate(envelope.getMinX(), envelope.getMinY()),
                                        new Coordinate(envelope.getMaxX(), envelope.getMaxY())});
                }

                return factory.createLinearRing(new Coordinate[]{
                                new Coordinate(envelope.getMinX(), envelope.getMinY()),
                                new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                                new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                                new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                                new Coordinate(envelope.getMinX(), envelope.getMinY())});
        }
}
