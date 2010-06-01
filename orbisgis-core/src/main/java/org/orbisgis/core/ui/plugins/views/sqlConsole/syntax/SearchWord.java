package org.orbisgis.core.ui.plugins.views.sqlConsole.syntax;

import java.awt.Color;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter.HighlightPainter;

public class SearchWord {

	private JTextComponent textComp;

	// An instance of the private subclass of the default highlight painter
	Highlighter.HighlightPainter myHighlightPainter = (HighlightPainter) new WordHighlightPainter(
			new Color(205, 235, 255));

	public SearchWord(JTextComponent textComp) {
		this.textComp = textComp;
	}

	// Creates highlights around all occurrences of pattern in textComp
	public void highlight(String pattern) {
		// First remove all old highlights
		removeHighlights();
		try {
			Highlighter hilite = textComp.getHighlighter();
			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;

			// Search for pattern
			while ((pos = text.indexOf(pattern, pos)) >= 0) {
				// Create highlighter using private painter and apply around
				// pattern
				hilite.addHighlight(pos, pos + pattern.length(),
						myHighlightPainter);
				pos += pattern.length();
			}
		} catch (BadLocationException e) {
		}
	}

	// Removes only our private highlights
	public void removeHighlights() {
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();

		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof WordHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}

}
