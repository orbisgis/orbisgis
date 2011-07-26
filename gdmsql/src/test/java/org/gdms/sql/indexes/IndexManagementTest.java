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
package org.gdms.sql.indexes;

import org.junit.Before;
import org.junit.Test;
import java.io.File;


import org.gdms.SQLBaseTest;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.indexes.IndexManager;
import org.gdms.source.SourceManager;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.sql.engine.SQLEngine;
import org.gdms.sql.engine.SemanticException;

import static org.junit.Assert.*;

public class IndexManagementTest {

        private SQLDataSourceFactory dsf;
        private IndexManager im;

        @Before
        public void setUp() throws Exception {
                dsf = new SQLDataSourceFactory();
                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                dsf.setResultDir(SQLBaseTest.backupDir);
                SourceManager sm = dsf.getSourceManager();
                sm.removeAll();
                sm.register("source", new File(SQLBaseTest.internalData,
                        "hedgerow.shp"));
                im = dsf.getIndexManager();
        }

        @Test
        public void testDeleteIndex() throws Exception {
                testBuildSpatialIndexOnFirstFieldByDefault();
                String sql = "select DeleteIndex(the_geom) from source";
                dsf.executeSQL(sql);
                assertNull(im.getIndex("source", "the_geom"));
        }

        @Test
        public void testBuildSpatialIndexSpecifyingField() throws Exception {
                testBuildIndexSpecifyingField("BuildSpatialIndex", "the_geom");
        }

        @Test
        public void testBuildAlphaIndexSpecifyingField() throws Exception {
                testBuildIndexSpecifyingField("BuildAlphaIndex", "gid");
        }

        @Test
        public void testBuildSpatialIndexOnFirstFieldByDefault() throws Exception {
                testBuildIndexOnFirstFieldByDefault("BuildSpatialIndex", "the_geom");
        }

        @Test
        public void testSpatialWrongParameters() throws Exception {
                testSpatialWrongParameters("BuildSpatialIndex", "the_geom", "gid");
        }

        @Test
        public void testAlphaWrongParameters() throws Exception {
                testWrongParameters("BuildAlphaIndex", "gid", "the_geom");
        }

        @Test
        public void testDeleteWrongParameters() throws Exception {
                testWrongParametersDelete("DeleteIndex", "gid");
        }

        private void testBuildIndexSpecifyingField(String indexCall, String field)
                throws Exception {
                String sql = "select " + indexCall + "(" + field + ") from source";
                dsf.executeSQL(sql);
                assertNotNull(im.getIndex("source", field));
        }

        private void testBuildIndexOnFirstFieldByDefault(String indexCall,
                String field) throws Exception {
                String sql = "select " + indexCall + "() from source";
                dsf.executeSQL(sql);
                assertNotNull(im.getIndex("source", field));
        }

        private void testSpatialWrongParameters(String indexCall, String field,
                String wrongField) throws Exception {
                try {
                        testWrongParametersInSQL("select " + indexCall + "(" + "'" + field
                                + "'" + ") from source;");
                        fail();
                } catch (IncompatibleTypesException e) {
                }
                testWrongParameters(indexCall, field, wrongField);
        }

        private void testWrongParameters(String indexCall, String field,
                String wrongField) throws Exception {
                try {
                        testWrongParametersInSQL("select " + indexCall + "(" + wrongField
                                + ") from source;");
                        fail();
                } catch (IncompatibleTypesException e) {
                }
                testWrongParametersDelete(indexCall, field);
        }

        private void testWrongParametersDelete(String indexCall, String field)
                throws Exception {
                try {
                        testWrongParametersInSQL("select " + indexCall + "(" + field
                                + ") from source s1, source s2;");
                        fail();
                } catch (SemanticException e) {
                }
                try {
                        testWrongParametersInSQL("select " + indexCall + "();");
                        fail();
                } catch (SemanticException e) {
                }
        }

        private void testWrongParametersInSQL(String sql) throws Exception {
                SQLEngine engine = new SQLEngine(dsf);
                engine.execute(sql);
        }
}
