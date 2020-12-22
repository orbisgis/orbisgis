/*
 * Bundle sql-parser is part of the OrbisGIS platform
 *
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
 * sql-parser is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * sql-parser is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * sql-parser is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * sql-parser. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sql;

import java.util.HashMap;

/**
 * This class is used to match the functions as defined in
 *
 * OpenGIS®Implementation Standard for Geographic information -Simple feature access -Part 2: SQL option
 * See : https://www.ogc.org/standards/sfs
 *
 * to the geootools functions
 */
public class SQLFunctions {

    /**
     * key : sfs function
     * value : geotools function
     */
    static HashMap<String, String> functions = new HashMap<>();
    static {
        functions.put("ST_DIMENSION", "DIMENSION");
        functions.put("ST_LENGTH", "GEOMLENGTH");
        functions.put("ST_AREA", "AREA");
        functions.put("ST_BUFFER", "BUFFER");
        functions.put("ST_CENTROID", "CENTROID");
    };

    /**
     * Find if there is a SFS representation of the function name
     * if not return the input function name
     *
     * @param functionName
     * @return
     */
    static String getFunction(String functionName){
        String sqlFunction = SQLFunctions.functions.get(functionName.toUpperCase());
        if(sqlFunction!=null){
            return  sqlFunction;
        }
        return functionName;
    }
}
