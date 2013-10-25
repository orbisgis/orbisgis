package org.orbisgis.view.util;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Test;
import org.orbisgis.utils.TextUtils;

import javax.swing.text.BadLocationException;
import java.awt.*;

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
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
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
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
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
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
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
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
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

    @Test
    public void testBlockComment() throws BadLocationException {
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        scriptPanel.select(13, 226);
        final String beforeSelection = scriptPanel.getText(0, scriptPanel.getSelectionStart());
        final String selection = scriptPanel.getText(
                scriptPanel.getSelectionStart(), scriptPanel.getSelectedText().length());
        final String afterSelection = scriptPanel.getText(
                scriptPanel.getSelectionEnd(),
                scriptPanel.getDocument().getEndPosition().getOffset() -  scriptPanel.getSelectionEnd() - 1);
        final String expectedText = beforeSelection
                + CommentUtil.BLOCK_COMMENT_START + selection + CommentUtil.BLOCK_COMMENT_END
                + afterSelection;
        // Block comment the selection
        CommentUtil.blockCommentOrUncomment(scriptPanel);
        assertEquals(expectedText, scriptPanel.getText());
        // Check that the commented part is selected.
        assertEquals(13, scriptPanel.getSelectionStart());
        assertEquals(226 + CommentUtil.BLOCK_COMMENT_START.length()
                + CommentUtil.BLOCK_COMMENT_END.length(),
                scriptPanel.getSelectionEnd());
    }

    @Test
    public void testBlockCommentEmptySelection() {
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        // Try to comment when there is no selection.
        scriptPanel.select(13, 13);
        final String originalText = scriptPanel.getText();
        CommentUtil.blockCommentOrUncomment(scriptPanel);
        assertEquals(originalText, scriptPanel.getText());
    }

    @Test
    public void testBlockCommentUncomment() throws BadLocationException {
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        scriptPanel.select(13, 226);
        final String originalText = scriptPanel.getText();
        // Block comment the selection
        CommentUtil.blockCommentOrUncomment(scriptPanel);
        assertEquals(13, scriptPanel.getSelectionStart());
        assertEquals(226 + CommentUtil.BLOCK_COMMENT_START.length()
                + CommentUtil.BLOCK_COMMENT_END.length(),
                scriptPanel.getSelectionEnd());
        // Block uncomment the selection
        CommentUtil.blockCommentOrUncomment(scriptPanel);
        assertEquals(13, scriptPanel.getSelectionStart());
        assertEquals(226, scriptPanel.getSelectionEnd());
        assertEquals(originalText, scriptPanel.getText());
    }

    @Test
    public void testBlockUncomment() {
        org.junit.Assume.assumeTrue(!GraphicsEnvironment.isHeadless());
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText("This is a /*test for block*/ uncommenting.");
        // Comment just the first two lines.
        scriptPanel.select(10, 28);
        CommentUtil.blockCommentOrUncomment(scriptPanel);
        assertEquals(10, scriptPanel.getSelectionStart());
        assertEquals(28 - CommentUtil.BLOCK_COMMENT_START.length()
                - CommentUtil.BLOCK_COMMENT_END.length(),
                scriptPanel.getSelectionEnd());
        assertEquals("This is a test for block uncommenting.",
                scriptPanel.getText());
    }
}
