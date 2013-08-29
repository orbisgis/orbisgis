package org.orbisgis.scp;

import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.h2.util.OsgiDataSourceFactory;
import org.junit.Test;

import java.awt.*;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Nicolas Fortin
 */
public class SQLCompletionProviderTest {

    @Test //(timeout = 500)
    public void testBounds() throws Exception {
        // Create H2 DataSource
        org.h2.Driver driver = org.h2.Driver.load();
        OsgiDataSourceFactory dataSourceFactory = new OsgiDataSourceFactory(driver);
        Properties properties = new Properties();
        properties.setProperty(OsgiDataSourceFactory.JDBC_URL, RSyntaxSQLParserTest.DATABASE_PATH);

        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("alte");
        rSyntaxTextArea.setSize(new Dimension(420, 240));
        rSyntaxTextArea.setCaretPosition(4);
        SQLCompletionProvider autoComplete = new SQLCompletionProvider(dataSourceFactory.createDataSource(properties));

        List completions = autoComplete.getCompletionsAtIndex(rSyntaxTextArea, 4);
        assertEquals(1, completions.size());
        assertEquals("ALTER", ((Completion)completions.get(0)).getReplacementText());
    }
}
