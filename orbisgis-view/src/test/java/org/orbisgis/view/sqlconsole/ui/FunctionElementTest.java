package org.orbisgis.view.sqlconsole.ui;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * @author Adam Gouge
 */
public class FunctionElementTest {
    private static Connection connection;
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SpatialH2UT.createDataSource("FunctionElementTest", false);
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testMarkdownTooltipGeneration() throws SQLException {
        final String markdownRemarks = "Here is an *example* of some markdown text:\n" +
                "* Item 1\n" +
                "* Item 2\n" +
                "\nMake sure to skip a line after unordered lists.";
        FunctionElement f = new FunctionElement("EXAMPLE_FUNCTION",
                DatabaseMetaData.procedureReturnsResult,
                markdownRemarks,
                dataSource);
        // Remarks written in Markdown left untouched in the SQL console comments.
        assertEquals(markdownRemarks, f.getSQLRemarks());
        // Remarks written in Markdown are converted to HTML in the tooltip.
        assertEquals("<p>Here is an <em>example</em> of some markdown text:</p>\n" +
                        "<ul>\n" +
                        "<li>Item 1</li>\n" +
                        "<li>Item 2</li>\n" +
                        "</ul>\n" +
                        "<p>Make sure to skip a line after unordered lists.</p>\n",
                f.getToolTip()
        );
    }

    @Test
    public void testHTMLTooltipGeneration() throws SQLException {
        final String hTMLRemarks = "<p>Here is an <em>example</em> of some markdown text:</p>\n" +
                        "<ul>\n" +
                        "<li>Item 1</li>\n" +
                        "<li>Item 2</li>\n" +
                        "</ul>\n" +
                        "<p>Make sure to skip a line after unordered lists.</p>\n";
        FunctionElement f = new FunctionElement("EXAMPLE_FUNCTION",
                DatabaseMetaData.procedureReturnsResult,
                hTMLRemarks,
                dataSource);
        // HTML remarks are left untouched in both cases.
        assertEquals(hTMLRemarks, f.getSQLRemarks());
        assertEquals(hTMLRemarks, f.getToolTip());
    }

    @Test
    public void testH2SignatureGeneration() throws SQLException {
        assertEquals("ST_SHORTESTPATH(VARCHAR, VARCHAR, INTEGER, INTEGER)\n"
                        + "ST_SHORTESTPATH(VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER)",
                new FunctionElement("ST_SHORTESTPATH",
                DatabaseMetaData.procedureReturnsResult,
                dataSource).getSQLCommand());
    }
}

