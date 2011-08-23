package org.gdms;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.file.FileSourceCreation;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryTypeConstraint;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.Driver;
import org.gdms.driver.csv.CSVDriver;
import org.gdms.driver.dbf.DBFDriver;
import org.gdms.driver.h2.H2spatialDriver;
import org.gdms.driver.hsqldb.HSQLDBDriver;
import org.gdms.driver.postgresql.PostgreSQLDriver;
import org.gdms.driver.shapefile.ShapefileDriver;
import org.gdms.driver.solene.CirDriver;
import org.gdms.source.SourceManager;
import org.gdms.spatial.SeveralSpatialFieldsDriver;
import org.junit.BeforeClass;

/**
 *
 * @author Antoine Gourlay
 */
public abstract class SQLBaseTest extends SourceTest<Value, Geometry> {

        public static SQLDataSourceFactory dsf;
        private static boolean loaded = false;
        private static String fname = "name";
        private static String frowCount = "rowCount";
        private static String fisDB = "isDB";
        private static String fnoPKField = "noPKField";
        private static String fhasRepeatedRows = "hasRepeatedRows";
        private static String fpkField = "pkField";
        private static String fpkType = "pkType";
        private static String fnewPK = "newPK";
        private static String fstringField = "stringField";
        private static String fnullField = "nullField";
        private static String fnumericFieldName = "numericFieldName";
        private static String fmin = "min";
        private static String fmax = "max";
        private static String fspatialField = "spatialField";
        private static String fnewGeometry = "newGeometry";
        private static String fwrite = "write";
        public static boolean postGisAvailable;
        public static boolean h2Available;
        public static boolean hsqlDbAvailable;
        private static final Logger LOG = Logger.getLogger(SQLBaseTest.class);

        static {
                BasicConfigurator.configure();
                Logger.getRootLogger().setLevel(Level.INFO);
                ((Appender) Logger.getRootLogger().getAllAppenders().nextElement()).setLayout(new PatternLayout("%d %-5p %c - %m%n"));
        }

        @BeforeClass
        public static void setUpClass() {
                if (!loaded) {
                        try {
                                LOG.info("Initializing test resources.");
                                DriverManager.setLoginTimeout(1);

                                dsf = new SQLDataSourceFactory();
                                dsf.setTempDir(SQLBaseTest.backupDir.getAbsolutePath());
                                dsf.setResultDir(SQLBaseTest.backupDir);

                                toTest.add(new FileTestSource("hedgerow", internalData
                                        + "hedgerow.shp"));
                                toTest.add(new FileTestSource("landcover2000dbf", internalData
                                        + "landcover2000.dbf"));

                                toTest.add(new FileTestSource(SHPTABLE, internalData
                                        + "landcover2000.shp"));
                                toTest.add(new ObjectTestSource("memory_spatial_object",
                                        new SeveralSpatialFieldsDriver()));
                                final DBTestSource dBTestH2 = new DBTestSource("testh2", "org.h2.Driver", internalData
                                        + "testh2.sql", new DBSource(null, 0, internalData
                                        + "backup/testh2", "sa", "", "POINT", "jdbc:h2"));
                                h2Available = dBTestH2.isConnected();

                                if (h2Available) {
                                        LOG.info("H2 database available.");
                                        toTest.add(dBTestH2);
                                } else {
                                        LOG.warn("H2 database not available!!");
                                        LOG.warn("Skipping H2 DB tests.");
                                }
                                final DBTestSource dBTestHsqlDb = new DBTestSource("testhsqldb", "org.hsqldb.jdbcDriver",
                                        internalData + "testhsqldb.sql", new DBSource(null, 0,
                                        internalData + "backup/testhsqldb", "sa", "",
                                        "gisapps", "jdbc:hsqldb:file"));
                                hsqlDbAvailable = dBTestHsqlDb.isConnected();

                                if (hsqlDbAvailable) {
                                        LOG.info("HsqlDb database available.");
                                        toTest.add(dBTestHsqlDb);
                                } else {
                                        LOG.warn("HsqlDb database not available!!");
                                        LOG.warn("Skipping HsqlDb DB tests.");
                                }

                                toTest.add(new FileTestSource("testcsv", internalData
                                        + "test.csv"));
                                toTest.add(new FileTestSource("repeatedRows", internalData
                                        + "repeatedRows.csv"));
                                toTest.add(new SQLTestSource("select_source", internalData
                                        + "repeatedRows.csv"));

                                final DBTestSource dBTestPostGIS = new DBTestSource("pghedgerow", "org.postgresql.Driver",
                                        internalData + "hedgerow.sql", new DBSource("127.0.0.1",
                                        -1, "gdms", "postgres", "postgres", "hedgerow",
                                        "jdbc:postgresql"));
                                postGisAvailable = dBTestPostGIS.isConnected();

                                if (postGisAvailable) {
                                        LOG.info("PostGIS database available.");
                                        toTest.add(dBTestPostGIS);
                                        toTest.add(new DBTestSource("postgres", "org.postgresql.Driver",
                                                internalData + "testpostgres.sql", new DBSource(
                                                "127.0.0.1", -1, "gdms", "postgres", "postgres",
                                                "gisapps", "jdbc:postgresql")));
                                } else {
                                        LOG.warn("PostGIS database not available!!");
                                        LOG.warn("Skipping PostGIS DB tests.");
                                }

                                if (!testDataInfo.exists()) {
                                        createDB();
                                }

                                testMetaData = readDataInfo();

                        } catch (Exception e) {
                                testDataInfo.delete();
                                LOG.error("Error initializing", e);
                        }
                        loaded = true;
                }
        }

