/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
