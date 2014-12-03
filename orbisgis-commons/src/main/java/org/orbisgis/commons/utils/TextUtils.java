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
package org.orbisgis.utils;

import java.util.regex.Pattern;

/**
 * Utility class that provides useful functions for dealing with text.
 * 
 * @author Erwan Bocher
 * @author Antoine Gourlay <antoine@gourlay.fr>
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
         * Return the absolute start and end position of the specified positions in a text composed by the specified
         * lines
         *
         * @param bl start position line
         * @param bc start position column
         * @param el end position line
         * @param ec end position column
         * @param lines lines representing the multi-line text
         * @return An array of two elements. The first one is the start position (inclusive) and the second is the end
         * position (exclusive)
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

        /**
         * Creates a {@link Pattern } instance for a LIKE pattern.
         *
         * A LIKE pattern matches always the complete string, and allows for the following special characters: - '%'
         * matches any sequence of zero or more characters - '_' matches any single character - '\' escapes the meaning
         * of a special character (any of '\', '%', '_').
         *
         * This implementation is not strict about the escape character: while the only way to match a '%' or a '_'
         * character is to escape it, a '\' character followed by anything else than '%', '_' or '\' will not report any
         * error and will just match a regular '\' character. This means that
         * <code>str\\str</code> and
         * <code>str\str</code> both only match the String literal
         * <code>str\str</code>.
         *
         * Note: this implementation is not entirely identical to the SQL standard's definition of the LIKE operator.
         * The non-strict behavior describe in the paragraph above is not in the standard. Most implementation will
         * reject non-properly-escaped pattern strings, so always escaping the backslash is the preferred option.
         *
         * @param pattern a LIKE pattern string
         * @return a standard pattern
         */
        public static Pattern buildLikePattern(String pattern) {
                return buildLikePattern(pattern, false);
        }

        /**
         * Creates a {@link Pattern } instance for a LIKE pattern.
         *
         * A LIKE pattern matches always the complete string, and allows for the following special characters: - '%'
         * matches any sequence of zero or more characters - '_' matches any single character - '\' escapes the meaning
         * of a special character (any of '\', '%', '_').
         *
         * This implementation is not strict about the escape character: while the only way to match a '%' or a '_'
         * character is to escape it, a '\' character followed by anything else than '%', '_' or '\' will not report any
         * error and will just match a regular '\' character. This means that
         * <code>str\\str</code> and
         * <code>str\str</code> both only match the String literal
         * <code>str\str</code>.
         *
         * Note: this implementation is not entirely identical to the SQL standard's definition of the LIKE operator.
         * The non-strict behavior describe in the paragraph above is not in the standard. Most implementation will
         * reject non-properly-escaped pattern strings, so always escaping the backslash is the preferred option.
         *
         * @param pattern a LIKE pattern string
         * @param caseInsensitive true if the match has to be text insensitive
         * @return a standard pattern
         */
        public static Pattern buildLikePattern(String pattern, boolean caseInsensitive) {
                String[] s = SPLITLIKE.split(pattern);
                StringBuilder b = new StringBuilder();
                b.append("^");
                if (s.length == 0) {
                        for (int i = 0; i < pattern.length(); i++) {
                                switch (pattern.charAt(i)) {
                                        case '%':
                                                b.append(".*");
                                                break;
                                        case '_':
                                                b.append(".");
                                                break;
                                        default:
                                }
                        }
                } else {
                        int pos = 0;
                        for (int i = 0; i < s.length; i++) {
                                boolean esc = false;
                                if (s[i].endsWith("\\")) {
                                        pos++;
                                        if (i + 1 < s.length) {
                                                s[i + 1] = s[i].substring(0, s[i].length() - 1) + pattern.charAt(pos + s[i].length() - 1) + s[i + 1];
                                                continue;
                                        } else {
                                                s[i] = s[i].substring(0, s[i].length() - 1);
                                                esc = true;
                                        }
                                }
                                if (!s[i].isEmpty()) {
                                        b.append(Pattern.quote(ESCAPE.matcher(s[i]).replaceAll("\\\\")));
                                        pos += s[i].length();
                                }
                                if (pos < pattern.length()) {
                                        switch (pattern.charAt(pos)) {
                                                case '%':
                                                        if (esc) {
                                                                b.append("%");
                                                        } else {
                                                                b.append(".*");
                                                        }
                                                        pos++;
                                                        break;
                                                case '_':
                                                        if (esc) {
                                                                b.append("_");
                                                        } else {
                                                                b.append(".");
                                                        }
                                                        pos++;
                                                        break;
                                                default:
                                        }
                                }
                        }
                        while (pos < pattern.length()) {
                                switch (pattern.charAt(pos)) {
                                        case '%':
                                                b.append(".*");
                                                pos++;
                                                break;
                                        case '_':
                                                b.append(".");
                                                pos++;
                                                break;
                                        default:
                                }
                        }
                }

                b.append("$");
                if (caseInsensitive) {
                        return Pattern.compile(b.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                } else {
                        return Pattern.compile(b.toString());
                }
        }
        private static final Pattern SPLITLIKE = Pattern.compile("%|_");
        private static final Pattern ESCAPE = Pattern.compile("\\\\\\\\");
        private static final Pattern DOT = Pattern.compile("\\.");

        /**
         * Creates a {@link Pattern } instance for a SIMILAR TO pattern.
         *
         * A SIMILAR TO pattern matches always the complete string, and allows any POSIX special character EXCEPT that:
         * - '%' matches any sequence of zero or more characters - '_' matches any single character (instead of '.' in
         * POSIX) - '\' escapes the meaning of a any character (POSIX and above).
         *
         * This implementation is not strict about the escape character: while the only way to match a special character
         * is to escape it, a '\' character followed by a regular character will not report any error and will just
         * match a regular '\' character. This means that
         * <code>str\\str</code> and
         * <code>str\str</code> both only match the String literal
         * <code>str\str</code>.
         *
         * See {@link Pattern } for the list of supported POSIX special characters and features.
         *
         * Note: this implementation is not entirely identical to the SQL standard's definition of a regular expression.
         * It allows some POSIX special character, like '\r' (carriage-return) or '\s' (whitespace character), that are
         * not allowed in the SQL definition. Furthermore, most implementation will reject non-properly-escaped pattern
         * strings, so always escaping the backslash is the preferred option.
         *
         * @param pattern
         * @return
         */
        public static Pattern buildSimilarToPattern(String pattern) {
                String[] s = SPLITLIKE.split(pattern);
                StringBuilder b = new StringBuilder();
                b.append("^");
                if (s.length == 0) {
                        for (int i = 0; i < pattern.length(); i++) {
                                switch (pattern.charAt(i)) {
                                        case '%':
                                                b.append(".*");
                                                break;
                                        case '_':
                                                b.append(".");
                                                break;
                                        default:
                                }
                        }
                } else {
                        int pos = 0;
                        for (int i = 0; i < s.length; i++) {
                                boolean esc = false;
                                if (s[i].endsWith("\\")) {
                                        pos++;
                                        if (i + 1 < s.length) {
                                                s[i + 1] = s[i].substring(0, s[i].length() - 1) + pattern.charAt(pos + s[i].length() - 1) + s[i + 1];
                                                continue;
                                        } else {
                                                s[i] = s[i].substring(0, s[i].length() - 1);
                                                esc = true;
                                        }
                                }
                                if (!s[i].isEmpty()) {
                                        String t = ESCAPE.matcher(s[i]).replaceAll("\\\\");
                                        b.append(DOT.matcher(t).replaceAll("\\\\."));
                                        pos += s[i].length();
                                }
                                if (pos < pattern.length()) {
                                        switch (pattern.charAt(pos)) {
                                                case '%':
                                                        if (esc) {
                                                                b.append("%");
                                                        } else {
                                                                b.append(".*");
                                                        }
                                                        pos++;
                                                        break;
                                                case '_':
                                                        if (esc) {
                                                                b.append("_");
                                                        } else {
                                                                b.append(".");
                                                        }
                                                        pos++;
                                                        break;
                                                default:
                                        }
                                }
                        }
                        while (pos < pattern.length()) {
                                switch (pattern.charAt(pos)) {
                                        case '%':
                                                b.append(".*");
                                                pos++;
                                                break;
                                        case '_':
                                                b.append(".");
                                                pos++;
                                                break;
                                        default:
                                }
                        }
                }
                b.append("$");
                return Pattern.compile(b.toString());
        }

        private TextUtils() {
        }
}
