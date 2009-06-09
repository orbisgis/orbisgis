package org.orbisgis.core.ui.views.sqlConsole.ui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.orbisgis.core.ui.components.text.UndoableDocument;


public abstract class AbstractSyntaxColoringDocument extends UndoableDocument {
	protected JTextPane textPane;
	protected boolean styling = false;

	protected static SimpleAttributeSet getStyle(Color color) {
		SimpleAttributeSet id = new SimpleAttributeSet();
		StyleConstants.setForeground(id, color);
		StyleConstants.setFontFamily(id, "Monospaced");
		return id;
	}

	public AbstractSyntaxColoringDocument(JTextPane textPane) {
		super();
		this.textPane = textPane;
		this.setDocumentFilter(new ColorizerDocumentFilter());
		try {
			colorize(0, textPane.getText().length());
		} catch (BadLocationException e) {
		}
	}

	private void colorize(int init, int end) throws BadLocationException {
		if (end > 0) {
			int caretPosition = textPane.getCaretPosition();
			int selStart = textPane.getSelectionStart();
			int selEnd = textPane.getSelectionEnd();
			colorIn(init, end);
			textPane.setCaretPosition(caretPosition);
			textPane.setSelectionStart(selStart);
			textPane.setSelectionEnd(selEnd);
		}
	}

	protected abstract void colorIn(int init, int end)
			throws BadLocationException;

	private final class ColorizerDocumentFilter extends DocumentFilter {
		@Override
		public void replace(FilterBypass fb, int offset, int length,
				String text, AttributeSet attrs) throws BadLocationException {
			// replace tabs by spaces. Grammar needs that
			text = text.replaceAll("\t", "    ");

			// Indent as many spaces as after the last \n
			if (text.equals("\n")) {
				String currentText = textPane.getText().substring(0, offset);
				int lastIndex = currentText.lastIndexOf("\n");
				for (int i = lastIndex + 1; i < currentText.length()
						&& currentText.charAt(i) == ' '; i++) {
					text += ' ';
				}
			}

			// Style by default
			if (!styling) {
				attrs = getCommentStyle();
			}

			boolean alreadyGrouping = isGrouping;
			if (!alreadyGrouping) {
				groupUndoEdits(true);
			}

			// Save token bounds
			int[] originalTokenBounds = new int[] { 0, 0 };
			if (!styling) {
				originalTokenBounds = getTokenBounds(offset, text.length());
			}

			// Do replacement
			super.replace(fb, offset, length, text, attrs);

			// Get current token bounds
			// If not already applying style, apply it
			int[] currentTokenBounds;
			if (!styling) {
				currentTokenBounds = getTokenBounds(offset, text.length());
				colorize(Math
						.min(originalTokenBounds[0], currentTokenBounds[0]),
						Math.max(originalTokenBounds[1] + length + 1,
								currentTokenBounds[1]));
			}

			if (!alreadyGrouping) {
				groupUndoEdits(false);
			}
		}

		@Override
		public void remove(FilterBypass fb, int offset, int length)
				throws BadLocationException {
			boolean alreadyGrouping = isGrouping;
			if (!alreadyGrouping) {
				groupUndoEdits(true);
			}

			// Save token bounds
			int[] originalTokenBounds = new int[] { 0, 0 };
			if (!styling) {
				originalTokenBounds = getTokenBounds(offset, length);
			}

			// do removal
			super.remove(fb, offset, length);

			// Get current token bounds
			// If not already applying style, apply it
			if (!styling) {
				int[] currentTokenBounds = getTokenBounds(offset, length);
				colorize(Math
						.min(originalTokenBounds[0], currentTokenBounds[0]),
						Math.max(originalTokenBounds[1] - length - 1,
								currentTokenBounds[1]));
			}

			if (!alreadyGrouping) {
				groupUndoEdits(false);
			}
		}

		@Override
		public void insertString(FilterBypass fb, int offset, String text,
				AttributeSet attr) throws BadLocationException {
			replace(fb, offset, 0, text, attr);
		}
	}

	protected void styleComment(String sqlText, int startText,
			int lastStyledPos, int beginPos) throws BadLocationException {
		int spaceLength = beginPos - lastStyledPos;
		String comment = sqlText.substring(lastStyledPos, lastStyledPos
				+ spaceLength);
		if (comment.trim().length() > 0) {
			styling = true;
			super.remove(startText + lastStyledPos, spaceLength);
			super.insertString(startText + lastStyledPos, comment,
					getCommentStyle());
			styling = false;
		}
	}

	protected abstract int[] getTokenBounds(int offset, int length);

	protected abstract AttributeSet getCommentStyle();
}
