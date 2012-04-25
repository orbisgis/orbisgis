/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.orbisgis.utils.FileUtils;

import static org.gdms.TestResourceHandler.*;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.db.DBSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;

public abstract class TestBase {

        protected DataSourceFactory dsf;
        protected SourceManager sm;
        private static boolean loadedDb = false;
        public static boolean postGisAvailable;
        public static boolean hsqlDbAvailable;
        private static final Logger LOG = Logger.getLogger(TestBase.class);
        private static final Set<DBTestSource> DBS = new HashSet<DBTestSource>();
        protected File currentWorkspace = TESTRESOURCES;
        private boolean withEdition = false;

        @BeforeClass
        public static void setUpClass() {
                // main init
                TestResourceHandler.init();

                if (!loadedDb) {
                        LOG.info("Initializing test resources.");
                        DriverManager.setLoginTimeout(1);

                        final DBTestSource dBTestHsqlDb = new DBTestSource("testhsqldb", "org.hsqldb.jdbcDriver",
                                OTHERRESOURCES.getPath() + "testhsqldb.sql", new DBSource(null, 0,
                                OTHERRESOURCES.getPath() + "testhsqldb", "sa", "",
                                "gisapps", "jdbc:hsqldb:file"));
                        try {
                                hsqlDbAvailable = dBTestHsqlDb.isConnected();
                        } catch (Exception ex) {
                                LOG.error("Failed to test for HsqlDb", ex);
                        }

                        if (hsqlDbAvailable) {
                                LOG.info("HsqlDb database available.");
                                DBS.add(dBTestHsqlDb);
                        } else {
                                LOG.warn("HsqlDb database not available!!");
                                LOG.warn("Skipping HsqlDb DB tests.");
                        }

                        final DBTestSource dBTestPostGIS = new DBTestSource("pghedgerow", "org.postgresql.Driver",
                                OTHERRESOURCES.getPath() + "hedgerow.sql", new DBSource("127.0.0.1",
                                -1, "gdms", "postgres", "postgres", "hedgerow",
                                "jdbc:postgresql"));
                        try {
                                postGisAvailable = dBTestPostGIS.isConnected();
                        } catch (Exception ex) {
                                LOG.error("Failed to test for PostGis", ex);
                        }

                        if (postGisAvailable) {
                                LOG.info("PostGIS database available.");
                                DBS.add(dBTestPostGIS);
                                DBS.add(new DBTestSource("postgres", "org.postgresql.Driver",
                                        OTHERRESOURCES.getPath() + "testpostgres.sql", new DBSource(
                                        "127.0.0.1", -1, "gdms", "postgres", "postgres",
                                        "gisapps", "jdbc:postgresql")));
                        } else {
                                LOG.warn("PostGIS database not available!!");
                                LOG.warn("Skipping PostGIS DB tests.");
                        }
                        loadedDb = true;
                }

        }

        protected String[] getFieldNames(String ds) throws NoSuchTableException,
                DataSourceCreationException, DriverException {
                DataSource d = dsf.getDataSource(ds);
                d.open();
                String[] fields = d.getFieldNames();
                d.close();
                return fields;
        }

        public void setUpTestsWithoutEdition() throws Exception {
                withEdition = false;
                currentWorkspace = TESTRESOURCES;
                File workspace = getNewSandBox(false);
                final File dir = new File(workspace, "/directory/");
                dir.mkdirs();
                dsf = new DataSourceFactory(dir.getAbsolutePath());
                final File temp = new File(workspace, "/temp/");
                temp.mkdirs();
                dsf.setTempDir(temp.getAbsolutePath());
                final File res = new File(workspace, "/result/");
                res.mkdirs();
                dsf.setResultDir(res);
                dsf.getSqlEngine().getProperties().put("output.explain", "true");
                sm = dsf.getSourceManager();
                sm.removeAll();
        }

        public void setUpTestsWithEdition(boolean withData) throws Exception {
                withEdition = withData;
                currentWorkspace = getNewSandBox(withData);
                final File dir = new File(currentWorkspace, "/directory/");
                dir.mkdirs();
                dsf = new DataSourceFactory(dir.getAbsolutePath());
                final File temp = new File(currentWorkspace, "/temp/");
                temp.mkdirs();
                dsf.setTempDir(temp.getAbsolutePath());
                final File res = new File(currentWorkspace, "/result/");
                res.mkdirs();
                dsf.setResultDir(res);
                dsf.getSqlEngine().getProperties().put("output.explain", "true");
                sm = dsf.getSourceManager();
                sm.removeAll();
        }

