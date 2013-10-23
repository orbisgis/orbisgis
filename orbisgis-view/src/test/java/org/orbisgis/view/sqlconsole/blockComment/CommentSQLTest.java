package org.orbisgis.view.sqlconsole.blockComment;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.Test;
import org.orbisgis.utils.TextUtils;
import static junit.framework.Assert.assertEquals;

/**
 * @author Adam Gouge
 */
public class CommentSQLTest {

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
    public void testComment() {
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        // Comment just the first two lines.
        scriptPanel.select(13, 226);
        CommentSQL.commentSQL(scriptPanel);
        assertEquals(CommentSQL.COMMENT_CHARACTER + LINE_ONE
                + CommentSQL.COMMENT_CHARACTER + LINE_TWO + LINE_THREE,
                scriptPanel.getText());
    }

    @Test
    public void testUncomment() {
        RSyntaxTextArea scriptPanel = new RSyntaxTextArea();
        scriptPanel.setText(TEXT);
        // Comment and uncomment the first two lines.
        scriptPanel.select(13, 226);
        CommentSQL.commentSQL(scriptPanel);
        CommentSQL.uncommentSQL(scriptPanel);
        assertEquals(TEXT, scriptPanel.getText());
    }
}
