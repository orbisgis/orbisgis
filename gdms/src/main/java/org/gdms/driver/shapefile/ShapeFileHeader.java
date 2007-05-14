/*
 *    GISToolkit - Geographical Information System Toolkit
 *    (C) 2002, Ithaqua Enterprises Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
/* gvSIG. Sistema de Informacin Geogrfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * Class to represent the header in the shape file.
 */
public class ShapeFileHeader {
    /**
     * Shape Type Value Shape Type 0 Null Shape 1 Point 3 PolyLine 5 Polygon 8
     * MultiPoint 11 PointZ 13 PolyLineZ 15 PolygonZ 18 MultiPointZ 21 PointM
     * 23 PolyLineM 25 PolygonM 28 MultiPointM 31 MultiPatch
     */

    /* The null shape type, there is no shape for this record. */
    public static final int SHAPE_NULL = 0;
    public static final int SHAPE_POINT = 1;
    public static final int SHAPE_POLYLINE = 3;
    public static final int SHAPE_POLYGON = 5;
    public static final int SHAPE_MULTIPOINT = 8;
    public static final int SHAPE_POINTZ = 11;
    public static final int SHAPE_POLYLINEZ = 13;
    public static final int SHAPE_POLYGONZ = 15;
    public static final int SHAPE_MULTIPOINTZ = 18;
    public static final int SHAPE_POINTM = 21;
    public static final int SHAPE_POLYLINEM = 23;
    public static final int SHAPE_POLYGONM = 25;
    public static final int SHAPE_MULTIPOINTM = 28;
    public static final int SHAPE_MULTIPATCH = 31;

    /** File Code, must be the value 9994 */
    public int myFileCode = 9994;

    /** Unused 1; */
    public int myUnused1 = 0;

    /** Unused 2; */
    public int myUnused2 = 0;

    /** Unused 3; */
    public int myUnused3 = 0;

    /** Unused 4; */
    public int myUnused4 = 0;

    /** Unused 5; */
    public int myUnused5 = 0;

    /** File Length; */
    public int myFileLength = 0;

    /** Version of the file. */
    public int myVersion = 1000;
    public int myShapeType = 0;

    /** BoundingBox Xmin */
    public double myXmin = 0;

    /** BoundingBox Ymin */
    public double myYmin = 0;

    /** BoundingBox Xmax */
    public double myXmax = 0;

    /** BoundingBox Ymax */
    public double myYmax = 0;

    /** BoundingBox Zmin */
    public double myZmin = 0;

    /** BoundingBox Zmax */
    public double myZmax = 0;

    /** BoundingBox Zmin */
    public double myMmin = 0;

    /** BoundingBox Zmax */
    public double myMmax = 0;

    // notify about warnings.
    private boolean myWarning = true;

    /**
     * ShapeFileHeader constructor comment.
     */
    public ShapeFileHeader() {
        super();
    }

    /**
     * Return the file code.
     *
     * @return Entero.
     */
    public int getFileCode() {
        return myFileCode;
    }

    /**
     * Return the version of the file.
     *
     * @return Versin.
     */
    public int getVersion() {
        return myVersion;
    }

    /**
     * Get the extents of the shape file.
     *
     * @return FullExtent.
     */
    public java.awt.geom.Rectangle2D.Double getFileExtents() {
        return new java.awt.geom.Rectangle2D.Double(myXmin, myYmin,
            myXmax - myXmin, myYmax - myYmin);
    }

    /**
     * Print warnings to system.out.
     *
     * @param inWarning boolean.
     */
    public void setWarnings(boolean inWarning) {
        myWarning = inWarning;
    }

    /**
     * Return the length of the header in 16 bit words..
     *
     * @return Longitud de la cabecera.
     */
    public int getHeaderLength() {
        return 50;
    }

    /**
     * Return the number of 16 bit words in the shape file as recorded in the
     * header
     *
     * @return Longitud del fichero.
     */
    public int getFileLength() {
        return myFileLength;
    }

    /**
     * Read the header from the shape file.
     *
     * @param in ByteBuffer.
     */
    public void readHeader(BigByteBuffer2 in) {
        // the first four bytes are integers
        // in.setLittleEndianMode(false);
        in.order(ByteOrder.BIG_ENDIAN);
        myFileCode = in.getInt();

        if (myFileCode != 9994) {
            warn("File Code = " + myFileCode + " Not equal to 9994");
        }

        // From 4 to 8 are unused.
        myUnused1 = in.getInt();

        // From 8 to 12 are unused.
        myUnused2 = in.getInt();

        // From 12 to 16 are unused.
        myUnused3 = in.getInt();

        // From 16 to 20 are unused.
        myUnused4 = in.getInt();

        // From 20 to 24 are unused.
        myUnused5 = in.getInt();

        // From 24 to 28 are the file length.
        myFileLength = in.getInt();

        // From 28 to 32 are the File Version.
        in.order(ByteOrder.LITTLE_ENDIAN);
        myVersion = in.getInt();

        // From 32 to 36 are the Shape Type.
        myShapeType = in.getInt();

        // From 36 to 44 are Xmin.
        myXmin = in.getDouble(); // Double.longBitsToDouble(in.getLong());

        // From 44 to 52 are Ymin.
        myYmin = in.getDouble();

        // From 52 to 60 are Xmax.
        myXmax = in.getDouble();

        // From 60 to 68 are Ymax.
        myYmax = in.getDouble();

        // From 68 to 76 are Zmin.
        myZmin = in.getDouble();

        // From 76 to 84 are Zmax.
        myZmax = in.getDouble();

        // From 84 to 92 are Mmin.
        myMmin = in.getDouble();

        // From 92 to 100 are Mmax.
        myMmax = in.getDouble();

        // that is all 100 bytes of the header.
    }
    public void write(ByteBuffer out,int type,
  		  int numGeoms,int length,double minX,double minY,double maxX,double maxY,double minZ,double maxZ,double minM,double maxM)
  		  throws IOException {
  		    out.order(ByteOrder.BIG_ENDIAN);
  		    
  		    out.putInt(myFileCode);
  		    
  		    for (int i = 0; i < 5; i++) {
  		      out.putInt(0); //Skip unused part of header
  		    }

  		    out.putInt(length);
  		    
  		    out.order(ByteOrder.LITTLE_ENDIAN);
  		    
  		    out.putInt(myVersion);
  		    out.putInt(type);
  		    
  		    //write the bounding box
  		    out.putDouble(minX);
  		    out.putDouble(minY);
  		    out.putDouble(maxX);
  		    out.putDouble(maxY);
  		    /*
  		    out.putDouble(minZ);
  		    out.putDouble(minZ);
  		    out.putDouble(maxM);
  		    out.putDouble(maxM);*/
  		    out.order(ByteOrder.BIG_ENDIAN);
  		    for (int i = 0; i < 8; i++) {
  		      out.putInt(0); //Skip unused part of header
  		    }
  		   
  		  }

    /**
     * Muestra por consola los warning.
     *
     * @param inWarn warning.
     */
    private void warn(String inWarn) {
        if (myWarning) {
            System.out.print("WARNING: ");
            System.out.println(inWarn);
        }
    }
}
