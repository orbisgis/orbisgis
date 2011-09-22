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
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.orbisgis.core.ui.plugins.views.sqlConsole.language.SQLCompletionProvider;

/**
 * This is a hand written SQL pattern matcher that triggers the correct completion actions.
 * @author Antoine Gourlay
 */
public class SQLMatcher {

        private SQLCompletionProvider pr;
        private SQLLexer lexer;
        private Iterator<String> it;

        public SQLMatcher(SQLCompletionProvider pr) {
                this.pr = pr;
        }

        public void match(String str) {
                lexer = new SQLLexer(str);
                it = lexer.getTokenIterator();

                matchInit();

        }

        private void matchInit() {
                if (!it.hasNext()) {
                        // no text
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE");
                        return;
                }
                String a = it.next();

                if ("FROM".equals(a)) {
                        // FROM tablename
                        addTables(false);
                        addTableFunctions();
                } else if ("TABLE".equals(a)) {
                        // DROP TABLE and others ending in TABLE
                        matchSourceNames1();
                } else if ("SELECT".equals(a)) {
                        // SELECT table.field
                        addScalarFunctions();
                        addTables(true);
                } else if ("CREATE".equals(a) || "DROP".equals(a)) {
                        // CREATE/DROP tata
                        addKeyWords("TABLE", "VIEW", "INDEX");
                } else if ("INDEX".equals(a)) {
                        // CREATE INDEX ON tutu(field)
                        addKeyWord("ON");
                } else if ("EXECUTE".equals(a) || "CALL".equals(a)) {
                        // table function call
                        addExecutorFunctions();
                } else if (";".equals(a)) {
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE");
                }
        }

        private void matchSourceNames1() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("DROP".equals(a) || "ALTER".equals(a)) {
                        // DROP TABLE ; ALTER TABLE
                        addTables(false);
                }
        }

        private void addScalarFunctions() {
                pr.addFunctionCompletions(false, true, false);
        }

        private void addTableFunctions() {
                pr.addFunctionCompletions(true, false, false);
        }
        
        private void addExecutorFunctions() {
                pr.addFunctionCompletions(false, false, true);
        }

        private void addTables(boolean withFields) {
                pr.addSourceNamesCompletion(withFields);
        }

        private void addKeyWords(String... k) {
                for (int i = 0; i < k.length; i++) {
                        addKeyWord(k[i]);
                }
        }

        private void addKeyWord(String k) {
                pr.addCompletion(new ShorthandCompletion(pr, k, k + ' '));
        }
}
