/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.geocatalog.filters;

import org.h2gis.utilities.TableLocation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * A filter reject or accept DataSource according to properties.
 */
public interface IFilter {
    /** {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])} */
    enum ATTRIBUTES {
        TABLE_CAT ,TABLE_SCHEM,TABLE_NAME,TABLE_TYPE ,REMARKS,TYPE_CAT,TYPE_SCHEM,TYPE_NAME,SELF_REFERENCING_COL_NAME,REF_GENERATION,
        GEOMETRY_TYPE,  /** WKT geometry type */
        LOCATION,       /** Equivalent to TableLocation.toString()*/
        LABEL           /** Table label in GeoCatalog */
        }
    /**
    * Does this filter reject or accept this Source
    * @param tableProperties Table metadata, {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
    * @return True if the Source should be shown
    */
	boolean accepts(TableLocation table, Map<ATTRIBUTES, String> tableProperties);
}
