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
package org.gdms.driver.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gdms.data.WarningListener;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DimensionConstraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.TableDescription;
import org.gdms.driver.jdbc.AutonumericRule;
import org.gdms.driver.jdbc.BooleanRule;
import org.gdms.driver.jdbc.ConversionRule;
import org.gdms.driver.jdbc.DateRule;
import org.gdms.driver.jdbc.DefaultDBDriver;
import org.gdms.driver.jdbc.StringRule;
import org.gdms.driver.jdbc.TimeRule;
import org.gdms.driver.jdbc.TimestampRule;
import org.gdms.source.SourceManager;
import org.postgis.jts.JtsBinaryParser;
import org.postgresql.PGConnection;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;
import org.gdms.data.types.SRIDConstraint;

/**
 *
 */
public class PostgreSQLDriver extends DefaultDBDriver {

        public static final String DRIVER_NAME = "postgresql";
        private static Exception driverException;
        private static JtsBinaryParser parser = new JtsBinaryParser();

        static {
                try {
                        Class.forName("org.postgresql.Driver").newInstance();
                } catch (Exception ex) {
                        driverException = ex;
                }
        }
        private Set<String> geometryFields;
        private ArrayList<String> fields;
        private HashMap<String, String> geometryTypes;
        private HashMap<String, Integer> geometryDimensions;
        private Window wnd;
        private int rowCount;
        private boolean isPostGISTable;
        private int srid;

        /**
         *
         *
         * @param host
         *            for the database.
         * @param port
         *            for the database. By default is 5432.
         * @param dbName
         *            for the database
         * @param user
         *            name for the database
         * @param password
         *            for the database
         *
         * @return a JDBC connection
         *
         * @throws SQLException
         * @throws RuntimeException
         *
         *
         * @see org.gdms.driver.DBDriver#connect(java.lang.String)
         */
        public Connection getConnection(String host, int port, String dbName,
                String user, String password) throws SQLException {
                if (driverException != null) {
                        throw new RuntimeException(driverException);
                }

                String connectionString = "jdbc:postgresql://" + host;

                if (port != -1) {
                        connectionString += (":" + port);
                }

                connectionString += ("/" + dbName);

                if (user != null) {
                        connectionString += ("?user=" + user + "&password=" + password);
                }

                Connection c = DriverManager.getConnection(connectionString);
                ((PGConnection) c).addDataType("geometry", org.postgis.PGgeometry.class);
                ((PGConnection) c).addDataType("box3d", org.postgis.PGbox3d.class);

                return c;
        }

        @Override
        public long getRowCount() throws DriverException {
                return rowCount;
        }

        @Override
        public void open(Connection con, String tableName) throws DriverException {
                open(con, tableName, null);
        }

        @Override
        public void open(Connection con, String tableName, String schemaName)
                throws DriverException {
                this.tableName = tableName;
                this.schemaName = schemaName;

                try {
                        geometryFields = new HashSet<String>();
                        geometryTypes = new HashMap<String, String>();
                        geometryDimensions = new HashMap<String, Integer>();
                        fields = new ArrayList<String>();
                        Statement st = con.createStatement();
                        ResultSet res = null;
                        if (schemaName != null) {
                                if (schemaName.length() != 0) {
                                        res = st.executeQuery("select * from \"geometry_columns\"" + " where \"f_table_schema\" = '" + schemaName + "' and \"f_table_name\" = '" + tableName + "'");
                                }
                        }
                        if (res == null) {
                                res = st.executeQuery("select * from \"geometry_columns\""
                                        + " where \"f_table_name\" = '" + tableName + "'");
                        }

                        while (res.next()) {
                                isPostGISTable = true;
                                String geomFieldName = res.getString("f_geometry_column");
                                int tempSrid = res.getInt("srid");
                                if (tempSrid != 0) {
                                        srid = tempSrid;
                                }
                                geometryFields.add(geomFieldName);
                                geometryTypes.put(geomFieldName, res.getString("type"));
                                int dim = res.getInt("coord_dimension");
                                if ((dim != 2) && (dim != 3)) {
                                        getWL().throwWarning(
                                                "Dimension of " + geomFieldName + " is wrong: "
                                                + dim);
                                        if (this.schemaName == null) {
                                                this.schemaName = res.getString("f_table_schema");
                                        }
                                }
                                geometryDimensions.put(geomFieldName, dim);
                                srid = res.getInt("srid");

                        }
                        res.close();
                        res = st.executeQuery("select * from " + getTableAndSchemaName()
                                + " where false");
                        ResultSetMetaData metadata = res.getMetaData();
                        if (!isPostGISTable) {
                                boolean geometryFounded = false;
                                for (int i = 0; (i < metadata.getColumnCount() || !geometryFounded); i++) {
                                        if (metadata.getColumnTypeName(i + 1).equals("geometry")) {
                                                geometryFounded = true;
                                                String geomFieldName = metadata.getColumnName(i + 1);
                                                geometryFields.add(geomFieldName);
                                                geometryTypes.put(geomFieldName, "GEOMETRY");
                                                geometryDimensions.put(geomFieldName, Type.GEOMETRY);
                                        }
                                }
                        }

                        for (int i = 0; i < metadata.getColumnCount(); i++) {
                                fields.add(metadata.getColumnName(i + 1));
                        }

                        res.close();
                        res = st.executeQuery("select count(*) from "
                                + getTableAndSchemaName() + ";");
                        res.next();
                        rowCount = res.getInt(1);
                        res.close();
                        st.close();

                } catch (SQLException e) {
                        throw new DriverException(e);
                }
                wnd = new Window(0);
                super.open(con, tableName, this.schemaName);
        }

