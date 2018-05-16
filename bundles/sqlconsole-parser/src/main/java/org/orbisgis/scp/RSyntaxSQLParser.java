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
package org.orbisgis.scp;

import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.text.Document;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.parser.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser for SQL syntax that provides error locations.
 * @author Antoine Gourlay
 * @author Nicolas Fortin
 */
public class RSyntaxSQLParser extends AbstractParser {
    private DataSource dataSource;
    private Logger log = LoggerFactory.getLogger(RSyntaxSQLParser.class);
    public static int WORD_POSITION = 0;
    public static int WORD_LENGTH = 1;
    private DefaultParseResult lastParsedResult = new DefaultParseResult(this);
    private ExecutorService executor;
    // Time in ms to wait before returning a result
    // Too low value will always return the old parse result
    // Too high value will lag user interface
    private static final long MINIMAL_WAITING_TIME_PARSE = 50;
    private AtomicBoolean backgroundParsing = new AtomicBoolean(false);

    /**
     * Constructor
     * @param dataSource Active DataSource
     */
    public RSyntaxSQLParser(DataSource dataSource, ExecutorService executor) {
        this.dataSource = dataSource;
        this.executor = executor;
    }

    /**     *
     * @param statement SQL Statement
     * @param end Find end position
     * @return Last word position and length {@link RSyntaxSQLParser#WORD_POSITION} {@link RSyntaxSQLParser#WORD_LENGTH},
     * null if no word was found.
     */
    public static int[] getLastWordPositionAndLength(String statement, int end) {
        int[] res = new int[2];
        res[WORD_POSITION] = -1;
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(statement);
        while(m.find() && m.start() < end) {
            res[WORD_POSITION] = m.start();
            res[WORD_LENGTH] = m.group().length();
        }
        if(res[WORD_POSITION]!=-1) {
            return res;
        } else {
            return null;
        }
    }

    /**
     * @param dataSource Linked DataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        DefaultParseResult res = new DefaultParseResult(this);
        int docLength = doc.getLength();
        if (docLength==0) {
            lastParsedResult = res;
            return res;
        } else {
            // Do not run multiple time the parsing of the sql
            if(backgroundParsing.compareAndSet(false, true)) {
                // Launch evaluation thread but return last evaluation
                DelayedParsed parse = new DelayedParsed(doc, dataSource, this);
                if (executor != null) {
                    executor.execute(parse);
                    try {
                        Thread.sleep(MINIMAL_WAITING_TIME_PARSE);
                    } catch (InterruptedException ex) {
                        return lastParsedResult;
                    }
                } else {
                    parse.run();
                }
            }
            return lastParsedResult;
        }
    }

    private static final class DelayedParsed implements Runnable {
        private Document doc;
        private DataSource dataSource;
        private RSyntaxSQLParser parser;
        private Logger log = LoggerFactory.getLogger(DelayedParsed.class);

        public DelayedParsed(Document doc, DataSource dataSource, RSyntaxSQLParser parser) {
            this.doc = doc;
            this.dataSource = dataSource;
            this.parser = parser;
        }

        @Override
        public void run() {
            DefaultParseResult res = new DefaultParseResult(parser);
            int docLength = doc.getLength();
            DocumentSQLReader documentReader = new DocumentSQLReader(doc);
            long start = System.currentTimeMillis();
            try {
                try (Connection connection = dataSource.getConnection()) {
                    while (documentReader.hasNext()) {
                        String statement = documentReader.next();
                        if (!documentReader.isInsideRemark()) {
                            try {
                                connection.prepareStatement(statement);
                            } catch (SQLException ex) {
                                // Find the beginning of the rightmost word in error
                                int syntaxErrorPosition = ex.getLocalizedMessage().indexOf("[*]");
                                int syntaxErrorLength = 0;
                                if (syntaxErrorPosition == -1) {
                                    // Could not find exact position, underline all the statement (remove preceding line break)
                                    syntaxErrorPosition = statement.indexOf(statement.trim());
                                    syntaxErrorLength = statement.length() - syntaxErrorPosition;
                                } else {
                                    int[] syntaxWord = getLastWordPositionAndLength(ex.getLocalizedMessage(), syntaxErrorPosition);
                                    if(syntaxWord != null) {
                                        String word = ex.getLocalizedMessage().substring(syntaxWord[WORD_POSITION], syntaxWord[WORD_POSITION] + syntaxWord[WORD_LENGTH]);
                                        syntaxErrorPosition = statement.toLowerCase().indexOf(word.toLowerCase());
                                        syntaxErrorLength = syntaxWord[WORD_LENGTH];
                                    }
                                }
                                // Compute syntax error position from the beginning of the document, (-1 is length of ; char)
                                int syntaxErrorPositionOffset = Math.min(docLength,
                                        documentReader.getPosition() + syntaxErrorPosition);
                                DefaultParserNotice notice = new DefaultParserNotice(parser, ex.getLocalizedMessage(),
                                        documentReader.getLineIndex(syntaxErrorPositionOffset), syntaxErrorPositionOffset,
                                        syntaxErrorLength);
                                notice.setLevel(ParserNotice.Level.ERROR);
                                res.addNotice(notice);
                            }
                        }
                    }
                }
            } catch (SQLException ex) {
                log.trace(ex.getLocalizedMessage(), ex);
                // ignore
            }
            long time = System.currentTimeMillis() - start;
            res.setParseTime(time);
            parser.lastParsedResult = res;
            parser.backgroundParsing.set(false);
        }
    }
}
