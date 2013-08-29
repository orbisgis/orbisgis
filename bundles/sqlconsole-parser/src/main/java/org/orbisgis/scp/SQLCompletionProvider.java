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
package org.orbisgis.scp;

import java.awt.Point;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import javax.swing.text.JTextComponent;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProviderBase;
import org.h2.api.JdbcParseSQLException;
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
    private DataSource ds;
    private Logger log = LoggerFactory.getLogger(SQLCompletionProvider.class);

    public SQLCompletionProvider(DataSource ds) {
        this.ds = ds;
    }

    @Override
    protected List getCompletionsImpl(JTextComponent comp) {
        return getCompletionsAtIndex(comp, comp.getCaretPosition());
    }

    @Override
    public String getAlreadyEnteredText(JTextComponent jTextComponent) {
        //Returns the text just before the current caret position that could be the start of something auto-completable.
        int charIndex = jTextComponent.getCaretPosition();
        // Extract the statement at this position
        DocumentSQLReader documentReader = new DocumentSQLReader(jTextComponent.getDocument());
        String statement = "";
        while(documentReader.hasNext() && documentReader.getPosition() + statement.length() < charIndex) {
            statement = documentReader.next();
        }
        int completionPosition = charIndex - documentReader.getPosition();
        String partialStatement = statement.substring(0, completionPosition);
        int[] lastWord = RSyntaxSQLParser.getLastWordPositionAndLength(statement, completionPosition);
        if(lastWord != null && !partialStatement.endsWith(" ")) {
            return partialStatement.substring(lastWord[RSyntaxSQLParser.WORD_POSITION],
                    lastWord[RSyntaxSQLParser.WORD_POSITION]+lastWord[RSyntaxSQLParser.WORD_LENGTH]);
        } else {
            return "";
        }
    }

    public List<Completion> getCompletionsAtIndex(JTextComponent jTextComponent, int charIndex) {
        List<Completion> completionList = new LinkedList<Completion>();

        // Extract the statement at this position
        DocumentSQLReader documentReader = new DocumentSQLReader(jTextComponent.getDocument());
        String statement = "";
        while(documentReader.hasNext() && documentReader.getPosition() + statement.length() < charIndex) {
            statement = documentReader.next();
        }
        try {
            Connection connection = ds.getConnection();
            try {
                String wordBegin = "";
                try {
                    int completionPosition = charIndex - documentReader.getPosition();
                    String partialStatement = statement.substring(0, completionPosition);
                    int[] lastWord = RSyntaxSQLParser.getLastWordPositionAndLength(statement, completionPosition);
                    if(lastWord != null && !partialStatement.endsWith(" ")) {
                        wordBegin = partialStatement.substring(lastWord[RSyntaxSQLParser.WORD_POSITION],
                                lastWord[RSyntaxSQLParser.WORD_POSITION]+lastWord[RSyntaxSQLParser.WORD_LENGTH]);
                    }
                    connection.prepareStatement(partialStatement);
                } catch (SQLException ex) {
                    if(ex instanceof JdbcParseSQLException) {
                        // If we can obtain the parse error character index
                        JdbcParseSQLException parseEx = (JdbcParseSQLException) ex;
                        for(String token : parseEx.getExpectedTokens()) {
                            if(token.toUpperCase().startsWith(wordBegin.toUpperCase())) {
                                Completion completion = new BasicCompletion(this, token);
                                completionList.add(completion);
                            }
                        }
                    }
                }
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            // Cannot establish a connection
            log.warn(ex.getLocalizedMessage(), ex);
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
}
