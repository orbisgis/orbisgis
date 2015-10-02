/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.controller.processexecution.utils;

import org.orbisgis.orbistoolbox.model.Format;
import org.orbisgis.orbistoolbox.model.MalformedScriptException;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Class creating formats.
 *
 * @author Sylvain PALOMINOS
 **/

public class FormatFactory {

    public static final String SHAPEFILE_EXTENSION = "shp";
    public static final String GEOJSON_EXTENSION = "geojson";
    public static final String SQL_EXTENSION = "sqlTable";
    public static final String WKT_EXTENSION = "wkt";
    public static final String GEOMETRY_EXTENSION = "geometry";
    public static final String OTHER_EXTENSION = "*";

    public static final String SHAPEFILE_MIMETYPE = "application/octet-stream";
    public static final String GEOJSON_MIMETYPE = "application/json";
    public static final String SQL_MIMETYPE = "custom/sql";
    public static final String WKT_MIMETYPE = "custom/wkt";
    public static final String OTHER_MIMETYPE = "custom/other";

    public static final String SHAPEFILE_URI = "https://tools.ietf.org/html/rfc2046";
    public static final String GEOJSON_URI = "https://tools.ietf.org/html/rfc4627";
    public static final String SQL_URI = "";
    public static final String WKT_URI = "";
    public static final String OTHER_URI = "";

    public static final String SQL_DESCRIPTION = "SQL table";
    public static final String WKT_DESCRIPTION = "Well-Known Text";

    /**
     * Returns the Format from the given extension. If the extension isn't recognize, return null.
     * @param extension Extension used for the Format generation.
     * @return Format corresponding to the given extension.
     */
    public static Format getFormatFromExtension(String extension){
        try {
            switch(extension){
                case SHAPEFILE_EXTENSION:
                    return new Format(SHAPEFILE_MIMETYPE, URI.create(SHAPEFILE_URI));
                case GEOJSON_EXTENSION:
                    return new Format(GEOJSON_MIMETYPE, URI.create(GEOJSON_URI));
                case SQL_EXTENSION:
                    return new Format(SQL_MIMETYPE, URI.create(SQL_URI));
                case WKT_EXTENSION:
                    return new Format(WKT_MIMETYPE, URI.create(WKT_URI));
                case OTHER_EXTENSION:
                    return new Format(OTHER_MIMETYPE, URI.create(OTHER_URI));
                default:
                    return new Format(extension, URI.create(OTHER_URI));
            }
        } catch (MalformedScriptException e) {
            LoggerFactory.getLogger(FormatFactory.class);
            return null;
        }
    }

    /**
     * Return a list of Format from a list of extension.
     * @param extensions List of extension for the Format generation.
     * @return A list of Format.
     */
    public static List<Format> getFormatsFromExtensions(List<String> extensions){
        List<Format> formatList = new ArrayList<>();
        for(String extension : extensions){
            formatList.add(getFormatFromExtension(extension));
        }
        return formatList;
    }

    /**
     * Return a list of Format from an array of extension.
     * @param extensions List of extension for the Format generation.
     * @return A list of Format.
     */
    public static List<Format> getFormatsFromExtensions(String[] extensions){
        List<Format> formatList = new ArrayList<>();
        for(String extension : extensions){
            formatList.add(getFormatFromExtension(extension));
        }
        return formatList;
    }

    /**
     * Return the extension from a Format.
     * @param format Format to decode.
     * @return File extension.
     */
    public static String getFormatExtension(Format format){
        switch(format.getMimeType()){
            case SHAPEFILE_MIMETYPE:
                return SHAPEFILE_EXTENSION;
            case GEOJSON_MIMETYPE:
                return GEOJSON_EXTENSION;
            case SQL_MIMETYPE:
                return SQL_DESCRIPTION;
            case WKT_MIMETYPE:
                return WKT_DESCRIPTION;
            case OTHER_MIMETYPE:
                return OTHER_EXTENSION;
            default:
                return format.getMimeType();
        }
    }
}
