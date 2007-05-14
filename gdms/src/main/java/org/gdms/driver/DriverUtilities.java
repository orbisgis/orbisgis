package org.gdms.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.gdms.data.SpatialDataSource;
import org.geotools.feature.FeatureType;

import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;


/**
 * Utility method for the drivers
 */
public class DriverUtilities {

    private static int BUF_SIZE = 50000;

    /**
     * Translates the specified code by using the translation table specified
     * by the two last arguments. If there is no translation a RuntimeException is
     * thrown.
     *
     * @param code code to translate
     * @param source keys on the translation table
     * @param target translation to the keys
     *
     * @return translated code
     */
    public static int translate(int code, int[] source, int[] target) {
        for (int i = 0; i < source.length; i++) {
            if (code == source[i]){
                return target[i];
            }
        }

        throw new RuntimeException("code mismatch");
    }

    public static long copy(File input, File output) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(input);
            return copy(in, output);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static long copy(File input, File output, byte[] copyBuffer) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
            return copy(in, out, copyBuffer);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static long copy(InputStream in, File outputFile) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            return copy(in, out);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        return copy(in, out, buf);
    }

    public static long copy(InputStream in, OutputStream out, byte[] copyBuffer) throws IOException {
        long bytesCopied = 0;
        int read = -1;

        while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
            out.write(copyBuffer, 0, read);
            bytesCopied += read;
        }
        return bytesCopied;
    }

	public static int getGDBMSGeometryType(FeatureType schema) {
			Class c = schema.getDefaultGeometry().getType();
			if (Point.class.isAssignableFrom(c)) {
				return SpatialDataSource.POINT;
			} else if (MultiPoint.class.isAssignableFrom(c)) {
				return SpatialDataSource.MULTIPOINT;
			} else if (LineString.class.isAssignableFrom(c)) {
				return SpatialDataSource.LINESTRING;
			} else if (MultiLineString.class.isAssignableFrom(c)) {
				return SpatialDataSource.MULTILINESTRING;
			} else if (Polygon.class.isAssignableFrom(c)) {
				return SpatialDataSource.POLYGON;
			} else if (MultiPolygon.class.isAssignableFrom(c)) {
				return SpatialDataSource.MULTIPOLYGON;
			} else if (GeometryCollection.class.isAssignableFrom(c)) {
				return SpatialDataSource.ANY;
			}

			throw new RuntimeException("Unknown geometry type");
	}

}
