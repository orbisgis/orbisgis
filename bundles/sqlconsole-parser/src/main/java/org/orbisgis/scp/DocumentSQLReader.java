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

import org.h2.util.ScriptReader;
import org.orbisgis.sqlparserapi.ScriptSplitter;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import java.io.IOException;
import java.io.Reader;

/**
 * Iterate over statements in document
 * @author Nicolas Fortin
 */
public class DocumentSQLReader implements ScriptSplitter {

    private Document document;
    private int position = 0;
    private ScriptReader scriptReader;
    private String statement;
    private String nextStatement;
    private boolean commentStatement;
    private boolean nextCommentStatement;

    public DocumentSQLReader(Document document) {
        this(document, false);
    }

    public DocumentSQLReader(Document document, boolean skipRemarks) {
        this.document = document;
        DocumentReader documentReader = new DocumentReader(document);
        scriptReader = new ScriptReader(documentReader);
        scriptReader.setSkipRemarks(skipRemarks);
        nextStatement = scriptReader.readStatement();
        nextCommentStatement = scriptReader.isInsideRemark();
    }

    @Override
    public boolean hasNext() {
        return nextStatement!=null;
    }

    @Override
    public int getLineIndex() {
        return getLineIndex(position);
    }

    @Override
    public int getLineIndex(int charOffset) {
        Element map = document.getDefaultRootElement();
        return map.getElementIndex(charOffset);
    }

    @Override
    public boolean isInsideRemark() {
        return commentStatement;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public String next() {
        if(statement != null) {
            position += statement.length() + 1;
        }
        statement = nextStatement;
        commentStatement = nextCommentStatement;
        nextStatement = scriptReader.readStatement();
        nextCommentStatement = scriptReader.isInsideRemark();
        return statement;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("It is a SQL reader");
    }

    /**
     * Wrap a Document into a Reader.
     */
    private static class DocumentReader extends Reader {
        private Document document;
        private int offset=0;

        /**
         * @param document Document to read
         */
        public DocumentReader(Document document) {
            this.document = document;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int totalLength = document.getLength();
            if(offset >= totalLength) {
                return -1;
            }
            int effectiveLen = Math.min(len, totalLength - offset);
            try {
                String text = document.getText(offset, effectiveLen);
                text.getChars(0,text.length(),cbuf,off);
            } catch (BadLocationException ex) {
                throw new IOException(ex);
            } catch(ArrayIndexOutOfBoundsException ex) {
                // Should not happen, this error give more information than stack trace
                throw new IOException("DocumentReader.read(buff,"+off+","+len+") offset="+offset, ex);
            }
            offset += effectiveLen;
            // return -1 if end of document
            return effectiveLen;
        }
    }
}
