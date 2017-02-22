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

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.h2.bnf.Bnf;
import org.h2.bnf.RuleHead;
import org.h2.bnf.RuleList;
import org.h2.bnf.Sentence;
import org.h2.bnf.context.DbContents;
import org.h2.bnf.context.DbContextRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SQL CompletionProvider based on a simple Lexer and a simple top-down parser.
 * 
 * @author Antoine Gourlay
 * @author Nicolas Fortin
 * @since 4.0
 */
public class SQLCompletionProvider extends CompletionProviderBase {
    private DataSource dataSource;
    private Bnf parser;
    private Logger log = LoggerFactory.getLogger(SQLCompletionProvider.class);
    private static final int UPDATE_INTERVAL = 30000; // ms, metadata update interval
    private static final int UPDATE_TIMEOUT = 1000;
    private long lastUpdate = 0;
    private UpdateParserThread fetchingParser = null;
    private final static Pattern LTRIM = Pattern.compile("^\\s+");

    public SQLCompletionProvider(DataSource dataSource, boolean immediateInit) {
        this.dataSource = dataSource;
        try {
            // Use h2 internal grammar
            updateParser(dataSource, immediateInit);
        } catch (Exception ex) {
            log.warn("Could not load auto-completion engine", ex);
        }
    }
    /**
     * Set the DataSource used by this Parser
     * @param ds
     */
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
        try {
            updateParser(ds, false);
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    /***
     * Read the data source in order to update parser grammar.
     * @param dataSource New DataSource, to extract meta data, can be null
     */
    public void updateParser(DataSource dataSource, boolean immediateInit) throws SQLException, IOException {
        if(dataSource != null) {
            lastUpdate = System.currentTimeMillis();
            fetchingParser = new UpdateParserThread(dataSource);
            if(!immediateInit) {
                fetchingParser.execute();
            } else {
                fetchingParser.run();
            }
        }
    }

    @Override
    protected List getCompletionsImpl(JTextComponent comp) {
        return getCompletionsAtIndex(comp, comp.getCaretPosition());
    }

    @Override
    public String getAlreadyEnteredText(JTextComponent jTextComponent) {
        return "";
    }


    public List<Completion> getCompletionsAtIndex(JTextComponent jTextComponent, int charIndex) {
        UpdateParserThread fetchingParserTmp = fetchingParser;
        if(fetchingParserTmp != null) {
            if(!fetchingParserTmp.isDone()) {
                try {
                    // Wait until update is done
                    fetchingParserTmp.get(UPDATE_TIMEOUT, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    return new ArrayList<>();
                }
            } else {
                parser = fetchingParserTmp.getParser();
            }
        } if(parser == null) {
            return new ArrayList<>();
        }
        //Completion completion = new BasicCompletion(this, token);
        List<Completion> completionList = new LinkedList<Completion>();

        // Extract the statement at this position
        DocumentSQLReader documentReader = new DocumentSQLReader(jTextComponent.getDocument(), true);
        String statement = "";
        while(documentReader.hasNext() && documentReader.getPosition() + statement.length() < charIndex) {
            statement = documentReader.next();
        }
        // Last word for filtering results
        int completionPosition = charIndex - documentReader.getPosition();
        String partialStatement = LTRIM.matcher(statement.substring(0, completionPosition)).replaceAll("");
        // Ask parser for completion list
        // Left trim the string
        Map<String,String> autoComplete = parser.getNextTokenList(partialStatement);
        for(Map.Entry<String, String> entry : autoComplete.entrySet()) {
            String token =  entry.getKey().substring(entry.getKey().indexOf("#") + 1);
            Completion completion = new BnfAutoCompletion(this, token, entry.getValue());
            completionList.add(completion);
        }

        // Update table list if it has not be done more than UPDATE_INTERVAL ms ago
        long now = System.currentTimeMillis();
        if(lastUpdate + UPDATE_INTERVAL < now) {
            try {
                updateParser(dataSource, false);
            } catch (Exception ex) {
                log.warn("Could not update auto-completion engine", ex);
            }
        }
        return completionList;
    }

    @Override
    public List getCompletionsAt(JTextComponent jTextComponent, Point point) {
        int pos = jTextComponent.viewToModel(point);
        if(pos==-1) {
            return null;
        } else {
            return getCompletionsAtIndex(jTextComponent, pos);
        }
    }

    @Override
    public List getParameterizedCompletions(JTextComponent jTextComponent) {
        return null;
    }

    private static class BnfAutoCompletion extends BasicCompletion {
        private String append;

        private BnfAutoCompletion(CompletionProvider provider, String completeToken, String append) {
            super(provider, completeToken);
            this.append = append;
        }

        @Override
        public String getAlreadyEntered(JTextComponent comp) {
            String completeToken = getReplacementText();
            return completeToken.substring(0, completeToken.length() - append.length());
        }
    }

    private static class UpdateParserThread extends SwingWorker<Object, Object> {
        private static final Logger LOGGER = LoggerFactory.getLogger(UpdateParserThread.class);
        private DataSource dataSource;
        private Bnf parser;


        public UpdateParserThread(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Bnf getParser() {
            return parser;
        }

        @Override
        protected Object doInBackground() {
            try (Connection connection = dataSource.getConnection()) {
                // Read copy of h2 syntax, temporary until #118 not merged
                // https://github.com/h2database/h2database/pull/118
                try(InputStreamReader fileReader = new InputStreamReader(SQLCompletionProvider.class.getResourceAsStream("exthelp.csv"))) {
                    parser = Bnf.getInstance(fileReader);
                }
                DbContents contents = new DbContents();
                contents.readContents(connection.getMetaData().getURL(), connection);
                DbContextRule columnRule = new DbContextRule(contents, DbContextRule.COLUMN);
                DbContextRule newAliasRule = new DbContextRule(contents, DbContextRule.NEW_TABLE_ALIAS);
                DbContextRule aliasRule = new DbContextRule(contents, DbContextRule.TABLE_ALIAS);
                DbContextRule tableRule = new DbContextRule(contents, DbContextRule.TABLE);
                DbContextRule schemaRule = new DbContextRule(contents, DbContextRule.SCHEMA);
                DbContextRule columnAliasRule = new DbContextRule(contents, DbContextRule.COLUMN_ALIAS);
                DbContextRule procedureRule = new DbContextRule(contents, DbContextRule.PROCEDURE);

                parser.updateTopic("procedure", procedureRule);
                parser.updateTopic("new_table_alias", newAliasRule);
                parser.updateTopic("table_alias", aliasRule);
                parser.updateTopic("column_alias", columnAliasRule);
                parser.updateTopic("schema_name", schemaRule);
                parser.updateTopic("table_name", tableRule);
                parser.updateTopic("column_name", columnRule);
                parser.linkStatements();
            } catch (SQLException|IOException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            return null;
        }
    }
}
