/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.drivers;

import org.junit.Test;
import org.junit.Before;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.types.Constraint;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.engine.SemanticException;
import org.gdms.sql.engine.ParseException;
import com.vividsolutions.jts.geom.Geometry;
import org.gdms.TestBase;
import org.gdms.data.types.Dimension3DConstraint;
import org.gdms.sql.engine.SQLEngine;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class ExportTest extends AbstractDBTest {

        @Before
        @Override
        public void setUp() throws Exception {
                super.setUp();
                if (TestBase.postGisAvailable) {
                        deleteTable(getPostgreSQLSource("pglandcoverfromshp"));
                }
        }

        @Test
        public void testSHP2H22PostgreSQL2SHP_2D() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                sm.remove("landcover2000");
                testSHP2H22PostgreSQL2SHP("CALL register('" + TestBase.internalData + "p3d.shp', "
                        + "'landcover2000');", "gid", 2);
        }

        @Test
        public void testSHP2H22PostgreSQL2SHP_3D() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                sm.remove("landcover2000");
                testSHP2H22PostgreSQL2SHP(
                        "CALL register('" + TestBase.internalData + "p3d.shp', "
                        + "'landcover2000');", "gid", 3);
        }

        private void testSHP2H22PostgreSQL2SHP(String script, String orderField,
                int dim) throws Exception {
                script += "CALL register('h2','', '0', '"
                        + TestBase.internalData + "backup/h2landcoverfromshp',"
                        + "'sa','','h2landcoverfromshp', 'h2landcoverfromshp');";
                script += "create table h2landcoverfromshp as select * from landcover2000;";

                script += "CALL register('postgresql','127.0.0.1', '5432', "
                        + "'gdms','postgres','postgres','pglandcoverfromshp', 'pglandcoverfromshp');";
                if (dim == 2) {
                        script += "create table pglandcoverfromshp as "
                                + "select * from h2landcoverfromshp;";
                } else {
                        script += "create table pglandcoverfromshp as "
                                + "select constraint3d(the_geom), gid from h2landcoverfromshp;";
                }

                script += "CALL register('" + TestBase.internalData + "backup/landcoverfrompg.shp', 'res');";
                script += "create table res as select * from pglandcoverfromshp;";
                check(script, orderField);
        }

        private void check(String script, String orderField)
                throws DriverException, ParseException,
                DataSourceCreationException, NoSuchTableException,
                DriverLoadException,
                SemanticException {
                executeGDMSScript(script);

                DataSource dsRes = dsf.getDataSourceFromSQL("select the_geom"
                        + " from res order by " + orderField + ";");
                DataSource ds = dsf.getDataSourceFromSQL("select the_geom"
                        + " from landcover2000 order by " + orderField + ";");
                ds.open();
                dsRes.open();
                Dimension3DConstraint dc1 = (Dimension3DConstraint) ds.getMetadata().getFieldType(0).getConstraint(Constraint.DIMENSION_3D_GEOMETRY);
                Dimension3DConstraint dc2 = (Dimension3DConstraint) dsRes.getMetadata().getFieldType(0).getConstraint(Constraint.DIMENSION_3D_GEOMETRY);
                assertTrue((dc2 == null) || (dc1 == null)
                        || (dc1.getDimension() == dc2.getDimension()));
                for (int i = 0; i < ds.getRowCount(); i++) {
                        Value v1 = ds.getFieldValue(i, 0);
                        Geometry g1 = v1.getAsGeometry();
                        Value v2 = dsRes.getFieldValue(i, 0);
                        Geometry g2 = v2.getAsGeometry();

                        if (dc1.getDimension() == 2) {
                                assertEquals(g1, g2);
                        } else {
                                assertTrue(v1.equals(v2).getAsBoolean());
                        }
                }
                ds.close();
                dsRes.close();
        }

//	public void testSHP2PostgreSQL2H22SHP_2D() throws Exception {
//		testSHP2PostgreSQL2H22SHP("select register('../../datas2tests/shp/"
//				+ "mediumshape2D/landcover2000.shp', " + "'landcover2000');",
//				"gid", 2);
//	}
        @Test
        public void testSHP2PostgreSQL2H22SHP_3D() throws Exception {
                assumeTrue(TestBase.postGisAvailable);
                sm.remove("landcover2000");
                testSHP2PostgreSQL2H22SHP(
                        "CALL register('" + TestBase.internalData + "p3d.shp', "
                        + "'landcover2000');", "gid", 3);
        }

        private void testSHP2PostgreSQL2H22SHP(String script, String orderField,
                int dim) throws Exception {
                script += "CALL register('postgresql','127.0.0.1', '5432', "
                        + "'gdms','postgres','postgres','pglandcoverfromshp', 'pglandcoverfromshp');";
                script += "create table pglandcoverfromshp as select * from landcover2000;";

                script += "CALL register('h2','', '0', "
                        + "'" + TestBase.internalData + "backup/h2landcoverfromshp',"
                        + "'sa','','h2landcoverfromshp', 'h2landcoverfromshp');";
                script += "create table h2landcoverfromshp as select * from pglandcoverfromshp;";

                script += "CALL register('" + TestBase.internalData + "backup/landcoverfrompg.shp', 'res');";
                if (dim == 2) {
                        script += "create table res as "
                                + "select * from h2landcoverfromshp;";
                } else {
                        script += "create table res as "
                                + "select constraint3d(the_geom), gid from h2landcoverfromshp;";
                }
                check(script, orderField);
        }

        private void executeGDMSScript(String script) throws
                DriverException, ParseException, SemanticException {
                SQLEngine engine = new SQLEngine(dsf);
                engine.execute(script);
        }
}
