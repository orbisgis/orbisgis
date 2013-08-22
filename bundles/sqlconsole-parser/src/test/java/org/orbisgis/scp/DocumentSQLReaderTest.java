package org.orbisgis.scp;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Nicolas Fortin
 */
public class DocumentSQLReaderTest {

    @Test
    public void testSplitSQLDocument() {
        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("ALTER;SELECT * FROM;\nDELETE;select * from bla;");

        DocumentSQLReader documentSQLReader = new DocumentSQLReader(rSyntaxTextArea.getDocument());

        assertTrue(documentSQLReader.hasNext());
        assertEquals("ALTER",documentSQLReader.next());
        assertEquals(6, documentSQLReader.getPosition());
        assertEquals(0, documentSQLReader.getLineIndex());
        assertTrue(documentSQLReader.hasNext());
        assertEquals("SELECT * FROM",documentSQLReader.next());
        assertEquals(20, documentSQLReader.getPosition());
        assertEquals(0, documentSQLReader.getLineIndex());
        assertTrue(documentSQLReader.hasNext());
        assertEquals("\nDELETE",documentSQLReader.next());
        assertEquals(28, documentSQLReader.getPosition());
        assertEquals(1, documentSQLReader.getLineIndex());
        assertTrue(documentSQLReader.hasNext());
        assertEquals("select * from bla",documentSQLReader.next());
        assertEquals(46, documentSQLReader.getPosition());
        assertEquals(1, documentSQLReader.getLineIndex());
    }
    @Test
    public void testSplitSQLWithSpecialChar() {
        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("\n\n\nINSERT INTO BLA VALUES(';hello;');\nSELECT * from test;");

        DocumentSQLReader documentSQLReader = new DocumentSQLReader(rSyntaxTextArea.getDocument());

        assertTrue(documentSQLReader.hasNext());
        assertEquals("\n\n\nINSERT INTO BLA VALUES(';hello;')",documentSQLReader.next());
        assertEquals(37, documentSQLReader.getPosition());
        assertEquals(3, documentSQLReader.getLineIndex());
        assertEquals("\nSELECT * from test",documentSQLReader.next());
        assertEquals(57, documentSQLReader.getPosition());
        assertEquals(4, documentSQLReader.getLineIndex());
    }
}
