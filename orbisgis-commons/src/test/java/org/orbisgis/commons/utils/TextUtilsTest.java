/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.commons.utils;

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
