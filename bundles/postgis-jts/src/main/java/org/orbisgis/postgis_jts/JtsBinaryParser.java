package org.orbisgis.postgis_jts;


import org.locationtech.jts.geom.*;
import org.postgis.binary.ByteGetter;
import org.postgis.binary.ValueGetter;
import org.locationtech.jts.geom.impl.PackedCoordinateSequence.Double;

public class JtsBinaryParser {
    public JtsBinaryParser() {
    }

    public static ValueGetter valueGetterForEndian(ByteGetter bytes) {
        if (bytes.get(0) == 0) {
            return new ValueGetter.XDR(bytes);
        } else if (bytes.get(0) == 1) {
            return new ValueGetter.NDR(bytes);
        } else {
            throw new IllegalArgumentException("Unknown Endian type:" + bytes.get(0));
        }
    }

    public Geometry parse(String value) {
        ByteGetter.StringByteGetter bytes = new ByteGetter.StringByteGetter(value);
        return this.parseGeometry(valueGetterForEndian(bytes));
    }

    public Geometry parse(byte[] value) {
        ByteGetter.BinaryByteGetter bytes = new ByteGetter.BinaryByteGetter(value);
        return this.parseGeometry(valueGetterForEndian(bytes));
    }

    protected Geometry parseGeometry(ValueGetter data) {
        return this.parseGeometry(data, 0, false);
    }

    protected Geometry parseGeometry(ValueGetter data, int srid, boolean inheritSrid) {
        byte endian = data.getByte();
        if (endian != data.endian) {
            throw new IllegalArgumentException("Endian inconsistency!");
        } else {
            int typeword = data.getInt();
            int realtype = typeword & 536870911;
            boolean haveZ = (typeword & -2147483648) != 0;
            boolean haveM = (typeword & 1073741824) != 0;
            boolean haveS = (typeword & 536870912) != 0;
            if (haveS) {
                int newsrid = org.postgis.Geometry.parseSRID(data.getInt());
                if (inheritSrid && newsrid != srid) {
                    throw new IllegalArgumentException("Inconsistent srids in complex geometry: " + srid + ", " + newsrid);
                }

                srid = newsrid;
            } else if (!inheritSrid) {
                srid = 0;
            }

            Object result;
            switch(realtype) {
                case 1:
                    result = this.parsePoint(data, haveZ, haveM);
                    break;
                case 2:
                    result = this.parseLineString(data, haveZ, haveM);
                    break;
                case 3:
                    result = this.parsePolygon(data, haveZ, haveM, srid);
                    break;
                case 4:
                    result = this.parseMultiPoint(data, srid);
                    break;
                case 5:
                    result = this.parseMultiLineString(data, srid);
                    break;
                case 6:
                    result = this.parseMultiPolygon(data, srid);
                    break;
                case 7:
                    result = this.parseCollection(data, srid);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Geometry Type!");
            }

            ((Geometry)result).setSRID(srid);
            return (Geometry)result;
        }
    }

    private Point parsePoint(ValueGetter data, boolean haveZ, boolean haveM) {
        double X = data.getDouble();
        double Y = data.getDouble();
        Point result;
        if (haveZ) {
            double Z = data.getDouble();
            result = JtsGeometry.geofac.createPoint(new Coordinate(X, Y, Z));
        } else {
            result = JtsGeometry.geofac.createPoint(new Coordinate(X, Y));
        }

        if (haveM) {
            data.getDouble();
        }

        return result;
    }

    private void parseGeometryArray(ValueGetter data, Geometry[] container, int srid) {
        for(int i = 0; i < container.length; ++i) {
            container[i] = this.parseGeometry(data, srid, true);
        }

    }

    private CoordinateSequence parseCS(ValueGetter data, boolean haveZ, boolean haveM) {
        int count = data.getInt();
        int dims = haveZ ? 3 : 2;
        CoordinateSequence cs = new Double(count, dims);

        for(int i = 0; i < count; ++i) {
            for(int d = 0; d < dims; ++d) {
                cs.setOrdinate(i, d, data.getDouble());
            }

            if (haveM) {
                data.getDouble();
            }
        }

        return cs;
    }

    private MultiPoint parseMultiPoint(ValueGetter data, int srid) {
        Point[] points = new Point[data.getInt()];
        this.parseGeometryArray(data, points, srid);
        return JtsGeometry.geofac.createMultiPoint(points);
    }

    private LineString parseLineString(ValueGetter data, boolean haveZ, boolean haveM) {
        return JtsGeometry.geofac.createLineString(this.parseCS(data, haveZ, haveM));
    }

    private LinearRing parseLinearRing(ValueGetter data, boolean haveZ, boolean haveM) {
        return JtsGeometry.geofac.createLinearRing(this.parseCS(data, haveZ, haveM));
    }

    private Polygon parsePolygon(ValueGetter data, boolean haveZ, boolean haveM, int srid) {
        int holecount = data.getInt() - 1;
        LinearRing[] rings = new LinearRing[holecount];
        LinearRing shell = this.parseLinearRing(data, haveZ, haveM);
        shell.setSRID(srid);

        for(int i = 0; i < holecount; ++i) {
            rings[i] = this.parseLinearRing(data, haveZ, haveM);
            rings[i].setSRID(srid);
        }

        return JtsGeometry.geofac.createPolygon(shell, rings);
    }

    private MultiLineString parseMultiLineString(ValueGetter data, int srid) {
        int count = data.getInt();
        LineString[] strings = new LineString[count];
        this.parseGeometryArray(data, strings, srid);
        return JtsGeometry.geofac.createMultiLineString(strings);
    }

    private MultiPolygon parseMultiPolygon(ValueGetter data, int srid) {
        int count = data.getInt();
        Polygon[] polys = new Polygon[count];
        this.parseGeometryArray(data, polys, srid);
        return JtsGeometry.geofac.createMultiPolygon(polys);
    }

    private GeometryCollection parseCollection(ValueGetter data, int srid) {
        int count = data.getInt();
        Geometry[] geoms = new Geometry[count];
        this.parseGeometryArray(data, geoms, srid);
        return JtsGeometry.geofac.createGeometryCollection(geoms);
    }
}