        /**
         * If the test is going to write creates a backup and adds the backup to the
         * DataSourceFactory
         *
         * @param testSource
         *
         * @return The name of the backup in the DataSourceFactory
         * @throws IOException
         */
        @Override
        protected void backup(TestSource testSource) throws Exception {
                backupDir.mkdirs();
                SourceManager sourceManager = dsf.getSourceManager();
                if (sourceManager.exists(testSource.name)) {
                        sourceManager.remove(testSource.name);
                }
                testSource.backup();
        }

        protected String[] getFieldNames(String ds) throws NoSuchTableException,
                DataSourceCreationException, DriverException {
                DataSource d = dsf.getDataSource(ds);
                d.open();
                String[] fields = d.getFieldNames();
                d.close();
                return fields;
        }

        public void setUp() throws Exception {
                dsf.getSourceManager().removeAll();
        }

        public void tearDown() throws Exception {
                dsf.freeResources();
        }

        protected static void createDB() throws Exception {
                LOG.info("Creating the list of available sources.");
                ArrayList<TestSourceData> sources = new ArrayList<TestSourceData>();
                sources.add(new TestSourceData("hedgerow", null, false));
                sources.add(new TestSourceData("landcover2000dbf", null, false));
                sources.add(new TestSourceData("memory_spatial_object", null, false));
                sources.add(new TestSourceData(SHPTABLE, null, false));
                if (h2Available) {
                        sources.add(new TestSourceData("testh2", null, false));
                }
                if (hsqlDbAvailable) {
                        sources.add(new TestSourceData("testhsqldb", "version", false));
                }
                sources.add(new TestSourceData("testcsv", null, false));
                sources.add(new TestSourceData("repeatedRows", null, true));
                sources.add(new TestSourceData("select_source", null, false));
                if (postGisAvailable) {
                        sources.add(new TestSourceData("pghedgerow", null, false));
                        sources.add(new TestSourceData("postgres", null, false));
                }
                createTestDataInfo(sources);
                LOG.info("Created the list of available sources.");
        }

