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
