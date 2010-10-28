package org.orbisgis.core.ui.plugins.views.sqlConsole.blockComment;

import org.orbisgis.core.ui.plugins.views.sqlConsole.ui.SQLScriptPanel;
import org.orbisgis.core.ui.plugins.views.sqlConsole.util.QuoteUtilities;

public class QuoteSQL {

	public static void quoteSQL(SQLScriptPanel entryPanel, boolean sbAppend) {
		int[] bounds = entryPanel.getBoundsOfSQLToBeExecuted();

		if (bounds[0] == bounds[1]) {
			return;
		}

		String textToQuote = entryPanel.getSQLToBeExecuted();

		if (null == textToQuote) {
			return;
		}

		String quotedText = QuoteUtilities.quoteText(textToQuote, sbAppend);

		entryPanel.setSelectionStart(bounds[0]);
		entryPanel.setSelectionEnd(bounds[1]);
		entryPanel.replaceSelection(quotedText);
	}

	public static void unquoteSQL(SQLScriptPanel entryPanel) {
		int[] bounds = entryPanel.getBoundsOfSQLToBeExecuted();

		if (bounds[0] == bounds[1]) {
			return;
		}

		String textToUnquote = entryPanel.getSQLToBeExecuted();

		if (null == textToUnquote) {
			return;
		}

		String unquotedText = QuoteUtilities.unquoteText(textToUnquote);

		entryPanel.setSelectionStart(bounds[0]);
		entryPanel.setSelectionEnd(bounds[1]);
		entryPanel.replaceSelection(unquotedText);
	}
}
