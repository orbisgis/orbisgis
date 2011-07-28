/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
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
package org.gdms.sql.engine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.driver.ReadAccess;
import org.gdms.sql.engine.parsing.GdmSQLLexer;
import org.gdms.sql.engine.parsing.GdmSQLParser;
import org.gdms.sql.engine.parsing.GdmSQLParser.start_rule_return;

/**
 * Main entry class for parsing & processing SQL
 * @author Antoine Gourlay
 */
public final class SQLEngine {

        private SQLDataSourceFactory dsf;
        private static final Logger LOG = Logger.getLogger(SQLEngine.class);

        /**
         * Creates a new SQLEngine with the given <code>SQLDataSourceFactory</code>
         * @param dsf a SQLDataSourceFactory
         */
        public SQLEngine(SQLDataSourceFactory dsf) {
                this.dsf = dsf;
        }

        /**
         * Executes the content of the <tt>sql</tt> script.
         * @param sql a script
         * @throws ParseException if there is a problem while parsing
         */
        public void execute(String sql) throws ParseException {
                SqlStatement[] sts = parse(sql);
                for (int i = 0; i < sts.length; i++) {
                        execute(sts[i]);
                }
        }

        /**
         * Executes an SQL statement.
         * @param statement a SQL statement
         */
        public void execute(SqlStatement statement) {
                statement.prepare(dsf);
                statement.execute();
        }

        /**
         * Executes a SQL script (its first statement if it has several statements) and gets its result.
         * @param sql a SQL script
         * @return a dataset
         * @throws ParseException 
         */
        public ReadAccess query(String sql) throws ParseException {
                SqlStatement[] sts = parse(sql);
                return query(sts[0]);
        }

        /**
         * Executes a SQL Statement and gets its result.
         * @param sql
         * @return 
         */
        public ReadAccess query(SqlStatement sql) {
                sql.prepare(dsf);
                return sql.execute();
        }

        /**
         * Parses a SQL script into several SQL Statements.
         * @param sql a SQL script
         * @return an array of statements
         * @throws ParseException if there is an error while parsing
         */
        public SqlStatement[] parse(String sql) throws ParseException {
                ANTLRInputStream input;
                try {
                        input = new ANTLRCaseInsensitiveInputStream(new ByteArrayInputStream(sql.getBytes()));
                } catch (IOException ex) {
                        throw new ParseException(ex);
                }
                GdmSQLLexer lexer = new GdmSQLLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                GdmSQLParser parser = new GdmSQLParser(tokens);
                CommonTree tree;
                try {
                        final start_rule_return startRule = parser.start_rule();
                        tree = (CommonTree) startRule.getTree();
                } catch (NoViableAltException ex) {
                        throw new ParseException(getErrorLocation(ex) + " "
                                + getErrorString(ex.token, ex.getUnexpectedType(), -1), ex);
                } catch (MismatchedTokenException ex) {
                        throw new ParseException(getErrorLocation(ex) + " "
                                + getErrorString(ex.token, ex.getUnexpectedType(), ex.expecting), ex);

                } catch (RecognitionException ex) {
                        throw new ParseException(getErrorLocation(ex) + " "
                                + getErrorString(ex.token, ex.getUnexpectedType(), -1), ex);
                } catch (IllegalArgumentException ex) {
                        if (ex.getCause() instanceof RecognitionException) {
                                RecognitionException e = (RecognitionException) ex.getCause();
                                throw new ParseException(getErrorLocation(e) + " "
                                        + getErrorString(e.token, e.getUnexpectedType(), -1), ex);
                        } else {
                                throw ex;
                        }
                }
                LOG.info("Parsing: " + tree.toStringTree());
                final ExecutionGraph[] graphs = ExecutionGraphBuilder.build(tree);
                final SqlStatement[] sts = new SqlStatement[graphs.length];

                for (int i = 0; i < graphs.length; i++) {
                        sts[i] = new SqlStatement(sql, graphs[i]);
                }

                return sts;
        }

        private String getErrorLocation(RecognitionException ex) {
                StringBuilder b = new StringBuilder();
                b.append("line ");
                b.append(ex.line);
                b.append(':');
                b.append(ex.charPositionInLine);
                return b.toString();
        }

        private String getErrorString(Token token, int type, int expecting) {
                StringBuilder b = new StringBuilder();
                if (type == -1) {
                        b.append("unexpected end of query");
                } else if (type == GdmSQLParser.ID) {
                        b.append("found identifer '").append(token.getText()).append('\'');
                } else {
                        b.append("found ").append(displayTokenName(type));

                }
                if (expecting != -1) {
                        b.append(", expected ").append(displayTokenName(expecting));

                } else {
                        b.append(", unexpected at this position");
                }
                b.append('.');
                return b.toString();
        }

        private String displayTokenName(int token) {
                String str = GdmSQLParser.tokenNames[token];
                str = str.replace("T_", "");
                if (str.equalsIgnoreCase("semi")) {
                        str = "';'";
                } else if (str.equalsIgnoreCase("comma")) {
                        str = "','";
                } else if (str.equalsIgnoreCase("lparen")) {
                        str = "'('";
                } else if (str.equalsIgnoreCase("rparen")) {
                        str = "')'";
                } else if (str.equalsIgnoreCase("eq")) {
                        str = "'='";
                }
                return str;
        }
}
