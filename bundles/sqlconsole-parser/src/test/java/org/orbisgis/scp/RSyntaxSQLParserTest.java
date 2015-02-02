package org.orbisgis.scp;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.h2.util.OsgiDataSourceFactory;
import org.junit.Test;
import sun.awt.HeadlessToolkit;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

/**
 * @author Nicolas Fortin
 */
public class RSyntaxSQLParserTest {
    public static final String DATABASE_PATH = "jdbc:h2:mem:syntax";

    //@Test(timeout = 500)
    public void testParseException() throws Exception {
        assumeTrue(!GraphicsEnvironment.isHeadless());
        // Create H2 DataSource
        org.h2.Driver driver = org.h2.Driver.load();
        OsgiDataSourceFactory dataSourceFactory = new OsgiDataSourceFactory(driver);
        Properties properties = new Properties();
        properties.setProperty(OsgiDataSourceFactory.JDBC_URL, DATABASE_PATH);

        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("ALTER;SELECT * FROM;\n" +
                                "DELETE;select * from bla;");
        RSyntaxSQLParser parser = new RSyntaxSQLParser(dataSourceFactory.createDataSource(properties));

        ParseResult res = parser.parse((RSyntaxDocument)rSyntaxTextArea.getDocument(), "");
        List noticeList = res.getNotices();
        assertEquals(4, noticeList.size());
        ParserNotice notice = (ParserNotice) noticeList.get(0);

        assertEquals(0, notice.getLine());
        assertEquals(0, notice.getOffset());
        assertEquals(5, notice.getLength());

        // Expect Table identifier after FROM
        // Add red underline with the word FROM
        notice = (ParserNotice) noticeList.get(1);
        assertEquals(0, notice.getLine());
        assertEquals(15, notice.getOffset());
        assertEquals(4, notice.getLength());

        notice = (ParserNotice) noticeList.get(2);
        assertEquals(1, notice.getLine());
        assertEquals(21, notice.getOffset());

        rSyntaxTextArea.setText("create tabre (id integer);");
        res = parser.parse((RSyntaxDocument)rSyntaxTextArea.getDocument(), "");
        noticeList = res.getNotices();
        assertEquals(1, noticeList.size());
        notice = (ParserNotice) noticeList.get(0);
        assertEquals(0, notice.getLine());
        assertEquals(7, notice.getOffset());

    }

    @Test //(timeout = 500)
    public void testBounds() throws Exception {
        assumeTrue(!GraphicsEnvironment.isHeadless());
        // Create H2 DataSource
        org.h2.Driver driver = org.h2.Driver.load();
        OsgiDataSourceFactory dataSourceFactory = new OsgiDataSourceFactory(driver);
        Properties properties = new Properties();
        properties.setProperty(OsgiDataSourceFactory.JDBC_URL, DATABASE_PATH);

        // Create document
        RSyntaxDocument document = new RSyntaxDocument("sql");
        RSyntaxTextArea rSyntaxTextArea = new RSyntaxTextArea(document);
        rSyntaxTextArea.setText("alter");
        RSyntaxSQLParser parser = new RSyntaxSQLParser(dataSourceFactory.createDataSource(properties));

        ParseResult res = parser.parse((RSyntaxDocument)rSyntaxTextArea.getDocument(), "");
        List noticeList = res.getNotices();
        assertEquals(1, noticeList.size());
        ParserNotice notice = (ParserNotice) noticeList.get(0);
        assertEquals(0, notice.getLine());
        assertEquals(0, notice.getOffset());
        assertEquals(5, notice.getLength());
    }
}
