/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.language.matcher;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Specialized class for dealing with SQL words within a (partial) SQL query.
 * 
 * This class lexes tokens (in this case, words) from the end of the string to the beginning.
 * 
 * @author Antoine Gourlay
 * @since 4.0
 */
final class SQLLexer {

        private final String s;
        private int currentIndex;

        /**
         * Creates a new SQLLexer for the given String.
         * @param s
         */
        SQLLexer(String s) {
                this.s = s;
                currentIndex = s.length() - 1;
        }

        Iterator<String> getTokenIterator() {
                return new SQLStringTokenIterator();
        }

        class SQLStringTokenIterator implements Iterator<String> {

                private SQLStringTokenIterator() {
                        // drop one if does not end in a whitespace or a dot
                        if (currentIndex != -1) {
                                char currChar = s.charAt(currentIndex);
                                while (isCorrectChar(currChar) && currChar != '.'
                                        && currChar != '(') {
                                        currentIndex--;
                                        if (currentIndex == -1) {
                                                break;
                                        }
                                        currChar = s.charAt(currentIndex);
                                }
                        }
                        trimIncorrectChars();
                }

                private void trimIncorrectChars() {
                        // trim incorrect chars
                        if (currentIndex == -1) {
                                return;
                        }

                        char currChar = s.charAt(currentIndex);
                        while (!isCorrectChar(currChar)) {
                                currentIndex--;
                                if (currentIndex == -1) {
                                        break;
                                }
                                currChar = s.charAt(currentIndex);
                        }
                }

                @Override
                public boolean hasNext() {
                        return currentIndex != -1;
                }

                @Override
                public String next() {
                        if (currentIndex == -1) {
                                throw new NoSuchElementException();
                        }

                        trimIncorrectChars();
                        
                        if (currentIndex == -1) {
                                return "";
                        }

                        char currChar = s.charAt(currentIndex);

                        // this is the end
                        int end = currentIndex + 1;

                        // we go back again until we get to an invalid character
                        while (isCorrectChar(currChar)) {
                                currentIndex--;
                                if (currentIndex == -1) {
                                        break;
                                }
                                currChar = s.charAt(currentIndex);
                        }

                        return s.substring(currentIndex + 1, end);

                }

                private boolean isCorrectChar(char currChar) {
                        return !Character.isWhitespace(currChar);
                }

                @Override
                public void remove() {
                        throw new UnsupportedOperationException();
                }
        }
}
