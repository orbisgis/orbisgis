/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.data.values;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Fernando Gonzalez Cortes
 */
class ValueWriterImpl implements ValueWriter {

        private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        private final NumberFormat df = DecimalFormat.getInstance(Locale.ROOT);

        ValueWriterImpl() {
                df.setGroupingUsed(false);
                df.setMaximumFractionDigits(Integer.MAX_VALUE);
        }

        @Override
        public String getStatementString(long i) {
                return Long.toString(i);
        }

        @Override
        public String getStatementString(int i, int sqlType) {
                return Integer.toString(i);
        }

        @Override
        public String getStatementString(double d, int sqlType) {
                return df.format(d).replace(",", ".");
        }

        @Override
        public String getStatementString(String str, int sqlType) {
                return "'" + escapeString(str) + "'";
        }

        @Override
        public String getStatementString(Date d) {
                return "'" + d.toString() + "'";
        }

        @Override
        public String getStatementString(Time t) {
                return "'" + timeFormat.format(t) + "'";
        }

        @Override
        public String getStatementString(Timestamp ts) {
                return "'" + ts.toString() + "'";
        }

        @Override
        public String getStatementString(byte[] binary) {
                final StringBuilder sb = new StringBuilder("'");
                for (int i = 0; i < binary.length; i++) {
                        int theByte = binary[i];
                        if (theByte < 0) {
                                theByte += 256;
                        }
                        final String b = Integer.toHexString(theByte);
                        if (b.length() == 1) {
                                sb.append("0").append(b);
                        } else {
                                sb.append(b);
                        }

                }
                sb.append("'");

                return sb.toString();
        }

        @Override
        public String getStatementString(boolean b) {
                return Boolean.toString(b);
        }

        @Override
        public String getNullStatementString() {
                return "null";
        }

        static String escapeString(String string) {
                return string.replaceAll("\\Q'\\E", "''");
        }

        @Override
        public String getStatementString(Geometry g) {
                return "'" + g.toText() + "'";
        }
}