        @Override
        protected String getSelectSQL(String orderFieldName) throws DriverException {
                String sql = "SELECT * FROM " + getTableAndSchemaName();
                if (orderFieldName != null) {
                        sql += " ORDER BY " + orderFieldName;
                }
                sql += " OFFSET " + wnd.offset + " LIMIT " + wnd.length;
                return sql;
        }

        @Override
        public TableDescription[] getTables(Connection c, String catalog,
                String schemaPattern, String tableNamePattern, String[] types)
                throws DriverException {
                TableDescription[] tableDescriptions = super.getTables(c, catalog,
                        schemaPattern, tableNamePattern, types);

                // Retrieves the PostGIS geometryType of each Database.
                Statement st;
                ResultSet res = null;
                try {
                        st = c.createStatement();
                        res = st.executeQuery("select * from \"geometry_columns\";");

                        while (res.next()) {
                                for (int i = 0; i < tableDescriptions.length; i++) {
                                        if (res.getString("f_table_name").equals(
                                                tableDescriptions[i].getName())
                                                && res.getString("f_table_schema").equals(
                                                tableDescriptions[i].getSchema())) {
                                                int srid = res.getInt("srid");
                                                if (srid == 0) {
                                                        srid = -1;
                                                }
                                                tableDescriptions[i].setSrid(srid);

                                                String geomType = res.getString("type");
                                                if (geomType.equals("MULTIPOLYGON")
                                                        || geomType.equals("MULTIPOLYGONM")) {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.MULTI_POLYGON);
                                                } else if (geomType.equals("POLYGON")
                                                        || geomType.equals("POLYGONM")) {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.POLYGON);
                                                } else if (geomType.equals("POINT")
                                                        || geomType.equals("POINTM")) {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.POINT);
                                                } else if (geomType.equals("LINESTRING")
                                                        || geomType.equals("LINESTRINGM")) {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.LINESTRING);
                                                } else if (geomType.equals("MULTILINESTRING")
                                                        || geomType.equals("MULTILINESTRINGM")) {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.MULTI_LINESTRING);
                                                } else if (geomType.equals("MULTIPOINT")
                                                        || geomType.equals("MULTIPOINTM")) {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.MULTI_POINT);
                                                } else // if the type is GEOMETRY or in any other case, set
                                                // the geometry type to ALL
                                                {
                                                        tableDescriptions[i].setGeometryType(GeometryConstraint.ALL);
                                                }
                                                break;
                                        }
                                }
                        }
                } catch (SQLException e) {
                        throw new DriverException(e);
                }

                // Else, search if there is a geometry object in each column of each
                // database
                // If a geometry object is found, the geometry type of the database is
                // set to ALL
                try {
                        for (int i = 0; i < tableDescriptions.length; i++) {
                                if (tableDescriptions[i].getGeometryType() == 0) {
                                        res = st.executeQuery("SELECT * FROM \""
                                                + tableDescriptions[i].getSchema() + "\".\""
                                                + tableDescriptions[i].getName() + "\" LIMIT 2 ;");
                                        ResultSetMetaData md = res.getMetaData();
                                        for (int column = 1; column <= md.getColumnCount(); column++) {
                                                if (md.getColumnType(column) == java.sql.Types.OTHER) {
                                                        res.next();
                                                        Object object = res.getObject(column);
                                                        if (object instanceof org.postgis.PGgeometry) {
                                                                tableDescriptions[i].setGeometryType(GeometryConstraint.ALL);
                                                        }

                                                        break;
                                                }
                                        }

                                }
                        }
                } catch (SQLException e) {
                        throw new DriverException(e);
                }

                return tableDescriptions;
        }

        @Override
        protected Type getGDMSType(ResultSetMetaData resultsetMetadata,
                List<String> pkFieldsList, int jdbcFieldIndex) throws SQLException,
                InvalidTypeException, DriverException {
                String fieldName = resultsetMetadata.getColumnName(jdbcFieldIndex);
                if (geometryFields.contains(fieldName)) {
                        String geometryType = geometryTypes.get(fieldName);
                        int geometryDimension = geometryDimensions.get(fieldName);

                        return TypeFactory.createType(Type.GEOMETRY, getConstraints(
                                fieldName, geometryType, geometryDimension, getWL()));
                } else {
                        return super.getGDMSType(resultsetMetadata, pkFieldsList,
                                jdbcFieldIndex);
                }
        }

        private Constraint[] getConstraints(String fieldName, String geometryType,
                int geometryDimension, WarningListener wl) throws DriverException {
                GeometryConstraint gc;

                if ("POINT".equals(geometryType)) {
                        gc = new GeometryConstraint(GeometryConstraint.POINT);
                } else if ("MULTIPOINT".equals(geometryType)) {
                        gc = new GeometryConstraint(GeometryConstraint.MULTI_POINT);
                } else if ("LINESTRING".equals(geometryType)) {
                        gc = new GeometryConstraint(GeometryConstraint.LINESTRING);
                } else if ("MULTILINESTRING".equals(geometryType)) {
                        gc = new GeometryConstraint(GeometryConstraint.MULTI_LINESTRING);
                } else if ("POLYGON".equals(geometryType)) {
                        gc = new GeometryConstraint(GeometryConstraint.POLYGON);
                } else if ("MULTIPOLYGON".equals(geometryType)) {
                        gc = new GeometryConstraint(GeometryConstraint.MULTI_POLYGON);
                } else if ("GEOMETRY".equals(geometryType)) {
                        gc = null;
                } else {
                        wl.throwWarning("Unrecognized geometry type: " + geometryType
                                + ". Using 'MIXED'");
                        gc = null;
                }

                DimensionConstraint dc;
                if (geometryDimension == 2) {
                        dc = new DimensionConstraint(2);
                } else if (geometryDimension == 3) {
                        dc = new DimensionConstraint(3);
                } else {
                        dc = new DimensionConstraint(2);
                }

                Constraint sc;
                ArrayList<Constraint> cons = new ArrayList<Constraint>();
                if (srid != -1) {
                        cons.add(new SRIDConstraint(srid));
                }


                if (gc != null) {
                        cons.add(gc);
                }
                cons.add(dc);
                return cons.toArray(new Constraint[cons.size()]);
        }

        /**
         * @see com.hardcode.driverManager.Driver#getDriverId()
         */
        public String getDriverId() {
                return DRIVER_NAME;
        }

        @Override
        public String[] getPrefixes() {
                return new String[]{"jdbc:postgresql"};
        }

        public Number[] getScope(int dimension) throws DriverException {
                return null;
        }

        @Override
        public String getStatementString(byte[] binary) {
                StringBuilder sb = new StringBuilder("'");
                for (int i = 0; i < binary.length; i++) {
                        int byte_ = binary[i];
                        if (byte_ < 0) {
                                byte_ += 256;
                        }
                        String b = Integer.toOctalString(byte_);
                        if (b.length() == 1) {
                                sb.append("\\\\00").append(b);
                        } else if (b.length() == 2) {
                                sb.append("\\\\0").append(b);
                        } else {
                                sb.append("\\\\").append(b);
                        }

                }
                sb.append("'");

                return sb.toString();
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId)
                throws DriverException {
                wnd.moveTo(rowIndex);
                rowIndex = rowIndex - wnd.offset;
                if (geometryFields.contains(super.getMetadata().getFieldName(fieldId))) {
                        try {
                                fieldId += 1;
                                ResultSet rs = getResultSet();
                                rs.absolute((int) rowIndex + 1);
                                String bytes = rs.getString(fieldId);
                                if (rs.wasNull()) {
                                        return ValueFactory.createNullValue();
                                } else {
                                        Geometry geom = parser.parse(bytes);
                                        return ValueFactory.createValue(geom);
                                }
                        } catch (SQLException e) {
                                getWL().throwWarning(
                                        "Cannot get value: " + e.getMessage()
                                        + ". Returning null instead.");
                                return ValueFactory.createNullValue();
                        }
                } else {
                        return super.getFieldValue(rowIndex, fieldId);
                }
        }

        @Override
        public String getAddFieldSQL(String fieldName, Type fieldType)
                throws DriverException {
                if (fieldType.getTypeCode() == Type.GEOMETRY) {
                        return getAddGeometryColumn(fieldName, fieldType);
                } else {
                        return super.getAddFieldSQL(fieldName, fieldType);
                }
        }

        @Override
        public String getPostCreateTableSQL(Metadata metadata)
                throws DriverException {
                String ret = "";
                for (int i = 0; i < metadata.getFieldCount(); i++) {
                        Type fieldType = metadata.getFieldType(i);
                        if (fieldType.getTypeCode() == Type.GEOMETRY) {
                                ret += getAddGeometryColumn(metadata.getFieldName(i), metadata.getFieldType(i));
                        }
                }

                return ret;
        }

        private String getAddGeometryColumn(String fieldName, Type fieldType)
                throws DriverException {
                Constraint geometryConstraint = fieldType.getConstraint(Constraint.GEOMETRY_TYPE);
                DimensionConstraint dimensionConstraint = (DimensionConstraint) fieldType.getConstraint(Constraint.GEOMETRY_DIMENSION);
                return "select AddGeometryColumn('" + schemaName + "', '" + tableName
                        + "', '" + fieldName + "', -1, '"
                        + getGeometryTypeName(geometryConstraint) + "', '"
                        + getGeometryDimension(dimensionConstraint) + "');";
        }

        private int getGeometryDimension(DimensionConstraint constraint) {
                if (constraint == null) {
                        return 2;
                } else {
                        switch (constraint.getDimension()) {
                                case 2:
                                        return 2;
                                case 3:
                                        return 3;
                                default:
                                        getWL().throwWarning(
                                                "Unknown dimension: " + constraint.getDimension());
                                        return 2;
                        }
                }
        }

        private String getGeometryTypeName(Constraint constraint) {
                if (constraint == null) {
                        return "GEOMETRY";
                } else {
                        GeometryConstraint gc = (GeometryConstraint) constraint;
                        if (gc == null) {
                                return "GEOMETRY";
                        } else {
                                switch (gc.getGeometryType()) {
                                        case GeometryConstraint.POINT:
                                                return "POINT";
                                        case GeometryConstraint.LINESTRING:
                                                return "LINESTRING";
                                        case GeometryConstraint.POLYGON:
                                                return "POLYGON";
                                        case GeometryConstraint.MULTI_POINT:
                                                return "MULTIPOINT";
                                        case GeometryConstraint.MULTI_LINESTRING:
                                                return "MULTILINESTRING";
                                        case GeometryConstraint.MULTI_POLYGON:
                                                return "MULTIPOLYGON";
                                        default:
                                                getWL().throwWarning(
                                                        "Bug in postgreSQL driver: "
                                                        + gc.getGeometryType());
                                                return "GEOMETRY";
                                }
                        }
                }
        }

        @Override
        public String getInsertSQL(String[] fieldNames, Type[] fieldTypes,
                Value[] row) throws DriverException {
                StringBuffer sql = new StringBuffer();
                sql.append("INSERT INTO ").append(getTableAndSchemaName()).append(
                        " (\"").append(fieldNames[0]);

                for (int i = 1; i < fieldNames.length; i++) {
                        sql.append("\", \"").append(fieldNames[i]);
                }

                sql.append("\") VALUES(");

                String separator = "";

                for (int i = 0; i < row.length; i++) {
                        if (isAutoNumerical(fieldTypes[i])) {
                                sql.append(separator).append(getAutoIncrementDefaultValue());
                        } else {
                                String fieldValue;
                                if ((fieldTypes[i].getTypeCode() == Type.GEOMETRY)
                                        && (row[i].getType() != Type.NULL)) {
                                        Geometry g = row[i].getAsGeometry();
                                        DimensionConstraint gc = (DimensionConstraint) fieldTypes[i].getConstraint(Constraint.GEOMETRY_DIMENSION);
                                        WKTWriter writer = new WKTWriter(getGeometryDimension(gc));
                                        fieldValue = "GeomFromText('" + writer.write(g) + "')";
                                } else {
                                        fieldValue = row[i].getStringValue(this);
                                }
                                sql.append(separator).append(fieldValue);
                        }
                        separator = ", ";
                }

                return sql.append(")").toString();
        }

        @Override
        public String getUpdateSQL(String[] pkNames, Value[] pkValues,
                String[] fieldNames, Type[] fieldTypes, Value[] row)
                throws DriverException {
                StringBuffer sql = new StringBuffer();
                sql.append("UPDATE ").append(getTableName()).append(" SET ");
                String separator = "";
                for (int i = 0; i < fieldNames.length; i++) {
                        if (isAutoNumerical(fieldTypes[i])) {
                                continue;
                        } else {
                                String fieldValue;
                                if ((fieldTypes[i].getTypeCode() == Type.GEOMETRY)
                                        && (row[i].getType() != Type.NULL)) {
                                        Geometry g = row[i].getAsGeometry();
                                        DimensionConstraint gc = (DimensionConstraint) fieldTypes[i].getConstraint(Constraint.GEOMETRY_DIMENSION);
                                        WKTWriter writer = new WKTWriter(getGeometryDimension(gc));
                                        fieldValue = "GeomFromText('" + writer.write(g) + "')";
                                } else {
                                        fieldValue = row[i].getStringValue(this);
                                }
                                sql.append(separator).append("\"").append(fieldNames[i]).append("\" = ").append(fieldValue);
                                separator = ", ";
                        }
                }

                sql.append(" WHERE \"").append(pkNames[0]).append("\" = ").append(
                        pkValues[0].getStringValue(this));

                for (int i = 1; i < pkNames.length; i++) {
                        sql.append(" AND \"").append(pkNames[0]).append("\" = ").append(
                                pkValues[0].getStringValue(this));
                }

                return sql.toString();
        }

        private class Window {

                private int length = 1024 * 8;
                private int offset;

                public Window(int offset) {
                        this.offset = offset;
                }

                public void moveTo(long rowIndex) throws DriverException {
                        if ((rowIndex < offset) || (rowIndex >= offset + length)) {
                                this.offset = (int) rowIndex;
                                getData();
                        }
                }
        }

        public int getType() {
                return SourceManager.DB | SourceManager.VECTORIAL;
        }

        @Override
        public ConversionRule[] getConversionRules() {
                return new ConversionRule[]{new AutonumericRule(),
                                new PGBinaryRule(), new BooleanRule(), new DateRule(),
                                new PGDoubleRule(), new PGIntRule(), new PGLongRule(),
                                new PGShortRule(), new StringRule(), new TimestampRule(),
                                new TimeRule(), new PGGeometryRule()};
        }

        public String validateMetadata(Metadata metadata) {
                return null;
        }

        @Override
        public int getDefaultPort() {
                return 5432;
        }

        @Override
        public String getTypeDescription() {
                return "PostgreSQL / PostGIS";
        }

        @Override
        public String getTypeName() {
                return "POSTGRESQL";
        }

        @Override
        public String[] getSchemas(Connection c) throws DriverException {
                String[] result = super.getSchemas(c);

                List<String> schemas = new ArrayList<String>();
                for (int i = 0; i < result.length; i++) {
                        schemas.add(result[i]);
                }

                schemas.remove("information_schema");
                schemas.remove("pg_catalog");
                schemas.remove("pg_toast_temp_1");

                return schemas.toArray(new String[schemas.size()]);
        }
}
