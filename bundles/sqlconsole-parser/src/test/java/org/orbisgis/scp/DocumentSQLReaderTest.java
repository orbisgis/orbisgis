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

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * @author Nicolas Fortin
 */
public class DocumentSQLReaderTest {

    @Test
    public void testSplitSQLDocument() {
        assumeTrue(!GraphicsEnvironment.isHeadless());
        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("ALTER;SELECT * FROM;\nDELETE;select * from bla;");

        DocumentSQLReader documentSQLReader = new DocumentSQLReader(rSyntaxTextArea.getDocument());

        assertTrue(documentSQLReader.hasNext());
        assertEquals("ALTER",documentSQLReader.next());
        assertEquals(0, documentSQLReader.getPosition());
        assertEquals(0, documentSQLReader.getLineIndex());
        assertTrue(documentSQLReader.hasNext());
        assertEquals("SELECT * FROM",documentSQLReader.next());
        assertEquals(6, documentSQLReader.getPosition());
        assertEquals(0, documentSQLReader.getLineIndex());
        assertTrue(documentSQLReader.hasNext());
        assertEquals("\nDELETE",documentSQLReader.next());
        assertEquals(20, documentSQLReader.getPosition());
        assertEquals(0, documentSQLReader.getLineIndex());
        assertTrue(documentSQLReader.hasNext());
        assertEquals("select * from bla",documentSQLReader.next());
        assertEquals(28, documentSQLReader.getPosition());
        assertEquals(1, documentSQLReader.getLineIndex());
    }
    @Test
    public void testSplitSQLWithSpecialChar() {
        assumeTrue(!GraphicsEnvironment.isHeadless());
        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("\n\n\nINSERT INTO BLA VALUES(';hello;');\nSELECT * from test;");

        DocumentSQLReader documentSQLReader = new DocumentSQLReader(rSyntaxTextArea.getDocument());

        assertTrue(documentSQLReader.hasNext());
        assertEquals("\n\n\nINSERT INTO BLA VALUES(';hello;')",documentSQLReader.next());
        assertEquals(0, documentSQLReader.getPosition());
        assertEquals(0, documentSQLReader.getLineIndex());
        assertEquals("\nSELECT * from test",documentSQLReader.next());
        assertEquals(37, documentSQLReader.getPosition());
        assertEquals(3, documentSQLReader.getLineIndex());
    }
}
