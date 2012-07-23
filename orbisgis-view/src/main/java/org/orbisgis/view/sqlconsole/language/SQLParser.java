/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.views.sqlConsole.language;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.swing.text.BadLocationException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;

import org.gdms.sql.engine.ANTLRCaseInsensitiveInputStream;
import org.gdms.sql.parser.GdmSQLLexer;
import org.gdms.sql.parser.GdmSQLParser;

/**
 * A parser for Gdms SQL syntax that provides error locations.
 *
 * @author Antoine Gourlay
 */
public class SQLParser extends AbstractParser {

        private RSyntaxTextArea textArea;

        SQLParser(RSyntaxTextArea textArea) {
                this.textArea = textArea;
        }

        @Override
        public ParseResult parse(RSyntaxDocument doc, String style) {
                String content;
                try {
                        content = doc.getText(0, doc.getLength());

                        DefaultParseResult res = new DefaultParseResult(this);
                        if (content.isEmpty()) {
                                return res;
                        }

                        long start = System.currentTimeMillis();

                        String[] statements = content.split(";");
                        int totalLength = 0;
                        for (int i = 0; i < statements.length - 1; i++) {
                                statements[i] += ";";
                                String trimmed = statements[i].trim();

                                if (trimmed.startsWith("--")) {
                                        totalLength += statements[i].length();
                                        continue;
                                }

                                // 0: current absolute start position
                                // 1: current trimmed absolute start position
                                // 2: length of the current section
                                // 3: length of the trimmed section
                                int[] info = new int[4];
                                info[0] = totalLength;
                                info[1] = totalLength + statements[i].indexOf(trimmed);
                                info[2] = statements[i].length();
                                info[3] = trimmed.length();
                                ParserNotice p = getError(statements[i], info);
                                if (p != null) {
                                        res.addNotice(p);
                                }

                                totalLength += statements[i].length();
                        }

                        if (!statements[statements.length - 1].trim().isEmpty()) {
                                if (content.trim().endsWith(";")) {
                                        statements[statements.length - 1] += ";";
                                }
                                String trimmed = statements[statements.length - 1].trim();

                                if (!trimmed.startsWith("--")) {
                                        // 0: current absolute start position
                                        // 1: current trimmed absolute start position
                                        // 2: length of the current section
                                        // 3: length of the trimmed section
                                        int[] info = new int[4];
                                        info[0] = totalLength;
                                        info[1] = totalLength + statements[statements.length - 1].indexOf(trimmed);

                                        info[2] = statements[statements.length - 1].length();
                                        info[3] = trimmed.length();
                                        ParserNotice p = getError(statements[statements.length - 1], info);
                                        if (p != null) {
                                                res.addNotice(p);
                                        }
                                }
                        }

                        long time = System.currentTimeMillis() - start;
                        res.setParseTime(time);

                        return res;
                } catch (BadLocationException ex) {
                        throw new IllegalStateException(ex);
                }
        }

        private ParserNotice getError(String content, int[] info) throws BadLocationException {
                if (content.isEmpty()) {
                        return null;
                }

                // getting the engine and parsing the sql statement
                ANTLRInputStream input = null;
                try {
                        input = new ANTLRCaseInsensitiveInputStream(new ByteArrayInputStream(content.getBytes()));
                } catch (IOException ex) {
                        // never happens
                        throw new IllegalStateException(ex);
                }
                GdmSQLLexer lexer = new GdmSQLLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                GdmSQLParser parser = new GdmSQLParser(tokens);

                try {
                        parser.start_rule();
                } catch (MismatchedTokenException e) {
                        int[] location = getErrorLocationAndLength(e, info);
                        DefaultParserNotice not = new DefaultParserNotice(this, getErrorLocationText(location[4], location[1]) + " "
                                + getErrorString(e.token, e.getUnexpectedType(), e.expecting), location[0], location[2], location[3]);
                        return not;
                } catch (RecognitionException e) {
                        int[] location = getErrorLocationAndLength(e, info);
                        DefaultParserNotice not = new DefaultParserNotice(this, getErrorLocationText(location[0], location[1]) + " "
                                + getErrorString(e.token, e.getUnexpectedType(), -1), location[0], location[2], location[3]);
                        return not;
                } catch (IllegalArgumentException ex) {
                        if (ex.getCause() instanceof RecognitionException) {
                                RecognitionException e = (RecognitionException) ex.getCause();
                                int[] location = getErrorLocationAndLength(e, info);
                                DefaultParserNotice not = new DefaultParserNotice(this, getErrorLocationText(location[4], location[1]) + " "
                                        + getErrorString(e.token, e.getUnexpectedType(), -1), location[0], location[2], location[3]);
                                return not;
                        }
                }

                return null;
        }

        private int[] getErrorLocationAndLength(RecognitionException ex, int[] info) throws BadLocationException {
                // input info
                // 0: current absolute start position
                // 1: current trimmed absolute start position
                // 2: length of the current section
                // 3: length of the trimmed section

                int[] loc = new int[5];
                // 0: line number
                // 1: char in line for error location text
                // 2: char in the whole textArea for the underline
                // 3: length of the highlighted part
                // 4: line number for error location text

                loc[0] = textArea.getLineOfOffset(info[1]);
                loc[4] = textArea.getLineOfOffset(info[0]) + ex.line - 1;
                final int startOfLine = textArea.getLineStartOffset(loc[4]);
                loc[1] = info[0] - startOfLine;

                if (loc[1] < 0) {
                        loc[1] = 0;
                }

                if (ex.charPositionInLine != -1) {
                        loc[1] += ex.charPositionInLine;
                }

                if (ex.token != null && ex.token.getType() != -1) {
                        loc[0] = loc[4];
                        loc[2] = startOfLine + loc[1];
                        loc[3] = ex.token.getText().length();
                } else {
                        loc[2] = info[1];
                        loc[3] = info[3];
                }

                return loc;
        }

        private String getErrorLocationText(int line, int pos) {
                StringBuilder b = new StringBuilder();
                b.append(line + 1);
                b.append(':');
                b.append(pos);
                return b.toString();
        }

        private String getErrorString(Token token, int type, int expecting) {
                StringBuilder b = new StringBuilder();
                if (type == -1) {
                        b.append("unexpected end of query");
                } else if (type == GdmSQLParser.LONG_ID) {
                        b.append("found identifer '").append(token.getText()).append('\'');
                } else {
                        b.append("found ").append(displayTokenName(type));

                }
                if (expecting != -1) {
                        b.append(", expected ").append(displayTokenName(expecting));

                } else if (type != -1) {
                        b.append(", unexpected here");
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
