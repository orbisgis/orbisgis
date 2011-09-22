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
package org.orbisgis.core.ui.plugins.views.sqlConsole.language.matcher;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Specialized class for dealing with SQL words within a (partial) SQL query.
 * @author antoine
 */
public final class SQLLexer {

        private final String s;
        private int currentIndex;

        public SQLLexer(String s) {
                this.s = s;
                currentIndex = s.length() - 1;
        }

        public Iterator<String> getTokenIterator() {
                return new SQLStringTokenIterator();
        }

        public class SQLStringTokenIterator implements Iterator<String> {

                public SQLStringTokenIterator() {
                        // drop one if does not end in a whitespace or a dot
                        if (currentIndex != -1) {
                                char currChar = s.charAt(currentIndex);
                                while (isCorrectChar(currChar) && currChar != '.') {
                                        if (currentIndex == 0) {
                                                currentIndex = -1;
                                                break;
                                        }
                                        currentIndex--;
                                        currChar = s.charAt(currentIndex);
                                }
                        }
                }

                @Override
                public boolean hasNext() {
                        return currentIndex >= 0;
                }

                @Override
                public String next() {
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
                        while (isCorrectChar(currChar) && currentIndex != 0) {
                                currentIndex--;
                                currChar = s.charAt(currentIndex);
                        }
                        int start = currentIndex == 0 ? 0 : currentIndex + 1;

                        return s.substring(start, end);

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