        private static void createTestDataInfo(ArrayList<TestSourceData> sources)
                throws Exception {
                LOG.info("Creating " + sources.size() + " sources.");
                DefaultMetadata m = new DefaultMetadata();
                m.addField(fname, Type.STRING);
                m.addField(frowCount, Type.STRING);
                m.addField(fisDB, Type.STRING);
                m.addField(fnoPKField, Type.STRING);
                m.addField(fhasRepeatedRows, Type.STRING);//
                m.addField(fpkField, Type.STRING);
                m.addField(fpkType, Type.STRING);
                m.addField(fnewPK, Type.STRING);
                m.addField(fstringField, Type.STRING);
                m.addField(fnullField, Type.STRING); //
                m.addField(fnumericFieldName, Type.STRING);
                m.addField(fmin, Type.STRING);
                m.addField(fmax, Type.STRING);
                m.addField(fspatialField, Type.STRING);
                m.addField(fnewGeometry, Type.STRING);
                m.addField(fwrite, Type.STRING);

                testDataInfo.delete();
                DataSourceCreation creation = new FileSourceCreation(testDataInfo, m);
                dsf.createDataSource(creation);
                DataSource ds = dsf.getDataSource(testDataInfo);
                ds.open();
                LOG.info("Metadata size: " + ds.getMetadata().getFieldCount());
                for (int i = 0; i < sources.size(); i++) {
                        try {

                                TestSourceData sourceData = sources.get(i);
                                getTestSource(sourceData.name).backup();
                                LOG.info(sourceData.name);
                                DataSource testData = dsf.getDataSource(sourceData.name, DataSourceFactory.STATUS_CHECK);
                                testData.open();
                                ds.insertEmptyRow();
                                long row = ds.getRowCount() - 1;
                                ds.setString(row, fname, sourceData.name);
                                ds.setString(row, frowCount, Long.toString(testData.getRowCount()));

                                Driver driverName = testData.getDriver();
                                if ((driverName instanceof H2spatialDriver)
                                        || (driverName instanceof PostgreSQLDriver)
                                        || (driverName instanceof HSQLDBDriver)) {
                                        ds.setString(row, fisDB, "true");
                                } else {
                                        ds.setString(row, fisDB, "false");
                                }
                                String pkField = null;
                                int pkType = -1;
                                String newPK = null;
                                String noPKField = null;
                                String stringField = null;
                                String numericField = null;
                                String spatialField = null;
                                int geometryType = -1;
                                int dimension = 2;
                                for (int j = 0; j < testData.getFieldCount(); j++) {
                                        Type fieldType = testData.getFieldType(j);
                                        Type type = fieldType;
                                        String fieldName = testData.getFieldName(j);
                                        // TODO This is due to a bug in the
                                        // parser. Remove when the bug is solved
                                        if (fieldName.startsWith("_")) {
                                                continue;
                                        }
                                        if (type.getConstraint(Constraint.PK) != null) {
                                                if (pkField == null) {
                                                        pkField = fieldName;
                                                        pkType = fieldType.getTypeCode();
                                                        newPK = getPKFor(testData, testData.getFieldIndexByName(fieldName));
                                                }
                                        } else if (fieldType.getTypeCode() != Type.GEOMETRY) {
                                                noPKField = fieldName;
                                        }

                                        int typeCode = type.getTypeCode();

                                        switch (typeCode) {
                                                case Type.STRING:
                                                        stringField = fieldName;
                                                        break;
                                                case Type.BYTE:
                                                case Type.INT:
                                                case Type.LONG:
                                                case Type.SHORT:
                                                        numericField = fieldName;
                                                        break;
                                                case Type.GEOMETRY:
                                                        spatialField = fieldName;
                                                        GeometryTypeConstraint c = (GeometryTypeConstraint) fieldType.getConstraint(Constraint.GEOMETRY_TYPE);
                                                        if (c != null) {
                                                                geometryType = c.getGeometryType();
                                                        }
                                                        DimensionConstraint dc = (DimensionConstraint) fieldType.getConstraint(Constraint.GEOMETRY_DIMENSION);
                                                        if (dc != null) {
                                                                dimension = dc.getDimension();
                                                        }
                                                        break;
                                        }
                                }
                                ds.setString(row, fnoPKField, noPKField);
                                ds.setString(row, fpkField, pkField);
                                ds.setString(row, fpkType, Integer.toString(pkType));
                                ds.setString(row, fnewPK, newPK);
                                ds.setString(row, fstringField, stringField);
                                ds.setString(row, fnumericFieldName, numericField);
                                ds.setString(row, fspatialField, spatialField);
                                WKTWriter writer = new WKTWriter();
                                if (geometryType == -1) {
                                        ds.setString(row, fnewGeometry, writer.write(Geometries.getPoint()));
                                } else {
                                        ds.setString(row, fnewGeometry, writer.write(Geometries.getGeometry(geometryType, dimension)));
                                }

                                if ((driverName instanceof DBFDriver)
                                        || (driverName instanceof ShapefileDriver)
                                        || (driverName instanceof HSQLDBDriver)
                                        || (driverName instanceof H2spatialDriver)
                                        || (driverName instanceof PostgreSQLDriver)
                                        || (driverName instanceof CSVDriver)
                                        || (driverName instanceof CirDriver)) {
                                        ds.setString(row, fwrite, "true");
                                } else {
                                        ds.setString(row, fwrite, "false");
                                }

                                if (numericField != null) {
                                        int min = Integer.MAX_VALUE;
                                        int max = Integer.MIN_VALUE;
                                        for (int j = 0; j < testData.getRowCount(); j++) {
                                                int value = testData.getInt(j, numericField);
                                                if (value < min) {
                                                        min = value;
                                                }
                                                if (value > max) {
                                                        max = value;
                                                }
                                        }
                                        ds.setString(row, fmin, Integer.toString(min));
                                        ds.setString(row, fmax, Integer.toString(max));
                                }

                                ds.setString(row, fhasRepeatedRows, Boolean.toString(sourceData.repeatedRows));
                                ds.setString(row, fnullField, sourceData.nullField);
                                testData.close();
                                LOG.info("Traited source " + sourceData.name);
                        } catch (Exception e) {
                                LOG.error("Error in loop, for source " + sources.get(i).name, e);
                        }
                }
                ds.commit();
                ds.close();
        }

