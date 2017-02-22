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
package org.orbisgis.coremap.renderer.classification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;

/**
 * Some methods used for classification...
 * @author Maxence Laurent
 * @author Alexis Guéganno
 * @author Erwan Bocher
 */
public class ClassificationUtils {

    private ClassificationUtils() {
    }

    /**
     * Retrieves the double values in {@code sds} from {@code value} in
     * ascending order.
     *
     * @param connection
     * @param table
     * @param value
     * @return
     * @throws SQLException
     * @throws ParameterException
     */
    public static List<Double> getSortedValues(Connection connection, String table, RealParameter value)
            throws SQLException, ParameterException {
        List<Double> values = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT " + TableLocation.quoteIdentifier(value.toString()) + " as fieldName FROM " + table + " ORDER BY fieldName")) {
            while (rs.next()) {
                values.add(rs.getDouble(1));
            }
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * Gets the minimum and maximum values of {@code table} from {@code value}.
     *
     * @param connection SQL Connection
     * @param table      Table identifier
     * @param field      Name of the column
     * @return [Min, Max] value
     * @throws java.sql.SQLException
     * @throws ParameterException
     */
    public static double[] getMinAndMax(Connection connection, String table, String field)
            throws SQLException, ParameterException {
        double[] minAndMax = new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT MIN(" + TableLocation.quoteIdentifier(field) + ") as minValue," +
                     " MAX(" + TableLocation.quoteIdentifier(field) + ") as maxValue FROM " + table)) {
            if (rs.next()) {
                minAndMax[0] = rs.getDouble(1);
                minAndMax[1] = rs.getDouble(2);
            }
        }
        return minAndMax;
    }
}
