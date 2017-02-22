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
package org.orbisgis.coremap.renderer.se.parameter.real;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.coremap.renderer.classification.ClassificationUtils;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 * @author Nicolas Fortin
 */
public class ClassificationTest {
    private static Connection connection;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = H2GISDBFactory.createDataSource(ClassificationTest.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        H2GISFunctions.load(connection);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
    }

    private Connection getConnection() {
        return connection;
    }

    @Test
    public void testMinMax() throws SQLException, ParameterException {
        try(Statement st = getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS TEST");
            st.execute("CREATE TABLE TEST(val double)");
            st.execute("INSERT INTO TEST VALUES (null), (1), (4), (-50), (83), (22)");
            double[] minMax = ClassificationUtils.getMinAndMax(connection, "TEST", "VAL");
            assertEquals(-50, minMax[0], 1e-12);
            assertEquals(83, minMax[1], 1e-12);
        }
    }
}