        private static List<TestData> readDataInfo() throws Exception {
                ArrayList<TestData> testData = new ArrayList<TestData>();
                DataSource info = dsf.getDataSource(testDataInfo);
                info.open();
                for (int i = 0; i < info.getRowCount(); i++) {
                        GdmsTestData td = new GdmsTestData(info.getString(i, fname), Boolean.parseBoolean(info.getString(i, fwrite)), 0, Long.parseLong(info.getString(i, frowCount)), Boolean.parseBoolean(info.getString(i, fisDB)), info.getString(i,
                                fnoPKField), Boolean.parseBoolean(info.getString(i,
                                fhasRepeatedRows)));
                        String value = info.getString(i, fspatialField);
                        if (value != null) {
                                WKTReader reader = new WKTReader();
                                td.setNewGeometry(value, new Geometry[]{reader.read(info.getString(i, fnewGeometry))});
                        }

                        td.setNullField(info.getString(i, fnullField));

                        value = info.getString(i, fnumericFieldName);
                        if (value != null) {
                                td.setNumericInfo(value, Integer.parseInt(info.getString(i,
                                        fmin)), Integer.parseInt(info.getString(i, fmax)));
                        }

                        value = info.getString(i, fpkField);
                        if (value != null) {
                                String pk = info.getString(i, fnewPK);
                                String type = info.getString(i, fpkType);
                                td.setPKInfo(value, ValueFactory.createValueByType(pk, Integer.parseInt(type)));
                        }

                        td.setStringField(info.getString(i, fstringField));

                        testData.add(td);
                }

                return testData;
        }

        private static String getPKFor(DataSource testData, int fieldId)
                throws Exception {
                Value max = null;
                for (int i = 0; i < testData.getRowCount(); i++) {
                        Value sampleValue = testData.getFieldValue(i, fieldId);
                        if (max == null) {
                                max = sampleValue;
                        } else if (sampleValue.greater(max).getAsBoolean()) {
                                max = sampleValue;
                        }
                }

                if (max == null) {
                        return "1";
                } else {
                        return max.sum(ValueFactory.createValue(1)).toString();
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
}
