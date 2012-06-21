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
package org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.autocompletion;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.JavaParser;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.JavaParserConstants;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.ParseException;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager.parser.SimpleNode;

public class Completion {

	private static final int MAX_ITERATIONS = 10;
	private AutoCompletionVisitor acVisitor;

	public Completion() throws LinkageError {
		acVisitor = new AutoCompletionVisitor();
	}


	public Option[] getOptions(String text, int caretPosition, boolean script) {
		ArrayList<String> versions = new ArrayList<String>();
		versions.add(text);
		int count = 0;
		while ((count < MAX_ITERATIONS) && (versions.size() > 0)) {
			String currentText = versions.remove(0);
			int[] pos = NodeUtils.getPosition(currentText, caretPosition);
			NodeUtils nu = new NodeUtils(currentText, pos[0], pos[1]);
			ByteArrayInputStream bis = new ByteArrayInputStream(currentText
					.getBytes());
			try {
				JavaParser parser = new JavaParser(bis);
				parser.prepareParser(nu, caretPosition);
				if (script) {
					parser.Script();
				} else {
					parser.CompilationUnit();
				}
				SimpleNode node = (SimpleNode) parser.getRootNode();
				acVisitor.setCompletionCase(text, node, pos[0], pos[1]);
				acVisitor.visit(node, null);
				return acVisitor.getOptions();
			} catch (ParseException e) {
				ArrayList<String> validTexts = getValidText(currentText,
						caretPosition, e);
				for (String validText : validTexts) {
					versions.add(validText);
				}
			}
			count++;
		}
		return new Option[0];
	}

	private ArrayList<String> getValidText(String text, int caretPosition,
			ParseException e) {
		ArrayList<String> options = new ArrayList<String>();
		if ((caretPosition > 0)
				&& (text.charAt(caretPosition - 1) == '.')
				&& ((caretPosition >= text.length()) || (text
						.charAt(caretPosition) != 'a'))) {
			String newText = text.substring(0, caretPosition) + "a"
					+ text.substring(caretPosition);
			System.err.println(newText);
			options.add(newText);
		} else if ((e.currentToken != null) && (e.currentToken.next != null)
				&& (e.currentToken.next.kind == JavaParserConstants.LT)) {
			String newText = text.substring(0, caretPosition) + "> a;"
					+ text.substring(caretPosition);
			System.err.println(newText);
			options.add(newText);
		} else {
			// Try to add from the expected token sequences
			int line = e.currentToken.endLine;
			int column = e.currentToken.endColumn + 1;
			int pos = NodeUtils.getPosition(text, line, column);
			if (canAdd(e, JavaParserConstants.RPAREN)) {
				String newText = text.substring(0, pos) + ")"
						+ text.substring(pos);
				System.err.println("Inserting )\n" + newText);
				options.add(newText);
			}
			if (canAdd(e, JavaParserConstants.RBRACE)) {
				String newText = text.substring(0, pos) + "}"
						+ text.substring(pos);
				System.err.println("Inserting }\n" + newText);
				options.add(newText);
			}
			if (canAdd(e, JavaParserConstants.LPAREN)) {
				String newText = text.substring(0, pos) + "("
						+ text.substring(pos);
				System.err.println("Inserting (\n" + newText);
				options.add(newText);
			}
			if (canAdd(e, JavaParserConstants.SEMICOLON)
					|| (options.size() == 0)) {
				// Add a semicolon by default
				String newText = text.substring(0, pos) + ";"
						+ text.substring(pos);
				System.err.println("Inserting ;\n" + newText);
				options.add(newText);
			}
		}

		return options;
	}

	private boolean canAdd(ParseException e, int wantedToken) {
		int[][] seq = e.expectedTokenSequences;
		for (int[] token : seq) {
			if (token[0] == wantedToken) {
				return true;
			}
		}

		return false;
	}

}