        @After
        public void tearDown() throws Exception {
                if (dsf != null) {
                        dsf.freeResources();

                        if (!currentWorkspace.equals(TESTRESOURCES)) {
                                FileUtils.deleteDir(currentWorkspace);
                        }
                }
        }

        /**
         * Gets the contents of the DataSource
         *
         * @param ds
         * @return
         * @throws DriverException
         */
        public Value[][] getDataSourceContents(DataSource ds)
                throws DriverException {
                Value[][] ret = new Value[(int) ds.getRowCount()][ds.getMetadata().getFieldCount()];
                for (int i = 0; i < ret.length; i++) {
                        for (int j = 0; j < ret[i].length; j++) {
                                ret[i][j] = ds.getFieldValue(i, j);
                        }
                }

                return ret;
        }

        /**
         * Compares the two values for testing purposes. This means that two null
         * values are always equal though its equals method returns always false
         *
         * @param v1
         * @param v2
         * @return
         */
        public static boolean equals(Value v1, Value v2) {
                if (v1.isNull()) {
                        return v2.isNull();
                } else {
                        try {
                                return v1.equals(v2).getAsBoolean();
                        } catch (IncompatibleTypesException e) {
                                throw new RuntimeException(e);
                        }
                }
        }

        /**
         * Compares the two arrays of values for testing purposes. This means that
         * two null values are always equal though its equals method returns always
         * false
         *
         * @param row1
         * @param row2
         * @return
         */
        public static boolean equals(Value[] row1, Value[] row2) {
                for (int i = 0; i < row2.length; i++) {
                        if (!equals(row1[i], row2[i])) {
                                return false;
                        }
                }

                return true;
        }

        /**
         * The same as the equals(Value[] row1, Value[] row2) version but it doesn't
         * compares the READ_ONLY fields
         *
         * @param row1
         * @param row2
         * @param metadata
         * @return
         * @throws DriverException
         */
        public static boolean equals(Value[] row1, Value[] row2, Metadata metadata)
                throws DriverException {
                for (int i = 0; i < row2.length; i++) {
                        if (metadata.getFieldType(i).getConstraint(Constraint.READONLY) == null) {
                                if (!equals(row1[i], row2[i])) {
                                        return false;
                                }
                        }
                }

                return true;
        }

        /**
         * Compares the two arrays of values for testing purposes. This means that
         * two null values are always equal though its equals method returns always
         * false
         *
         * @param content1
         * @param content2
         * @return
         */
        public static boolean equals(Value[][] content1, Value[][] content2) {
                for (int i = 0; i < content1.length; i++) {
                        if (!equals(content1[i], content2[i])) {
                                return false;
                        }
                }

                return true;
        }

        public File getAnyNonSpatialResource() {
                return new File(withEdition ? currentWorkspace : TESTRESOURCES, getAlphanumericFiles().iterator().next());
        }

        public File getAnySpatialResource() {
                return new File(withEdition ? currentWorkspace : TESTRESOURCES, getGeometricFiles().iterator().next());
        }

        public File getTempCopyOf(File f) throws IOException {
                File p = dsf.getResultDir().getParentFile();
                File dest = File.createTempFile("temp", f.getName(), p);
                dest.delete();
                if (f.getName().toLowerCase().endsWith(".shp")) {
                        String destbase = FileUtils.getFileNameWithoutExtensionU(dest);
                        FileUtils.copy(FileUtils.getFileWithExtension(f, "dbf"), new File(dest.getParent(), destbase + ".dbf"));
                        FileUtils.copy(FileUtils.getFileWithExtension(f, "shx"), new File(dest.getParent(), destbase + ".shx"));
                }

                FileUtils.copy(f, dest);
                dest.deleteOnExit();
                return dest;
        }

        public File getTempFile(String ending) throws IOException {
                File p = dsf.getResultDir().getParentFile();
                File dest = File.createTempFile("temp", ending, p);
                dest.delete();
                dest.deleteOnExit();
                return dest;
        }

        public static Set<DBTestSource> getDBTestSources() {
                return DBS;
        }
        
        
}
