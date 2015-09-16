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

package org.orbisgis.orbistoolbox.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * GeoData represent a data which can be an SQL table, a JSON file, a Shape file ...
 *
 * @author Sylvain PALOMINOS
 **/

public class GeoData extends ComplexData{

    public static final String geojsonMimeType = "application/json";
    public static final String shapeFileMimeType = "application/octet-stream";
    public static final String textMimeType = "text/plain";
    public static final String sqlTableMimeType = "custom/sql";

    public GeoData() throws MalformedScriptException {
        super(getDefaultFormats());
    }

    private static List<Format> getDefaultFormats() throws MalformedScriptException {
        List<Format> formatList = new ArrayList<>();
        Format shapeFormat = new Format(shapeFileMimeType, URI.create("https://tools.ietf.org/html/rfc2046"));
        formatList.add(shapeFormat);
        Format geoJSONFormat = new Format(geojsonMimeType, URI.create("https://tools.ietf.org/html/rfc4627"));
        formatList.add(geoJSONFormat);
        Format textFormat = new Format(textMimeType, URI.create("https://tools.ietf.org/html/rfc2046"));
        formatList.add(textFormat);
        textFormat.setDefaultFormat(true);
        Format sqlTableFormat = new Format(sqlTableMimeType, URI.create(""));
        formatList.add(sqlTableFormat);
        return formatList;
    }
}
