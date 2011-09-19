/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 **/
package org.orbisgis.core.ui.plugins.views.sqlConsole.language;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Specialized class for dealing with SQL words within a (partial) SQL query.
 * @author antoine
 */
public final class SQLString implements CharSequence {

        private final String s;
        private final String[] tokens;

        public SQLString(String s, String[] tokens) {
                this.s = s;
                this.tokens = tokens;
        }

        @Override
        public int length() {
                return s.length();
        }

        @Override
        public char charAt(int index) {
                return s.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
                return s.subSequence(start, end);
        }

        public Iterator<SQLToken> getTokenIterator() {
                return new SQLStringTokenIterator();
        }

        public class SQLStringTokenIterator implements Iterator<SQLToken> {

                private SQLStringTokenIterator() {
                        currentIndex = s.length() - 1;
                }
                private int currentIndex;

                @Override
                public boolean hasNext() {
                        return currentIndex >= 0;
                }

                @Override
                public SQLToken next() {
                        if (currentIndex < 0) {
                                throw new NoSuchElementException();
                        }

                        // trim incorrect chars
                        char currChar = s.charAt(currentIndex);
                        while (!isCorrectChar(currChar) && currentIndex != 0) {
                                currentIndex--;
                                currChar = s.charAt(currentIndex);
                        }

                        // this is the end
                        int end = currentIndex + 1;

                        // we go back again until we get to an invalid character
                        int start = 0;
                        if (currentIndex != 0) {
                                do {
                                        currentIndex--;
                                        currChar = s.charAt(currentIndex);
                                } while (isCorrectChar(currChar) && currentIndex != 0);
                                start = currentIndex + 1;
                        }

                        String str = s.substring(start, end).toUpperCase();
                        int type = Arrays.binarySearch(tokens, "T_" + str);
                        if (type < 0) {
                                type = -1;
                        }
                        return new SQLToken(str, type);

                }

                private boolean isCorrectChar(char currChar) {
                       return !Character.isWhitespace(currChar) &&
                               (Character.isLetterOrDigit(currChar) ||
                               currChar == '_' || currChar == '.' );
                }

                @Override
                public void remove() {
                        throw new UnsupportedOperationException();
                }
        }
        
        public boolean match(SQLToken[] tokens) {
                int i = 0;
                Iterator<SQLToken> refIterator = getTokenIterator();
                
                while (i != tokens.length) {
                        if (refIterator.hasNext()) {
                                SQLToken next = refIterator.next();
                                SQLToken next1 = tokens[i];
                                if (!next.equals(next1)) {
                                        return false;
                                }
                        } else {
                                return false;
                        }
                        i++;
                }
                
                return true;
        }
        
        public boolean match(SQLToken token) {
                return match(new SQLToken[] { token });
        }
}
