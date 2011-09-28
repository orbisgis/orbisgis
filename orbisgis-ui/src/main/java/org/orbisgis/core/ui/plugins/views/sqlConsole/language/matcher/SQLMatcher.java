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
 * @since 4.0
 */
public class SQLMatcher {

        private SQLCompletionProvider pr;
        private SQLLexer lexer;
        private Iterator<String> it;

        /**
         * Creates a new SQLMatcher that will register its completions to the 
         * given SQLCompletionProvider.
         * @param pr
         */
        public SQLMatcher(SQLCompletionProvider pr) {
                this.pr = pr;
        }

        /**
         * Main entry point for matching a SQL String.
         * @param str a string
         */
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

                if (a.endsWith(".")) {
                        addFieldsForTable(a);
                        return;
                } else if (a.endsWith(";")) {
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE");
                        return;
                } else if (a.endsWith(",")) {
                        matchAfterComma();
                        return;
                }

                if ("AS".equalsIgnoreCase(a)) {
                        matchAs1();
                } else if ("FROM".equalsIgnoreCase(a)) {
                        // FROM tablename
                        addTables(false);
                        addTableFunctions();
                } else if ("TABLE".equalsIgnoreCase(a)) {
                        // DROP TABLE and others ending in TABLE
                        matchSourceNames1();
                } else if ("SELECT".equalsIgnoreCase(a)) {
                        // SELECT table.field
                        addScalarFunctions();
                        addTables(true);
                } else if ("CREATE".equalsIgnoreCase(a) || "DROP".equalsIgnoreCase(a)) {
                        // CREATE/DROP tata
                        addKeyWords("TABLE", "VIEW", "INDEX");
                } else if ("INDEX".equalsIgnoreCase(a)) {
                        // CREATE INDEX ON tutu(field)
                        addKeyWord("ON");
                } else if ("EXECUTE".equalsIgnoreCase(a) || "CALL".equalsIgnoreCase(a)) {
                        // table function call
                        addExecutorFunctions();
                } else {
                        // identifier
                        matchAfterPossibleId();
                }
        }

        private void matchAfterPossibleId() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();
                if (a.endsWith(",")) {
                        addKeyWord("AS");
                } else if ("SELECT".equalsIgnoreCase(a)) {
                        addKeyWords("AS", "FROM");
                        return;
                } else if (a.endsWith(";")) {
                        return;
                }

                while (it.hasNext()) {
                        String b = it.next();
                        if ("SELECT".equalsIgnoreCase(b)) {
                                addKeyWord("FROM");
                                return;
                        } else if (a.endsWith(";")) {
                                return;
                        }
                }

        }

        private void matchAfterComma() {
                while (it.hasNext()) {
                        String a = it.next();
                        if ("FROM".equalsIgnoreCase(a)) {
                                addTables(false);
                                addTableFunctions();
                                return;
                        } else if ("SELECT".equalsIgnoreCase(a)) {
                                addScalarFunctions();
                                addTables(true);
                                return;
                        } else if (a.endsWith(";")) {
                                return;
                        }
                }
        }

        private void matchAs1() {
                if (!it.hasNext()) {
                        return;
                }
                it.next();
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("TABLE".equalsIgnoreCase(a)) {
                        addKeyWord("SELECT");
                }
        }

        private void matchSourceNames1() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("DROP".equalsIgnoreCase(a) || "ALTER".equalsIgnoreCase(a)) {
                        // DROP TABLE ; ALTER TABLE
                        addTables(false);
                }
        }

        private void addFieldsForTable(String tableDot) {
                int par = tableDot.lastIndexOf("(");
                par = par == -1 ? 0 : par + 1;
                pr.addFieldsCompletion(tableDot.substring(par, tableDot.length() - 1));
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
