/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan Bocher, scientific researcher,
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
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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

import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Antoine Gourlay
 */
public class TextUtilsTest {

        @Test
        public void testLikePattern() {
                Pattern p = TextUtils.buildLikePattern("str%a_8");
                match(p, "straaa8");
                match(p, "straa8");
                match(p, "strah8");
                match(p, "strazertyam8");
                notMatch(p, "stra8");
                notMatch(p, "straa");
                notMatch(p, "strazertya118");
                notMatch(p, "1straaa8");

                p = TextUtils.buildLikePattern("%str");
                match(p, "str");
                match(p, "astr");
                match(p, "strstr");
                match(p, "azerty34str");
                notMatch(p, "tr");
                notMatch(p, "str12");
                notMatch(p, "azertstrf");
                notMatch(p, "st");

                p = TextUtils.buildLikePattern("_str%");
                match(p, "sstr");
                match(p, "astr");
                match(p, "7strstr");
                match(p, "astrazert5y");
                notMatch(p, "str");
                notMatch(p, "str12stuff");
                notMatch(p, "sst");
                notMatch(p, "zzstr");

                p = TextUtils.buildLikePattern("__tr%");
                match(p, "sstr");
                match(p, "astr23");
                match(p, "7strstr");
                match(p, "trtrz");
                notMatch(p, "str");
                notMatch(p, "str12stuff");
                notMatch(p, "sst");
                notMatch(p, "zzztr12");

                p = TextUtils.buildLikePattern("_str%%");
                match(p, "sstr");
                match(p, "astr");
                match(p, "7strstr");
                match(p, "astrazert5y");
                notMatch(p, "str");
                notMatch(p, "str12stuff");
                notMatch(p, "sst");
                notMatch(p, "zzstr");

                p = TextUtils.buildLikePattern("__");
                match(p, "gg");
                match(p, "a1");
                notMatch(p, "str");
                notMatch(p, "s");
                notMatch(p, "1234g");

                p = TextUtils.buildLikePattern("%%");
                match(p, "gg");
                match(p, "a1");
                match(p, "str");
                match(p, "s");
                match(p, "1234g");

                p = TextUtils.buildLikePattern("str\\%a_8");
                match(p, "str%aa8");
                match(p, "str%ab8");
                notMatch(p, "stra8");
                notMatch(p, "straa8");
                notMatch(p, "strazertyaj8");
                notMatch(p, "1straaa8");

                p = TextUtils.buildLikePattern("str\\%a\\_8");
                match(p, "str%a_8");
                notMatch(p, "str%ab8");
                notMatch(p, "stra8");
                notMatch(p, "straa8");
                notMatch(p, "strazertyaj8");
                notMatch(p, "1straaa8");

                p = TextUtils.buildLikePattern("\\\\");
                match(p, "\\");
                notMatch(p, "\\\\");
                notMatch(p, "s\\");
                notMatch(p, "\\sse");
                
                p = TextUtils.buildLikePattern("ab\\_");
                match(p, "ab_");
                notMatch(p, "aba");
                notMatch(p, "ab_b");

        }
        
        @Test
        public void testSimilarToPattern() {
                Pattern p = TextUtils.buildSimilarToPattern("st[ra]_+8");
                match(p, "str%aa8");
                match(p, "str%ab8");
                notMatch(p, "sta8");
        }

        private void match(Pattern p, String s) {
                assertTrue(p.matcher(s).matches());
        }

        private void notMatch(Pattern p, String s) {
                assertFalse(p.matcher(s).matches());
        }
}
