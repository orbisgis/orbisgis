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

import java.io.Serializable;
import java.sql.Types;
import java.text.ParseException;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.orbisgis.utils.TextUtils;

import org.gdms.data.types.IncompatibleTypesException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;

/**
 * Efficient wrapper for strings.
 *
 * @author Antoine Gourlay
 */
class DefaultStringValue extends AbstractValue implements Serializable, StringValue, CharSequence {

        private char[] value;

        /**
         * Builds a DefaultStringValue from the given String <tt>text</tt>.
         *
         * @param text a string
         */
        DefaultStringValue(String text) {
                this.value = text.toCharArray();
        }

        /**
         * Creates a new StringValue object.
         */
        DefaultStringValue() {
                value = new char[0];
        }

        @Override
        public void setValue(CharSequence value) {
                if (value instanceof String) {
                        this.value = ((String) value).toCharArray();
                } else {
                        char[] temp = new char[value.length()];
                        for (int i = 0; i < value.length(); i++) {
                                temp[i] = value.charAt(i);
                        }
                        this.value = temp;
                }
        }

        @Override
        public CharSequence getValue() {
                return this;
        }

        @Override
        public String toString() {
                return String.valueOf(value);
        }

        private boolean charEquals(CharSequence s1, CharSequence s2) {
                if (s1.length() != s2.length()) {
                        return false;
                }

                for (int i = 0; i < s1.length(); i++) {
                        if (s1.charAt(i) != s2.charAt(i)) {
                                return false;
                        }
                }

                return true;
        }

        @Override
        public BooleanValue equals(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof StringValue) {
                        return ValueFactory.createValue(charEquals(this, ((StringValue) value).getValue()));
                } else {
                        throw new IncompatibleTypesException(
                                "The specified value is not a text value:"
                                + TypeFactory.getTypeName(value.getType()));
                }
        }

        @Override
        public BooleanValue greater(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(compareTo(value) > 0);
        }

        @Override
        public BooleanValue greaterEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(compareTo(value) >= 0);
        }

        @Override
        public BooleanValue less(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(compareTo(value) < 0);
        }

        @Override
        public BooleanValue lessEqual(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                return ValueFactory.createValue(compareTo(value) <= 0);
        }

        @Override
        public BooleanValue matches(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof StringValue) {
                        String pattern = ((StringValue) value).getAsString();

                        return matches(Pattern.compile(pattern));
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue matches(Pattern value) {
                return ValueFactory.createValue(value.matcher(this).matches());
        }

        @Override
        public BooleanValue like(Value value, boolean caseInsensitive) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof StringValue) {
                        String pattern = ((StringValue) value).getAsString();

                        return matches(TextUtils.buildLikePattern(pattern, caseInsensitive));
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public BooleanValue similarTo(Value value) {
                if (value.isNull()) {
                        return ValueFactory.createNullValue();
                }

                if (value instanceof StringValue) {
                        String pattern = ((StringValue) value).getAsString();

                        return matches(TextUtils.buildSimilarToPattern(pattern));
                } else {
                        throw new IncompatibleTypesException();
                }
        }

        @Override
        public int hashCode() {
                return 67 * 5 + Arrays.hashCode(value);
        }

        @Override
        public String getStringValue(ValueWriter writer) {
                return writer.getStatementString(this, Types.VARCHAR);
        }

        @Override
        public int getType() {
                return Type.STRING;
        }

        @Override
        public byte[] getBytes() {
                // this handles UTF-16 encoding for us
                return new String(value).getBytes();
        }

        public static Value readBytes(byte[] buffer) {
                // this handles UTF-16 decoding for us
                return new DefaultStringValue(new String(buffer));
        }

        @Override
        public String getAsString() {
                return toString();
        }

        @Override
        public Value toType(int typeCode) {
                try {
                        Value ret = ValueFactory.createValueByType(toString(), typeCode);
                        if ((ret.getType() & typeCode) != 0) {
                                return ret;
                        } else {
                                throw new IncompatibleTypesException(
                                        "Cannot convert string to "
                                        + TypeFactory.getTypeName(typeCode) + ": "
                                        + value);
                        }
                } catch (NumberFormatException e) {
                        throw new IncompatibleTypesException("Cannot convert value to "
                                + TypeFactory.getTypeName(typeCode) + ": " + value, e);
                } catch (ParseException e) {
                        throw new IncompatibleTypesException("Cannot convert value to "
                                + TypeFactory.getTypeName(typeCode) + ": " + value, e);
                }
        }

        @Override
        public int compareTo(Value o) {
                if (o.isNull()) {
                        // by default, NULL FIRST
                        return -1;
                } else if (o instanceof StringValue) {
                        CharSequence comp = ((StringValue) o).getValue();
                        // lexicographic comparison
                        int minSize = Math.min(value.length, comp.length());
                        for (int i = 0; i < minSize; i++) {
                                char c1 = value[i];
                                char c2 = comp.charAt(i);
                                if (c1 != c2) {
                                        return c1 - c2;
                                }
                        }
                        
                        return value.length - comp.length();
                } else {
                        return super.compareTo(o);
                }
        }

        @Override
        public int length() {
                return value.length;
        }

        @Override
        public char charAt(int index) {
                return value[index];
        }

        @Override
        public CharSequence subSequence(int start, int end) {
                return new SubSequence(start, end - start);
        }

        private class SubSequence implements CharSequence {

                private int start;
                private int length;

                private SubSequence(int start, int length) {
                        this.start = start;
                        this.length = length;
                }

                @Override
                public int length() {
                        return length;
                }

                @Override
                public char charAt(int index) {
                        return DefaultStringValue.this.charAt(start + index);
                }

                @Override
                public CharSequence subSequence(int start, int end) {
                        return new SubSequence(this.start + start, end - start);
                }
        }
}