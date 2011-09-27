/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : #name, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
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
 */
package org.orbisgis.utils;

/**
 * Utility class that provides useful functions for dealing with text.
 */
public final class TextUtils {

        /**
         * Returns the platform-specific line separator, or "\n" if it is not defined for some reason.
         *
         * @return the platform-specific line separator.
         */
        public static String getEolStr() {
                return System.getProperty("line.separator", "\n");
        }

        /**
         * Convert a string to HTML. From S. Bayer.
         *
         * @param string
         * @return
         */
        public static String stringToHTMLString(String string) {
                StringBuilder sb = new StringBuilder(string.length());
                // true if last char was blank
                boolean lastWasBlankChar = false;
                int len = string.length();
                char c;

                for (int i = 0; i < len; i++) {
                        c = string.charAt(i);
                        if (c == ' ') {
                                // blank gets extra work,
                                // this solves the problem you get if you replace all
                                // blanks with &nbsp;, if you do that you loss
                                // word breaking
                                if (lastWasBlankChar) {
                                        lastWasBlankChar = false;
                                        sb.append("&nbsp;");
                                } else {
                                        lastWasBlankChar = true;
                                        sb.append(' ');
                                }
                        } else {
                                lastWasBlankChar = false;
                                //
                                // HTML Special Chars
                                if (c == '"') {
                                        sb.append("&quot;");
                                } else if (c == '&') {
                                        sb.append("&amp;");
                                } else if (c == '<') {
                                        sb.append("&lt;");
                                } else if (c == '>') {
                                        sb.append("&gt;");
                                } else if (c == '\n') {
                                        sb.append("&lt;br/&gt;");
                                } else {
                                        int ci = 0xffff & c;
                                        if (ci < 160) {
                                                // nothing special only 7 Bit
                                                sb.append(c);
                                        } else {
                                                // Not 7 Bit use the unicode system
                                                sb.append("&#");
                                                sb.append(Integer.valueOf(ci).toString());
                                                sb.append(';');
                                        }
                                }
                        }
                }
                return sb.toString();
        }

        /**
         * Return the absolute start and end position of the specified positions in
         * a text composed by the specified lines
         *
         * @param bl
         *            start position line
         * @param bc
         *            start position column
         * @param el
         *            end position line
         * @param ec
         *            end position column
         * @param lines
         *            lines representing the multi-line text
         * @return An array of two elements. The first one is the start position
         *         (inclusive) and the second is the end position (exclusive)
         */
        public static int[] getLocation(int bl, int bc, int el, int ec,
                String[] lines) {
                int start = getPosition(bl, bc, lines) + bl;
                int end = getPosition(el, ec, lines) + el;

                return new int[]{start, end + 1};
        }

        private static int getPosition(int line, int column, String[] lines) {
                int start = 0;
                for (int i = 0; i < line; i++) {
                        start += lines[i].length();
                }
                start += column;
                return start;
        }

        private TextUtils() {
        }
}
