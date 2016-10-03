/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Enumeration of the LiteralData type.
 *
 * For more information : http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html
 *
 * @author Sylvain PALOMINOS
 */

public enum DataType {
    //LiteralData types
    NUMBER("number"),
    INTEGER("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#integer"),
    DOUBLE("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#double"),
    FLOAT("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#float"),
    SHORT("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#short"),
    BYTE("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#byte"),
    UNSIGNED_BYTE("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#unsignedByte"),
    LONG("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#long"),
    STRING("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#string"),
    BOOLEAN("http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean"),

    //Other types
    OTHER("other"),
    GEOMETRY("geometry"),
    POINT("point"),
    LINESTRING("linestring"),
    POLYGON("polygon"),
    MULTIPOINT("multipolygon"),
    MULTILINESTRING("multilinestring"),
    MULTIPOLYGON("multipolygon"),
    RASTER("raster"),
    NONE("none");

    /** URI for the data type. */
    private URI uri;

    /**
     * Main constructor.
     * @param uriString String of the URI to the reference of the type.
     */
    DataType(String uriString) {
        try {
            this.uri = new URI(uriString);
        } catch (URISyntaxException e) {
            LoggerFactory.getLogger(DataType.class).error(e.getMessage());
        }
    }

    public static DataType getDataTypeFromFieldType(String fieldType){
        return DataType.valueOf(fieldType);
    }

    /**
     * Returns the URI of the type.
     * @return The URI of the type.
     */
    public URI getUri() {
        return uri;
    }

    public static boolean testDBType(DataType dataType, String dbTypeName){
        dbTypeName = dbTypeName.toUpperCase();
        switch(dataType) {
            case INTEGER:
                return (dbTypeName.equals("INT") || dbTypeName.equals("INTEGER") ||
                        dbTypeName.equals("MEDIUMINT") || dbTypeName.equals("INT4") || dbTypeName.equals("SIGNED"));
            case BOOLEAN:
                return (dbTypeName.equals("BOOLEAN") || dbTypeName.equals("BIT") || dbTypeName.equals("BOOL"));
            case BYTE:
                return (dbTypeName.equals("TINYINT"));
            case SHORT:
                return (dbTypeName.equals("SMALLINT") || dbTypeName.equals("INT2") || dbTypeName.equals("YEAR"));
            case LONG:
                return (dbTypeName.equals("BIGINT") || dbTypeName.equals("INT8") || dbTypeName.equals("IDENTITY") ||
                        dbTypeName.equals("BIGSERIAL") || dbTypeName.equals("SERIAL8"));
            case DOUBLE:
                return (dbTypeName.equals("DOUBLE") || dbTypeName.equals("FLOAT") || dbTypeName.equals("FLOAT8"));
            case FLOAT:
                return (dbTypeName.equals("REAL") || dbTypeName.equals("FLOAT4"));
            case STRING:
                return (dbTypeName.equals("VARCHAR") || dbTypeName.equals("LONGVARCHAR") ||
                        dbTypeName.equals("VARCHAR2") || dbTypeName.equals("NVARCHAR") ||
                        dbTypeName.equals("NVARCHAR2") || dbTypeName.equals("VARCHAR_CASESENSITIVE") ||
                        dbTypeName.equals("VARCHAR_IGNORECASE") || dbTypeName.equals("CHAR") ||
                        dbTypeName.equals("CHARACTER") || dbTypeName.equals("NCHAR"));

            case NUMBER:
                return (dbTypeName.equals("INT") || dbTypeName.equals("INTEGER") || dbTypeName.equals("MEDIUMINT") ||
                        dbTypeName.equals("INT4") || dbTypeName.equals("SIGNED") || dbTypeName.equals("TINYINT") ||
                        dbTypeName.equals("SMALLINT") || dbTypeName.equals("INT2") || dbTypeName.equals("YEAR") ||
                        dbTypeName.equals("BIGINT") || dbTypeName.equals("INT8") || dbTypeName.equals("IDENTITY") ||
                        dbTypeName.equals("DOUBLE") || dbTypeName.equals("FLOAT") || dbTypeName.equals("FLOAT8") ||
                        dbTypeName.equals("REAL") || dbTypeName.equals("FLOAT4"));

            case OTHER:
                return (dbTypeName.equals("OTHER"));
            case GEOMETRY:
                return (dbTypeName.equals("POINT") || dbTypeName.equals("POLYGON") || dbTypeName.equals("LINESTRING") ||
                        dbTypeName.equals("MULTIPOINT") || dbTypeName.equals("MULTILINESTRING") ||
                        dbTypeName.equals("GEOMETRY") || dbTypeName.equals("MULTIPOLYGON") ||
                        dbTypeName.equals("GEOMETRYCOLLECTION"));
            case POINT:
                return (dbTypeName.equalsIgnoreCase(POINT.name()));
            case LINESTRING:
                return (dbTypeName.equalsIgnoreCase(LINESTRING.name()));
            case POLYGON:
                return (dbTypeName.equalsIgnoreCase(POLYGON.name()));
            case MULTIPOINT:
                return (dbTypeName.equalsIgnoreCase(MULTIPOINT.name()));
            case MULTILINESTRING:
                return (dbTypeName.equalsIgnoreCase(MULTILINESTRING.name()));
            case MULTIPOLYGON:
                return (dbTypeName.equalsIgnoreCase(MULTIPOLYGON.name()));
            default: return false;
        }
    }
}
