package org.orbisgis.core.ui.plugins.views.sqlConsole.blockComment;

import org.orbisgis.core.ui.plugins.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.core.ui.plugins.views.sqlConsole.util.QuoteUtilities;

public class QuoteSQL {

	public static void quoteSQL(SQLConsolePanel entryPanel, boolean sbAppend) {
		int[] bounds = entryPanel.getBoundsOfCurrentSQLStatement();

		if (bounds[0] == bounds[1]) {
			return;
		}

		String textToQuote = entryPanel.getCurrentSQLStatement();

		if (null == textToQuote) {
			return;
		}

		String quotedText = QuoteUtilities.quoteText(textToQuote, sbAppend);

		entryPanel.getScriptPanel().setSelectionStart(bounds[0]);
		entryPanel.getScriptPanel().setSelectionEnd(bounds[1]);
		entryPanel.getScriptPanel().replaceSelection(quotedText);
	}

	public static void unquoteSQL(SQLConsolePanel entryPanel) {
		int[] bounds = entryPanel.getBoundsOfCurrentSQLStatement();

		if (bounds[0] == bounds[1]) {
			return;
		}

		String textToUnquote = entryPanel.getCurrentSQLStatement();

		if (null == textToUnquote) {
			return;
		}

		String unquotedText = QuoteUtilities.unquoteText(textToUnquote);

		entryPanel.getScriptPanel().setSelectionStart(bounds[0]);
		entryPanel.getScriptPanel().setSelectionEnd(bounds[1]);
		entryPanel.getScriptPanel().replaceSelection(unquotedText);
	}
}
