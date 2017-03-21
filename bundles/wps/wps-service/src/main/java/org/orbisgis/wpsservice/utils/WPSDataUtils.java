/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.wpsservice.utils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Class containing methods used to manipulate data for the WPS scripts.
 *
 * @author Sylvain PALOMINOS
 */
public class WPSDataUtils {


    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(WPSDataUtils.class);

    /**
     * Convert a BoundingBox string representation into a JTS geometry
     * @param string Geometry string representation.
     * @return A JTS geometry.
     * @throws ParseException
     */
    public static Geometry parseStringToBoundingBox(String string) throws ParseException {
        Geometry geometry;
        String[] split = string.split(";");
        String[] wkt;
        String srid;
        if(split[0].contains(":")){
            srid = string.split(";")[0].split(":")[1];
            wkt = string.split(";")[1].split(",");
        }
        else{
            srid = string.split(";")[1].split(":")[1];
            wkt = string.split(";")[0].split(",");
        }
        if(wkt.length != 4){
            throw new ParseException(I18N.tr("Only 2D bounding boxes are supported yet."));
        }
        String minX, minY, maxX, maxY;
        minX = wkt[0];
        minY = wkt[1];
        maxX = wkt[2];
        maxY = wkt[3];
        //Read the string to retrieve the Geometry
        geometry = new WKTReader().read("POLYGON((" +
                minX+" "+minY+"," +
                maxX+" "+minY+"," +
                maxX+" "+maxY+"," +
                minX+" "+maxY+"," +
                minX+" "+minY+"))");
        geometry.setSRID(Integer.parseInt(srid));
        return geometry;
    }

    /**
     * Convert a BoundingBox JTS geometry into its string representation.
     * @param geometry BoundingBox JTS Geometry to convert.
     * @return The BoundingBox string representation.
     */
    public static String parseBoundingBoxToString(Geometry geometry) {
        String wkt = new WKTWriter().write(geometry);
        //Update the WKT string to have this pattern : ":SRID;minX,minY,maxX,maxY"
        wkt = wkt.replace("POLYGON ((", "");
        wkt = wkt.replace("))", "");
        String[] split = wkt.split(", ");
        wkt = split[0].replaceAll(" ", ",") + "," + split[2].replaceAll(" ", ",");
        String str = ":" + geometry.getSRID() + ";" + wkt;
        return str;
    }


    /**
     * Convert a GeometryData string representation into a JTS geometry
     * @param string Geometry string representation.
     * @return A JTS geometry.
     * @throws ParseException
     */
    public static Geometry parseStringToGeometry(String string) throws ParseException {
        Geometry geometry = new WKTReader().read(string);
        return geometry;
    }

    /**
     * Convert a JTS geometry into its string representation.
     * @param geometry Jts Geometry to convert.
     * @return The geometry string representation.
     */
    public static String parseGeometryToString(Geometry geometry) {
        String wkt = new WKTWriter().write(geometry);
        return wkt;
    }
}
