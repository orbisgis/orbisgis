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
/*
 * Library name : dxf
 * (C) 2006 Micha�l Michaud
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * michael.michaud@free.fr
 *
 */
package org.gdms.driver.dxf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * DxfGroup is a group containing a dxf code and a dxf value.
 * The class contains several utils to read and write groups an to format data.
 * @author Micha�l Michaud
 * @version 0.5.0
 */
// History
public class DxfGroup {
        // Pour �crire le symbole d�cimal anglophone (.) plut�t que fran�ais (,)

        private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        private static final DecimalFormat[] decimalFormats = new DecimalFormat[]{
                new DecimalFormat("#0", dfs),
                new DecimalFormat("#0.0", dfs),
                new DecimalFormat("#0.00", dfs),
                new DecimalFormat("#0.000", dfs),
                new DecimalFormat("#0.0000", dfs),
                new DecimalFormat("#0.00000", dfs),
                new DecimalFormat("#0.000000", dfs),
                new DecimalFormat("#0.0000000", dfs),
                new DecimalFormat("#0.00000000", dfs),
                new DecimalFormat("#0.000000000", dfs),
                new DecimalFormat("#0.0000000000", dfs),
                new DecimalFormat("#0.00000000000", dfs),
                new DecimalFormat("#0.000000000000", dfs)};
        private int code;
        private String value;
        private long address;

        public DxfGroup(int code, String value) {
                this.code = code;
                this.value = value;
        }

        public DxfGroup(String code, String value) throws NumberFormatException {
                try {
                        this.code = Integer.parseInt(code);
                        this.value = value;
                } catch (NumberFormatException nfe) {
                        throw nfe;
                }
        }

        public int getCode() {
                return code;
        }

        public void setCode(int code) {
                this.code = code;
        }

        public String getValue() {
                return value;
        }

        public int getIntValue() {
                return Integer.parseInt(value.trim());
        }

        public float getFloatValue() {
                return Float.parseFloat(value.trim());
        }

        public double getDoubleValue() {
                return Double.parseDouble(value.trim());
        }

        public void setValue(String value) {
                this.value = value;
        }

        public long getAddress() {
                return address;
        }

        private void setAddress(long address) {
                this.address = address;
        }

        public boolean equals(DxfGroup other) {
                if (code == other.getCode() && value.equals(other.getValue())) {
                        return true;
                } else {
                        return false;
                }
        }

        @Override
        public String toString() {
                String codeString = "    " + Integer.toString(code);
                int stringLength = codeString.length();
                codeString = codeString.substring(stringLength - (code < 1000 ? 3 : 4), stringLength);
                return codeString + "\r\n" + value + "\r\n";
        }

        public static String int34car(int code) {
                if (code < 10) {
                        return "  " + Integer.toString(code);
                } else if (code < 100) {
                        return " " + Integer.toString(code);
                } else {
                        return Integer.toString(code);
                }
        }

        public static String int6car(int value) {
                String s = "     " + Integer.toString(value);
                return s.substring(s.length() - 6, s.length());
        }

        public static String toString(int code, String value) {
                return int34car(code) + "\r\n" + value + "\r\n";
        }

        public static String toString(int code, int value) {
                return int34car(code) + "\r\n" + int6car(value) + "\r\n";
        }

        public static String toString(int code, float value, int decimalPartLength) {
                return int34car(code) + "\r\n"
                        + decimalFormats[decimalPartLength].format((double) value) + "\r\n";
        }

        public static String toString(int code, double value, int decimalPartLength) {
                return int34car(code) + "\r\n"
                        + decimalFormats[decimalPartLength].format(value) + "\r\n";
        }

        public static String toString(int code, Object value) {
                if (value instanceof String) {
                        return toString(code, (String) value);
                } else if (value instanceof Integer) {
                        return toString(code, ((Integer) value).intValue());
                } else if (value instanceof Float) {
                        return toString(code, ((Float) value).floatValue(), 3);
                } else if (value instanceof Double) {
                        return toString(code, ((Double) value).doubleValue(), 6);
                } else {
                        return toString(code, value.toString());
                }
        }

        public static DxfGroup readGroup(RandomAccessFile raf) throws IOException {
                try {
                        long pos = raf.getFilePointer();
                        DxfGroup dxfGroup = new DxfGroup(Integer.parseInt(raf.readLine().trim()), raf.readLine());
                        dxfGroup.setAddress(pos);
                        return dxfGroup;
                } catch (IOException ioe) {
                        raf.close();
                        throw ioe;
                }
        }
}
