package org.orbisgis.scp;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.h2.util.OsgiDataSourceFactory;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * @author Nicolas Fortin
 */
public class RSyntaxSQLParserTest {
    private static final String DATABASE_PATH = "jdbc:h2:target/parserTest;LOCK_MODE=0;LOG=0";

    @Test //(timeout = 500)
    public void testParseException() throws Exception {
        // Create H2 DataSource
        org.h2.Driver driver = org.h2.Driver.load();
        OsgiDataSourceFactory dataSourceFactory = new OsgiDataSourceFactory(driver);
        Properties properties = new Properties();
        properties.setProperty(OsgiDataSourceFactory.JDBC_URL, DATABASE_PATH);

        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("ALTER;SELECT * FROM;\nDELETE;");
        RSyntaxSQLParser parser = new RSyntaxSQLParser(dataSourceFactory.createDataSource(properties), rSyntaxTextArea);

        ParseResult res = parser.parse((RSyntaxDocument)rSyntaxTextArea.getDocument(), "");
        List noticeList = res.getNotices();
        assertEquals(3, noticeList.size());
    }
}
