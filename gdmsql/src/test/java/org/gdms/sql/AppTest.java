package org.gdms.sql;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.gdms.SQLBaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.engine.parsing.GdmSQLLexer;
import org.gdms.sql.engine.parsing.GdmSQLParser;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
        public void testEngine() throws IOException {
                String sql = "SELECT * toto";

                ANTLRInputStream input;
                input = new ANTLRInputStream(new ByteArrayInputStream(sql.getBytes()));

                GdmSQLLexer lexer = new GdmSQLLexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                GdmSQLParser parser = new GdmSQLParser(tokens);
                try {
                        parser.start_rule();
                } catch (RecognitionException ex) {
                        System.out.println("c: " + ex.c);
                        System.out.println("charPosInLine: " + ex.charPositionInLine);
                        System.out.println("index: " + ex.index);
                        System.out.println("line: " + ex.line);
                        System.out.println("node: " + ex.node);
                        System.out.println("token.text: " + ex.token.getText());
                        System.out.println("token.type" + ex.token.getType());
                        System.out.println("unexpectedType" + ex.getUnexpectedType());
                        System.out.println("message: " + ex.getMessage());
                }
        }
//        public void testQuery() throws Exception {
//                SQLDataSourceFactory dsf = new SQLDataSourceFactory(SQLBaseTest.backupDir.getAbsolutePath(), SQLBaseTest.backupDir.getAbsolutePath());
//                String sql = "SELECT * FROM landcover2000 ;";
//                dsf.getSourceManager().register("landcover2000",
//                        new File(SQLBaseTest.internalData + "landcover2000.shp"));
//
//                DataSource ds = dsf.getDataSourceFromSQL(sql);
//                ds.open();
//                for (int i = 0; i < ds.getRowCount(); i++) {
//                        System.out.println(Arrays.deepToString(ds.getRow(i)));
//                }
//                ds.close();
//        }
}
