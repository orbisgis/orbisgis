package org.orbisgis.view.sqlconsole.ui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.orbisgis.view.sqlconsole.ui.FunctionListTransferHandler.SIGNATURES;
import static org.orbisgis.view.util.CommentUtil.SQL_COMMENT_CHARACTER;

/**
 * Created by adam on 4/29/14.
 */
public class FunctionListTransferHandlerTest {

    @Test
    public void test() {
        String tooltip = "Compute a buffer around a Geometry.\n"
                + "The optional third parameter can either specify number of segments used\n"
                + " to approximate a quarter circle (integer case, defaults to 8)\n"
                + " or a list of blank-separated key=value pairs (string case) to manage buffer style parameters :\n"
                + "'quad_segs=8' endcap=round|flat|square' 'join=round|mitre|bevel' 'mitre_limit=5'";
        String sqlCommand = "ST_BUFFER(GEOMETRY, DOUBLE)\nST_BUFFER(GEOMETRY, DOUBLE, OTHER)";
        FunctionListTransferHandler t = new FunctionListTransferHandler();
        StringBuilder s = new StringBuilder();
        t.formatFunctionComment(s, tooltip, sqlCommand);
        assertEquals(SQL_COMMENT_CHARACTER + "Compute a buffer around a Geometry.\n"
                + SQL_COMMENT_CHARACTER + "The optional third parameter can either specify number of segments used\n"
                + SQL_COMMENT_CHARACTER + " to approximate a quarter circle (integer case, defaults to 8)\n"
                + SQL_COMMENT_CHARACTER + " or a list of blank-separated key=value pairs (string case) to manage buffer style parameters :\n"
                + SQL_COMMENT_CHARACTER +  "'quad_segs=8' endcap=round|flat|square' 'join=round|mitre|bevel' 'mitre_limit=5'\n"
                + SQL_COMMENT_CHARACTER + SIGNATURES + "\n"
                + sqlCommand + "\n", s.toString());
    }
}
