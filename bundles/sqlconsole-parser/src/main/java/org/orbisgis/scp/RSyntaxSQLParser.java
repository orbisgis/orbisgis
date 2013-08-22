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

import javax.sql.DataSource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.h2.api.SQLParseException;
import org.h2.util.ScriptReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A parser for SQL syntax that provides error locations.
 * @author Antoine Gourlay
 * @author Nicolas Fortin
 */
public class RSyntaxSQLParser extends AbstractParser {
    private RSyntaxTextArea textArea;
    private DataSource ds;
    private Logger log = LoggerFactory.getLogger(RSyntaxSQLParser.class);

    /**
     * Constructor
     * @param ds Active DataSource
     * @param textArea Component
     */
    public RSyntaxSQLParser(DataSource ds, RSyntaxTextArea textArea) {
        this.ds = ds;
        this.textArea = textArea;
        // Init parser with H2 and PostgreSQL features
    }

    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        DefaultParseResult res = new DefaultParseResult(this);
        if (doc.getLength()==0) {
            return res;
        }
        DocumentSQLReader documentReader = new DocumentSQLReader(doc);
        long start = System.currentTimeMillis();
        try {
            Connection connection = ds.getConnection();
            try {
                while(documentReader.hasNext()) {
                    String statement = documentReader.next();
                    if(!documentReader.isInsideRemark()) {
                        try {
                            connection.prepareStatement(statement);
                        } catch (SQLException ex) {
                            if(ex instanceof SQLParseException) {
                                // If we can obtain the parse error character index
                                SQLParseException parseEx = (SQLParseException) ex;
                                // TODO compute the length of the word before the error position, position may be position from the beginning of line
                                DefaultParserNotice notice = new DefaultParserNotice(this, ex.getLocalizedMessage(), textArea.getLineOfOffset(documentReader.getPosition()),parseEx.getSyntaxErrorPosition(),1);
                                notice.setLevel(ParserNotice.ERROR);
                                res.addNotice(notice);
                            }
                        }
                    }
                }
            } finally {
                connection.close();
            }
        } catch (SQLException ex) {
            log.trace(ex.getLocalizedMessage(), ex);
            // ignore
        } catch (BadLocationException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        long time = System.currentTimeMillis() - start;
        res.setParseTime(time);
        return res;
    }
}
