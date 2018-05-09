package org.orbisgis.postgis_jts;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.locationtech.jts.io.WKTReader;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class JtsGeometry extends PGobject {
    private static final long serialVersionUID = 256L;
    Geometry geom;
    static final JtsBinaryParser bp = new JtsBinaryParser();
    static final JtsBinaryWriter bw = new JtsBinaryWriter();
    static final PrecisionModel prec = new PrecisionModel();
    static final CoordinateSequenceFactory csfac;
    static final GeometryFactory geofac;
    static final WKTReader reader;

    public JtsGeometry() {
        this.setType("geometry");
    }

    public JtsGeometry(Geometry geom) {
        this();
        this.geom = geom;
    }

    public JtsGeometry(String value) throws SQLException {
        this();
        this.setValue(value);
    }

    public void setValue(String value) throws SQLException {
        this.geom = geomFromString(value);
    }

    public static Geometry geomFromString(String value) throws SQLException {
        try {
            value = value.trim();
            if (!value.startsWith("00") && !value.startsWith("01")) {
                int srid = 0;
                if (value.startsWith("SRID=")) {
                    String[] temp = value.split(";");
                    value = temp[1].trim();
                    srid = Integer.parseInt(temp[0].substring(5));
                }

                Geometry result = reader.read(value);
                setSridRecurse(result, srid);
                return result;
            } else {
                return bp.parse(value);
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            throw new SQLException("Error parsing SQL data:" + var4);
        }
    }

    public static void setSridRecurse(Geometry geom, int srid) {
        geom.setSRID(srid);
        int subcnt;
        if (geom instanceof GeometryCollection) {
            int num = geom.getNumGeometries();

            for(subcnt = 0; subcnt < num; ++subcnt) {
                setSridRecurse(geom.getGeometryN(subcnt), srid);
            }
        } else if (geom instanceof Polygon) {
            Polygon poly = (Polygon)geom;
            poly.getExteriorRing().setSRID(srid);
            subcnt = poly.getNumInteriorRing();

            for(int i = 0; i < subcnt; ++i) {
                poly.getInteriorRingN(i).setSRID(srid);
            }
        }

    }

    public Geometry getGeometry() {
        return this.geom;
    }

    public String toString() {
        return this.geom.toString();
    }

    public String getValue() {
        return bw.writeHexed(this.getGeometry());
    }

    public Object clone() {
        JtsGeometry obj = new JtsGeometry(this.geom);
        obj.setType(this.type);
        return obj;
    }

    public boolean equals(Object obj) {
        if (obj instanceof JtsGeometry) {
            Geometry other = ((JtsGeometry)obj).geom;
            if (this.geom == other) {
                return true;
            }

            if (this.geom != null && other != null) {
                return other.equals(this.geom);
            }
        }

        return false;
    }

    static {
        csfac = PackedCoordinateSequenceFactory.DOUBLE_FACTORY;
        geofac = new GeometryFactory(prec, 0, csfac);
        reader = new WKTReader(geofac);
    }
}

