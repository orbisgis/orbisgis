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
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.h2.util.OsgiDataSourceFactory;
import org.junit.Test;

import java.awt.GraphicsEnvironment;
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
