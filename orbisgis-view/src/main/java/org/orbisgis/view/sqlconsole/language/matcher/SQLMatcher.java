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
package org.orbisgis.view.sqlconsole.language.matcher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.gdms.data.values.SQLValueFactory;
import org.orbisgis.view.sqlconsole.language.SQLCompletionProvider;

/**
 * This is a hand written SQL pattern matcher that triggers the correct completion actions.
 *
 * @author Antoine Gourlay
 * @since 4.0
 */
public class SQLMatcher {

        private SQLCompletionProvider pr;
        private SQLLexer lexer;
        private Iterator<String> it;
        private static final char[] operators = new char[]{
                '=', '<', '>', '+', '-', '/', '^', '%', '~', '*', '@', '!', '|'};

        static {
                Arrays.sort(operators);
        }

        /**
         * Creates a new SQLMatcher that will register its completions to the
         * given SQLCompletionProvider.
         *
         * @param pr
         */
        public SQLMatcher(SQLCompletionProvider pr) {
                this.pr = pr;
        }

        /**
         * Main entry point for matching a SQL String.
         *
         * @param str a string
         */
        public void match(String str) {
                lexer = new SQLLexer(str);
                it = lexer.getTokenIterator();

                // main matching entry point
                matchInit();

        }

        private void matchInit() {

                // no text
                if (!it.hasNext()) {
                        // no text
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT INTO", "UPDATE", "DELETE", "EXECUTE", "ALTER");
                        return;
                }
                String a = it.next();

                if (a.endsWith(".")) {
                        // field name like 'toto.tata'
                        addFieldsForTable(a);
                        return;
                } else if (a.endsWith(";")) {
                        // end of query: start a new one
                        addKeyWords("CREATE", "DROP", "SELECT", "INSERT INTO", "UPDATE", "DELETE", "EXECUTE", "ALTER");
                        return;
                } else if (a.endsWith(",")) {
                        // comma; we have to look deeper to understand what to do.
                        matchAfterComma();
                        return;
                } else if (a.endsWith("(")) {
                        // open par; we have to look deeper to understand what to do.
                        matchAfterOpenPar();
                        return;
                } else if (a.endsWith(")")) {
                        // close par; we do nothing for now
                        // that's better than displaying rubbish
                        return;
                }

                if ("AS".equalsIgnoreCase(a)) {
                        // as: we have to look deeper to understand what to do.
                        matchAs1();
                } else if ("FROM".equalsIgnoreCase(a) || "JOIN".equalsIgnoreCase(a)) {
                        // FROM tablename
                        addTables(false);
                        addTableFunctions();
                        addKeyWord("(SELECT");
                } else if ("TABLE".equalsIgnoreCase(a)) {
                        // DROP TABLE and others ending in TABLE
                        matchAfterTable();
                } else if ("SELECT".equalsIgnoreCase(a) || "(SELECT".equalsIgnoreCase(a)) {
                        // SELECT table.field
                        addScalarFunctions();
                        addTables(true);
                        addKeyWord("DISTINCT");
                } else if ("WHERE".equalsIgnoreCase(a) || "ON".equalsIgnoreCase(a)
                        || "AND".equalsIgnoreCase(a) || "OR".equalsIgnoreCase(a) || "DISTINCT".equalsIgnoreCase(a)) {
                        // WHERE table.field ...
                        addScalarFunctions();
                        addTables(true);
                } else if ("INSERT".equalsIgnoreCase(a)) {
                        // INSERT INTO toto
                        addKeyWord("INTO");
                } else if ("INTO".equalsIgnoreCase(a) || "UPDATE".equalsIgnoreCase(a)) {
                        // INSERT INTO toto / UPDATE toto
                        addTables(false);
                } else if ("EXCEPT".equalsIgnoreCase(a)) {
                        // EXCEPT field, ...
                        addAllFields();
                } else if ("CREATE".equalsIgnoreCase(a)) {
                        // CREATE tata
                        addKeyWords("TABLE", "VIEW", "INDEX", "OR REPLACE VIEW");
                } else if ("DROP".equalsIgnoreCase(a)) {
                        // DROP tata
                        matchAfterDrop();
                } else if ("INDEX".equalsIgnoreCase(a)) {
                        // CREATE INDEX ON tutu(field)
                        addKeyWord("ON");
                } else if ("EXECUTE".equalsIgnoreCase(a) || "CALL".equalsIgnoreCase(a)) {
                        // table function call
                        addExecutorFunctions();
                } else if ("ALTER".equalsIgnoreCase(a)) {
                        // ALTER TABLE toto
                        matchAfterAlter();
                } else if ("IF".equalsIgnoreCase(a)) {
                        // IF EXISTS
                        addKeyWord("EXISTS");
                } else if ("OR".equalsIgnoreCase(a)) {
                        // OR REPLACE
                        addKeyWord("REPLACE");
                } else if ("REPLACE".equalsIgnoreCase(a)) {
                        // OR REPLACE
                        addKeyWord("VIEW");
                } else if ("RENAME".equalsIgnoreCase(a)) {
                        // RENAME toto/TO
                        addKeyWords("TO", "COLUMN");
                } else if ("UNION".equalsIgnoreCase(a)) {
                        // UNION SELECT ...
                        addKeyWord("SELECT");
                } else if ("ORDER".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWord("BY");
                } else if ("TO".equalsIgnoreCase(a)) {
                        // RENAME TO
                        matchAfterTo();
                } else if ("ADD".equalsIgnoreCase(a)) {
                        // ADD COLUMN
                        addKeyWord("COLUMN");
                } else if ("BY".equalsIgnoreCase(a)) {
                        // ORDER/GROUP BY
                        addTables(true);
                } else if ("EXISTS".equalsIgnoreCase(a)) {
                        // ... EXISTS
                        matchAfterExists();
                } else if ("COLUMN".equalsIgnoreCase(a)) {
                        // ADD/DROP/RENAME/ALTER COLUMN
                        matchAfterColumn();
                } else if ("LEFT".equalsIgnoreCase(a) || "RIGHT".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWords("OUTER", "JOIN");
                } else if ("INNER".equalsIgnoreCase(a) || "NATURAL".equalsIgnoreCase(a)
                        || "CROSS".equalsIgnoreCase(a) || "OUTER".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWord("JOIN");
                } else if ("ORDER".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWord("BY");
                } else if (a.endsWith("*")) {
                        matchAfterStar();
                } else if (isOperator(a) || "LIKE".equals(a) || "ILIKE".equals(a)) {
                        // Operator = + - / ...
                        addScalarFunctions();
                        addTables(true);
                } else if ("SIMILAR".equalsIgnoreCase(a)) {
                        addKeyWord("TO");
                } else if ("SET".equalsIgnoreCase(a)) {
                        if (!it.hasNext()) {
                                return;
                        }
                        String b = it.next();
                        addFieldsForTable(b + ".");
                } else if ("PURGE".equalsIgnoreCase(a)) {
                        // do nothing
                } else {
                        // anything else
                        matchAfterPossibleId();
                }
        }

