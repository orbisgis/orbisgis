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
package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.DefaultSchema;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.Schema;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.DataSet;

public class AllTypesObjectDriver extends AbstractDataSet implements MemoryDriver {

        private Value[][] values = new Value[2][12];
        private static String[] names = new String[]{"binary", "boolean", "byte",
                "date", "double", "float", "int", "long", "short", "string",
                "timestamp", "time"};
        private static int[] typesCodes = new int[]{Type.BINARY, Type.BOOLEAN,
                Type.BYTE, Type.DATE, Type.DOUBLE, Type.FLOAT, Type.INT, Type.LONG,
                Type.SHORT, Type.STRING, Type.TIMESTAMP, Type.TIME};
        private static Type[] types;
        private static Schema schema;

        static {
                final int fc = names.length;
                final Type[] fieldsTypes = new Type[fc];
                final String[] fieldsNames = new String[fc];
                TypeDefinition csvTypeDef;

                try {
                        for (int i = 0; i < fc; i++) {
                                csvTypeDef = new DefaultTypeDefinition(names[i], typesCodes[i],
                                        null);
                                fieldsNames[i] = names[i];
                                fieldsTypes[i] = csvTypeDef.createType(null);
                        }

                        schema = new DefaultSchema("AllTypes");
                        schema.addTable("main", new DefaultMetadata(fieldsTypes, fieldsNames));
                } catch (InvalidTypeException e) {
                        throw new RuntimeException("Bug in the static part", e);
                }
        }

        public AllTypesObjectDriver() throws ParseException {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                values[0][0] = ValueFactory.createValue(new byte[]{1, 2});
                values[0][1] = ValueFactory.createValue(true);
                values[0][2] = ValueFactory.createValue((byte) 4);
                values[0][3] = ValueFactory.createValue(df.parse("1980-9-5"));
                values[0][4] = ValueFactory.createValue(3d);
                values[0][5] = ValueFactory.createValue(3f);
                values[0][6] = ValueFactory.createValue(3);
                values[0][7] = ValueFactory.createValue(3L);
                values[0][8] = ValueFactory.createValue((short) 3);
                values[0][9] = ValueFactory.createValue("3");
                values[0][10] = ValueFactory.createValue(Timestamp.valueOf("1980-09-05 10:30:00.666666666"));
                values[0][11] = ValueFactory.createValue(Time.valueOf("10:30:00"));

                values[1][0] = ValueFactory.createValue(new byte[]{0, 2});
                values[1][1] = ValueFactory.createValue(false);
                values[1][2] = ValueFactory.createValue((byte) 5);
                values[1][3] = ValueFactory.createValue(df.parse("1986-9-5"));
                values[1][4] = ValueFactory.createValue(4d);
                values[1][5] = ValueFactory.createValue(4f);
                values[1][6] = ValueFactory.createValue(4);
                values[1][7] = ValueFactory.createValue(4L);
                values[1][8] = ValueFactory.createValue((short) 4);
                values[1][9] = ValueFactory.createValue("4");
                values[1][10] = ValueFactory.createValue(Timestamp.valueOf("1984-09-05 10:30:00.666666666"));
                values[1][11] = ValueFactory.createValue(Time.valueOf("10:31:40"));

        }

        /**
         * @see org.gdms.driver.MemoryDriver#write(org.gdms.data.edition.DataWare)
         */
        public void write(DataSource dataSource) throws DriverException {
                final int fc = dataSource.getMetadata().getFieldCount();
                names = new String[fc];
                types = new Type[fc];

                for (int i = 0; i < fc; i++) {
                        names[i] = dataSource.getMetadata().getFieldName(i);
                        types[i] = dataSource.getMetadata().getFieldType(i);
                }
                Value[][] newValues = new Value[(int) dataSource.getRowCount()][dataSource.getMetadata().getFieldCount()];
                for (int i = 0; i < dataSource.getRowCount(); i++) {
                        for (int j = 0; j < dataSource.getMetadata().getFieldCount(); j++) {
                                newValues[i][j] = dataSource.getFieldValue(i, j);
                        }
                }

                values = newValues;
        }

        /**
         * @see org.gdms.driver.DataSet#getFieldType(int)
         */
        public Type getFieldType(int i) throws DriverException {
                return types[i];
        }

        public void setDataSourceFactory(DataSourceFactory dsf) {
        }

        public String getDriverId() {
                return null;
        }

        public int getType(String driverType) {
                if ("STRING".equals(driverType)) {
                        return Type.STRING;
                } else if ("LONG".equals(driverType)) {
                        return Type.LONG;
                } else if ("BOOLEAN".equals(driverType)) {
                        return Type.BOOLEAN;
                } else if ("DATE".equals(driverType)) {
                        return Type.DATE;
                } else if ("DOUBLE".equals(driverType)) {
                        return Type.DOUBLE;
                } else if ("INT".equals(driverType)) {
                        return Type.INT;
                } else if ("FLOAT".equals(driverType)) {
                        return Type.FLOAT;
                } else if ("SHORT".equals(driverType)) {
                        return Type.SHORT;
                } else if ("BYTE".equals(driverType)) {
                        return Type.BYTE;
                } else if ("BINARY".equals(driverType)) {
                        return Type.BINARY;
                } else if ("TIMESTAMP".equals(driverType)) {
                        return Type.TIMESTAMP;
                } else if ("TIME".equals(driverType)) {
                        return Type.TIME;
                }

                throw new RuntimeException();
        }

        public void open() throws DriverException {
        }

        public void close() throws DriverException {
        }

        public TypeDefinition[] getTypesDefinitions() {
                final TypeDefinition[] result = new TypeDefinition[typesCodes.length];
                for (int i = 0; i < typesCodes.length; i++) {
                        result[i] = new DefaultTypeDefinition(names[i], typesCodes[i]);
                }
                return result;
        }

        public int getSupportedType() {
                return 0;
        }

        public int getType() {
                return 0;
        }

        @Override
        public String getTypeDescription() {
                return null;
        }

        @Override
        public String getTypeName() {
                return null;
        }

        @Override
        public boolean isCommitable() {
                return false;
        }

        @Override
        public String validateMetadata(Metadata metadata) throws DriverException {
                return null;
        }

        @Override
        public Schema getSchema() throws DriverException {
                return schema;
        }

        @Override
        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                return values[(int) rowIndex][fieldId];
        }

        @Override
        public long getRowCount() throws DriverException {
                return values.length;
        }

        @Override
        public Number[] getScope(int dimension) throws DriverException {
                return null;
        }

        @Override
        public Metadata getMetadata() throws DriverException {
                return schema.getTableByName("main");
        }

        @Override
        public DataSet getTable(String name) {
                if (!name.equals("main")) {
                        return null;
                }
                return this;
        }
}