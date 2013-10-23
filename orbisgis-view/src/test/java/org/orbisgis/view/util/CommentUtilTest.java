package org.orbisgis.view.util;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Test;
import org.orbisgis.utils.TextUtils;
import org.orbisgis.view.util.CommentUtil;

import static junit.framework.Assert.assertEquals;

/**
 * Note: The tests for {@link CommentUtil#commentOrUncommentJava} look exactly
 * the same, so we don't include them.
 *
 * @author Adam Gouge
 */
public class CommentUtilTest {

    private static final String EOL = TextUtils.getEolStr();

    private static final String LINE_ONE = "CREATE TABLE distances AS SELECT " +
            "a.the_geom, ST_Distance(a.the_geom, b.the_geom) AS distance, " +
            "a.ID_GEOFLA, a.id FROM nearest_points a, chef_lieu_43 b WHERE " +
            "a.ID_GEOFLA=b.ID_GEOFLA;" + EOL;
    private static final String LINE_TWO = "CREATE TABLE min_distances AS SELECT " +
                "MIN(distance) AS min_distance, ID_GEOFLA, MIN(id) as id FROM " +
                "distances GROUP BY ID_GEOFLA;" + EOL;
    private static final String LINE_THREE = "CREATE TABLE mairies_proj_final " +
            "AS SELECT a.* FROM nearest_points a, min_distances b WHERE a.id=b.id;";
    private static final String TEXT = LINE_ONE + LINE_TWO + LINE_THREE;


    @Test
    public void testCommentLinesOneAndTwo() {
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        // Comment just the first two lines.
        scriptPanel.select(13, 226);
        CommentUtil.commentOrUncommentSQL(scriptPanel);
        assertEquals(CommentUtil.SQL_COMMENT_CHARACTER + LINE_ONE
                + CommentUtil.SQL_COMMENT_CHARACTER + LINE_TWO + LINE_THREE,
                scriptPanel.getText());
    }

    @Test
    public void testCommentUncommentLinesOneAndTwo() {
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        scriptPanel.select(13, 226);
        // Comment the first two lines.
        CommentUtil.commentOrUncommentSQL(scriptPanel);
        // Uncomment the first two lines.
        CommentUtil.commentOrUncommentSQL(scriptPanel);
        assertEquals(TEXT, scriptPanel.getText());
    }

    @Test
    public void testUncommentUnbrokenRangeWithCommentRemainingOnLineTwo() {
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(CommentUtil.SQL_COMMENT_CHARACTER + LINE_ONE
                + CommentUtil.SQL_COMMENT_CHARACTER + CommentUtil.SQL_COMMENT_CHARACTER + LINE_TWO
                + CommentUtil.SQL_COMMENT_CHARACTER + LINE_THREE);
        scriptPanel.selectAll();
        // Uncomment all three lines, leaving only line two commented.
        CommentUtil.commentOrUncommentSQL(scriptPanel);
        // Uncomment the first two lines.
        assertEquals(LINE_ONE
                + CommentUtil.SQL_COMMENT_CHARACTER + LINE_TWO
                + LINE_THREE,
                scriptPanel.getText());
    }

    @Test
    public void testCommentBrokenRangeWithNoCommentOnLineTwo() {
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(CommentUtil.SQL_COMMENT_CHARACTER + LINE_ONE
                + LINE_TWO
                + CommentUtil.SQL_COMMENT_CHARACTER + LINE_THREE);
        scriptPanel.selectAll();
        // Comment all three lines.
        CommentUtil.commentOrUncommentSQL(scriptPanel);
        // Uncomment the first two lines.
        assertEquals(CommentUtil.SQL_COMMENT_CHARACTER + CommentUtil.SQL_COMMENT_CHARACTER + LINE_ONE
                + CommentUtil.SQL_COMMENT_CHARACTER + LINE_TWO
                + CommentUtil.SQL_COMMENT_CHARACTER + CommentUtil.SQL_COMMENT_CHARACTER + LINE_THREE,
                scriptPanel.getText());
    }
}