        private void matchAfterTo() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("SIMILAR".equals(a)) {
                        addScalarFunctions();
                        addTables(true);
                }
        }

        /**
         * Decides what to do after "By identifier"
         *
         * @param start true if we are directly after BY or after a comma
         */
        private void matchIdAfterBy() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("ORDER".equalsIgnoreCase(a)) {
                        // ORDER BY
                        addKeyWords("ASC", "DESC", "LIMIT", "OFFSET", "FETCH", "UNION");
                } else if ("GROUP".equalsIgnoreCase(a)) {
                        // GROUP BY
                        addKeyWords("HAVING", "ORDER BY", "LIMIT", "OFFSET", "FETCH", "UNION");
                }
        }

        /**
         * Decides what to do after a (probable) id has been matched.
         */
        private void matchAfterPossibleId() {
                while (it.hasNext()) {
                        String b = it.next();
                        if ("SELECT".equalsIgnoreCase(b) || "(SELECT".equalsIgnoreCase(b)) {
                                addKeyWord("FROM");
                                addOperators();
                                return;
                        } else if ("VIEW".equalsIgnoreCase(b)) {
                                addKeyWord("AS");
                                return;
                        } else if ("FROM".equalsIgnoreCase(b)) {
                                addKeyWords("WHERE", "UNION", "ORDER BY", "GROUP BY", "LIMIT", "OFFSET",
                                        "FETCH", "HAVING", "JOIN", "INNER JOIN", "LEFT JOIN",
                                        "RIGHT JOIN", "CROSS JOIN", "NATURAL JOIN");
                                return;
                        } else if ("WHERE".equalsIgnoreCase(b)) {
                                addKeyWords("UNION", "ORDER BY", "GROUP BY", "LIMIT", "OFFSET", "FETCH", "HAVING");
                                addOperators();
                                return;
                        } else if ("HAVING".equalsIgnoreCase(b)) {
                                addKeyWords("UNION", "ORDER BY", "LIMIT", "OFFSET", "FETCH");
                                return;
                        } else if ("BY".equalsIgnoreCase(b)) {
                                matchIdAfterBy();
                                return;
                        } else if ("JOIN".equalsIgnoreCase(b)) {
                                addKeyWords("ON", "WHERE", "UNION", "ORDER BY", "GROUP BY", "LIMIT", "OFFSET",
                                        "FETCH", "HAVING", "JOIN", "INNER JOIN", "LEFT JOIN",
                                        "RIGHT JOIN", "CROSS JOIN", "NATURAL JOIN");
                                return;
                        } else if ("ON".equalsIgnoreCase(b)) {
                                addKeyWords("WHERE", "UNION", "ORDER BY", "GROUP BY", "LIMIT", "OFFSET",
                                        "FETCH", "HAVING", "JOIN", "INNER JOIN", "LEFT JOIN",
                                        "RIGHT JOIN", "CROSS JOIN", "NATURAL JOIN");
                                addOperators();
                                return;
                        } else if ("TABLE".equalsIgnoreCase(b)) {
                                matchAfterTableId();
                                return;
                        } else if ("INTO".equalsIgnoreCase(b)) {
                                // INSERT INTO toto
                                addKeyWord("VALUES");
                                return;
                        } else if ("UPDATE".equalsIgnoreCase(b)) {
                                // UPDATE toto SET
                                addKeyWord("SET");
                                return;
                        } else if ("COLUMN".equalsIgnoreCase(b)) {
                                // ADD/RENAME/ALTER/DROP COLUMN toto
                                matchAfterDDLColumnId();
                                return;
                        } else if ("SET".equalsIgnoreCase(b)) {
                                // UPDATE toto SET tata =
                                addKeyWord("=");
                                return;
                        }

                        if (b.contains(";")) {
                                return;
                        }
                }

        }

        /**
         * Decides what to do after a comma (or id+comma) has been match.
         */
        private void matchAfterComma() {
                while (it.hasNext()) {
                        String a = it.next();
                        if ("FROM".equalsIgnoreCase(a)) {
                                // after from, tables and table functions
                                addTables(false);
                                addTableFunctions();
                                addScalarFunctions();
                                return;
                        } else if ("SELECT".equalsIgnoreCase(a) || "(SELECT".equalsIgnoreCase(a) || "WHERE".equalsIgnoreCase(a)
                                || "SET".equalsIgnoreCase(a) || "ON".equalsIgnoreCase(a)) {
                                // after SELECT, WHERE or the sert part of an update
                                addScalarFunctions();
                                addTables(true);
                                return;
                        } else if ("EXCEPT".equalsIgnoreCase(a)) {
                                // after EXCEPT
                                addAllFields();
                                return;
                        } else if ("VALUES".equalsIgnoreCase(a) || "INTO".equalsIgnoreCase(a)) {
                                // inside an insert, no completion
                                return;
                        } else if ("BY".equalsIgnoreCase(a)) {
                                // GROUP/ORDER BY
                                addTables(true);
                                return;
                        } else if ("EXECUTE".equalsIgnoreCase(a) || "CALL".equalsIgnoreCase(a)) {
                                addScalarFunctions();
                                addTableFunctions();
                                addTables(false);
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

                if ("TABLE".equalsIgnoreCase(a) || "VIEW".equalsIgnoreCase(a)) {
                        addKeyWord("SELECT");
                        return;
                }

                while (it.hasNext()) {
                        a = it.next();
                        if ("SELECT".equalsIgnoreCase(a) || "(SELECT".equalsIgnoreCase(a)) {
                                addKeyWord("FROM");
                                return;
                        } else if ("FROM".equalsIgnoreCase(a)) {
                                return;
                        }
                }
        }

        private void matchAfterTable() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("ALTER".equalsIgnoreCase(a)) {
                        // ALTER TABLE
                        addTables(false);
                } else if ("DROP".equalsIgnoreCase(a)) {
                        // DROP TABLE
                        addKeyWord("IF EXISTS");
                        addTables(false);
                }
        }

        private void matchAfterTableId() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("CREATE".equalsIgnoreCase(a)) {
                        // CREATE TABLE toto (AS / ( )
                        addKeyWords("AS SELECT", "(");
                } else if ("DROP".equalsIgnoreCase(a)) {
                        // DROP TABLE toto PURGE
                        addKeyWord("PURGE");
                } else if ("ALTER".equalsIgnoreCase(a)) {
                        addKeyWords("DROP COLUMN", "ADD COLUMN", "RENAME");
                }
        }

        private void matchAfterStar() {
                if (!it.hasNext()) {
                        return;
                }
                String a = it.next();

                if ("SELECT".equalsIgnoreCase(a) || "(SELECT".equalsIgnoreCase(a) || a.endsWith(",")) {
                        addKeyWords("EXCEPT", "FROM");
                } else {
                        addScalarFunctions();
                        addTables(true);
                }
        }

        private void matchAfterOpenPar() {
                addKeyWord("SELECT");
                while (it.hasNext()) {
                        String b = it.next();
                        if ("SELECT".equalsIgnoreCase(b) || "(SELECT".equalsIgnoreCase(b) || "WHERE".equalsIgnoreCase(b) || "HAVING".equalsIgnoreCase(b)) {
                                addScalarFunctions();
                                addTables(true);
                                return;
                        } else if ("FROM".equalsIgnoreCase(b)) {
                                addScalarFunctions();
                                addTables(false);
                                addTableFunctions();
                                return;
                        } else if ("EXECUTE".equalsIgnoreCase(b) || "CALL".equalsIgnoreCase(b)) {
                                addScalarFunctions();
                                addTables(false);
                                addTableFunctions();
                                return;
                        }
                        if (b.contains(";")) {
                                return;
                        }
                }
        }

        private void matchAfterDrop() {
                if (!it.hasNext()) {
                        addKeyWords("TABLE", "VIEW", "INDEX", "SCHEMA");
                        return;
                }
                it.next();
                if (!it.hasNext()) {
                        addKeyWords("TABLE", "VIEW", "INDEX", "SCHEMA");
                        return;
                }
                String a = it.next();
                if ("TABLE".equalsIgnoreCase(a)) {
                        addKeyWord("COLUMN");
                } else {
                        addKeyWords("TABLE", "VIEW", "INDEX", "SCHEMA");
                }
        }

        private void matchAfterAlter() {
                if (!it.hasNext()) {
                        addKeyWord("TABLE");
                        return;
                }
                it.next();
                if (!it.hasNext()) {
                        addKeyWord("TABLE");
                        return;
                }
                String a = it.next();
                if ("TABLE".equalsIgnoreCase(a)) {
                        addKeyWord("COLUMN");
                } else {
                        addKeyWord("TABLE");
                }
        }

        private void matchAfterColumn() {
                if (!it.hasNext()) {
                        return;
                }

                String a = it.next();
                if ("DROP".equalsIgnoreCase(a) || "ALTER".equalsIgnoreCase(a)
                        || "RENAME".equalsIgnoreCase(a)) {
                        if (!it.hasNext()) {
                                return;
                        }

                        a = it.next();

                        addFieldsForTable(a + ".");
                }
        }

        private void matchAfterExists() {
                if (!it.hasNext()) {
                        return;
                }

                String a = it.next();
                if ("IF".equalsIgnoreCase(a)) {

                        if (!it.hasNext()) {
                                return;
                        }
                        String b = it.next();
                        if (!"TABLE".equalsIgnoreCase(b)) {
                                return;
                        }
                        
                        if (!it.hasNext()) {
                                return;
                        }

                        b = it.next();
                        if ("DROP".equalsIgnoreCase(b)) {
                                addTables(false);
                        }
                }
        }

        private void matchAfterDDLColumnId() {
                if (!it.hasNext()) {
                        return;
                }

                String a = it.next();
                if ("ADD".equalsIgnoreCase(a)) {
                        addAllTypes();
                } else if ("RENAME".equalsIgnoreCase(a)) {
                        addKeyWord("TO");
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

        private void addAllFields() {
                pr.addFieldNamesCompletion();
        }

        private void addKeyWords(String... k) {
                for (int i = 0; i < k.length; i++) {
                        addKeyWord(k[i]);
                }
        }

        private void addKeyWord(String k) {
                pr.addCompletion(new ShorthandCompletion(pr, k, k + ' '));
        }

        private boolean isOperator(String s) {
                return Arrays.binarySearch(operators, s.charAt(s.length() - 1)) >= 0;
        }

        private void addOperators() {
                addKeyWords("IS NULL", "IS NOT NULL", "IS TRUE", "IS FALSE", "LIKE", "AND", "OR", "ILIKE",
                        "SIMILAR TO", "BETWEEN");
        }

        private void addAllTypes() {
                Set<String> s = SQLValueFactory.getValidSQLTypes();
                for (String t : s) {
                        addKeyWord(t.toUpperCase());
                }
        }
}
