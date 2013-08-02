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
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.h2.util.ScriptReader;

import java.io.IOException;
import java.io.Reader;

/**
 * A parser for SQL syntax that provides error locations.
 * This parser use EXPLAIN SQL command, and parse SQLException.
 * @author Antoine Gourlay
 * @author Nicolas Fortin
 */
public class SQLParser extends AbstractParser {
    private RSyntaxTextArea textArea;
    private DataSource dataSource;

    /**
     * Constructor
     * @param dataSource Active DataSOurce
     * @param textArea Component
     */
    public SQLParser(DataSource dataSource, RSyntaxTextArea textArea) {
        this.dataSource = dataSource;
        this.textArea = textArea;
    }

    @Override
    public ParseResult parse(RSyntaxDocument doc, String style) {
        String content;
        try {
            DefaultParseResult res = new DefaultParseResult(this);
            if (doc.getLength()==0) {
                return res;
            }
            DocumentReader documentReader = new DocumentReader(doc);
            ScriptReader scriptReader = new ScriptReader(documentReader);

            long start = System.currentTimeMillis();

            long time = System.currentTimeMillis() - start;
            res.setParseTime(time);

            return res;
        } catch (BadLocationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Wrap a Document into a Reader.
     */
    private static class DocumentReader extends Reader {
        private Document document;

        public DocumentReader(Document document) {
            this.document = document;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int totalLength = document.getLength();
            int effectiveLen = Math.min(len, totalLength - off);
            try {
                String text = document.getText(off, effectiveLen);
                text.getChars(0,text.length(),cbuf,0);
            } catch (BadLocationException ex) {
                throw new IOException(ex);
            }
            // return -1 if end of document
            return off + effectiveLen <= totalLength ? effectiveLen : -1;
        }
    }
}
