/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.sqlconsole.blockComment;

import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

/**
 * Class for commenting SQL code in the SQL console.
 *
 * @author Adam Gouge
 */
public class CommentSQL {

    public static final String COMMENT_CHARACTER = "--";
    private static final int COMMENT_LENGTH = COMMENT_CHARACTER.length();
    private final static I18n I18N = I18nFactory.getI18n(CommentSQL.class);
    private final static Logger LOGGER = Logger.getLogger(CommentSQL.class);

    /**
     * Comment the selected text in the given script panel.
     *
     * @param scriptPanel Script panel
     */
    public static void commentOrUncommentSQL(RSyntaxTextArea scriptPanel) {
        // If the selection contains an unbroken range of commented lines,
        // then we uncomment.
        if (unbrokenRangeOfComments(scriptPanel)) {
            uncommentSQL(scriptPanel);
        } // Otherwise, we comment everything.
        else {
            commentSQL(scriptPanel);
        }
    }

    /**
     * Test whether the selected text consists of an unbroken range of
     * commented lines.
     *
     * @param scriptPanel Script panel
     * @return True iff the selected text consists of an unbroken range of
     * commented lines
     */
    private static boolean unbrokenRangeOfComments(RSyntaxTextArea scriptPanel) {

        final Element root = scriptPanel.getDocument().getDefaultRootElement();

        final int numberOfLastLine = root.getElementIndex(scriptPanel.getSelectionEnd());
        int currentLineNumber = root.getElementIndex(scriptPanel.getSelectionStart());
        while (currentLineNumber <= numberOfLastLine) {
            try {
                int startOffset = root.getElement(currentLineNumber).getStartOffset();
                if (!scriptPanel.getText(startOffset, COMMENT_LENGTH).equals(COMMENT_CHARACTER)) {
                    return false;
                }
            } catch (BadLocationException e) {
                LOGGER.warn(I18N.tr("Problem when checking for an unbroken range of comments"), e);
            }
            currentLineNumber++;
        }
        return true;
    }

    /**
     * Comment the selected text in the given script panel.
     *
     * @param scriptPanel Script panel
     */
    private static void commentSQL(RSyntaxTextArea scriptPanel) {

        final Element root = scriptPanel.getDocument().getDefaultRootElement();

        final int numberOfLastLine = root.getElementIndex(scriptPanel.getSelectionEnd());
        int currentLineNumber = root.getElementIndex(scriptPanel.getSelectionStart());
        while (currentLineNumber <= numberOfLastLine) {
            scriptPanel.insert(COMMENT_CHARACTER,
                    root.getElement(currentLineNumber).getStartOffset());
            currentLineNumber++;
        }
    }

    /**
     * Uncomment the selected text in the given script panel.
     *
     * @param scriptPanel Script panel
     */
    private static void uncommentSQL(RSyntaxTextArea scriptPanel) {

        final Element root = scriptPanel.getDocument().getDefaultRootElement();

        final int numberOfLastLine = root.getElementIndex(scriptPanel.getSelectionEnd());
        int currentLineNumber = root.getElementIndex(scriptPanel.getSelectionStart());
        while (currentLineNumber <= numberOfLastLine) {
            try {
                int startOffset = root.getElement(currentLineNumber).getStartOffset();
                if (scriptPanel.getText(startOffset, COMMENT_LENGTH).equals(COMMENT_CHARACTER)) {
                    scriptPanel.replaceRange("", startOffset, startOffset + COMMENT_LENGTH);
                }
            } catch (BadLocationException e) {
                LOGGER.warn(I18N.tr("Invalid length or offset when trying to uncomment code."), e);
            }
            currentLineNumber++;
        }
    }
}
