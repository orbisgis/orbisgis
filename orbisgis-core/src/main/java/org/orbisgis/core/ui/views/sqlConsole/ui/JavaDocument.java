/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/**
 *
 */
package org.orbisgis.core.ui.views.sqlConsole.ui;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.orbisgis.core.javaManager.autocompletion.NodeUtils;
import org.orbisgis.core.javaManager.parser.JavaCharStream;
import org.orbisgis.core.javaManager.parser.JavaParserConstants;
import org.orbisgis.core.javaManager.parser.JavaParserTokenManager;
import org.orbisgis.core.javaManager.parser.Token;
import org.orbisgis.core.javaManager.parser.TokenMgrError;

public class JavaDocument extends AbstractSyntaxColoringDocument {

	private static SimpleAttributeSet commentStyle = new SimpleAttributeSet();
	private static SimpleAttributeSet punctStyle;

	private static HashMap<Integer, SimpleAttributeSet> kindStyle = new HashMap<Integer, SimpleAttributeSet>();

	static {
		kindStyle.clear();
		Color brown = new Color(128, 64, 0);
		SimpleAttributeSet idStyle = getStyle(Color.blue);
		SimpleAttributeSet keywordStyle = getStyle(new Color(127, 0, 85));
		StyleConstants.setBold(keywordStyle, true);
		punctStyle = getStyle(Color.black);
		SimpleAttributeSet literalStyle = getStyle(brown);
		commentStyle = getStyle(new Color(63, 127, 95));
		kindStyle.put(JavaParserConstants.EOF, keywordStyle);
		kindStyle.put(JavaParserConstants.SINGLE_LINE_COMMENT, commentStyle);
		kindStyle.put(JavaParserConstants.FORMAL_COMMENT, commentStyle);
		kindStyle.put(JavaParserConstants.MULTI_LINE_COMMENT, commentStyle);
		kindStyle.put(JavaParserConstants.ABSTRACT, keywordStyle);
		kindStyle.put(JavaParserConstants.ASSERT, keywordStyle);
		kindStyle.put(JavaParserConstants.BOOLEAN, keywordStyle);
		kindStyle.put(JavaParserConstants.BREAK, keywordStyle);
		kindStyle.put(JavaParserConstants.BYTE, keywordStyle);
		kindStyle.put(JavaParserConstants.CASE, keywordStyle);
		kindStyle.put(JavaParserConstants.CATCH, keywordStyle);
		kindStyle.put(JavaParserConstants.CHAR, keywordStyle);
		kindStyle.put(JavaParserConstants.CLASS, keywordStyle);
		kindStyle.put(JavaParserConstants.CONST, keywordStyle);
		kindStyle.put(JavaParserConstants.CONTINUE, keywordStyle);
		kindStyle.put(JavaParserConstants._DEFAULT, keywordStyle);
		kindStyle.put(JavaParserConstants.DO, keywordStyle);
		kindStyle.put(JavaParserConstants.DOUBLE, keywordStyle);
		kindStyle.put(JavaParserConstants.ELSE, keywordStyle);
		kindStyle.put(JavaParserConstants.ENUM, keywordStyle);
		kindStyle.put(JavaParserConstants.EXTENDS, keywordStyle);
		kindStyle.put(JavaParserConstants.FALSE, keywordStyle);
		kindStyle.put(JavaParserConstants.FINAL, keywordStyle);
		kindStyle.put(JavaParserConstants.FINALLY, keywordStyle);
		kindStyle.put(JavaParserConstants.FLOAT, keywordStyle);
		kindStyle.put(JavaParserConstants.FOR, keywordStyle);
		kindStyle.put(JavaParserConstants.GOTO, keywordStyle);
		kindStyle.put(JavaParserConstants.IF, keywordStyle);
		kindStyle.put(JavaParserConstants.IMPLEMENTS, keywordStyle);
		kindStyle.put(JavaParserConstants.IMPORT, keywordStyle);
		kindStyle.put(JavaParserConstants.INSTANCEOF, keywordStyle);
		kindStyle.put(JavaParserConstants.INT, keywordStyle);
		kindStyle.put(JavaParserConstants.INTERFACE, keywordStyle);
		kindStyle.put(JavaParserConstants.LONG, keywordStyle);
		kindStyle.put(JavaParserConstants.NATIVE, keywordStyle);
		kindStyle.put(JavaParserConstants.NEW, keywordStyle);
		kindStyle.put(JavaParserConstants.NULL, keywordStyle);
		kindStyle.put(JavaParserConstants.PACKAGE, keywordStyle);
		kindStyle.put(JavaParserConstants.PRIVATE, keywordStyle);
		kindStyle.put(JavaParserConstants.PROTECTED, keywordStyle);
		kindStyle.put(JavaParserConstants.PUBLIC, keywordStyle);
		kindStyle.put(JavaParserConstants.RETURN, keywordStyle);
		kindStyle.put(JavaParserConstants.SHORT, keywordStyle);
		kindStyle.put(JavaParserConstants.STATIC, keywordStyle);
		kindStyle.put(JavaParserConstants.STRICTFP, keywordStyle);
		kindStyle.put(JavaParserConstants.SUPER, keywordStyle);
		kindStyle.put(JavaParserConstants.SWITCH, keywordStyle);
		kindStyle.put(JavaParserConstants.SYNCHRONIZED, keywordStyle);
		kindStyle.put(JavaParserConstants.THIS, keywordStyle);
		kindStyle.put(JavaParserConstants.THROW, keywordStyle);
		kindStyle.put(JavaParserConstants.THROWS, keywordStyle);
		kindStyle.put(JavaParserConstants.TRANSIENT, keywordStyle);
		kindStyle.put(JavaParserConstants.TRUE, keywordStyle);
		kindStyle.put(JavaParserConstants.TRY, keywordStyle);
		kindStyle.put(JavaParserConstants.VOID, keywordStyle);
		kindStyle.put(JavaParserConstants.VOLATILE, keywordStyle);
		kindStyle.put(JavaParserConstants.WHILE, keywordStyle);
		kindStyle.put(JavaParserConstants.INTEGER_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.DECIMAL_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.HEX_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.OCTAL_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.FLOATING_POINT_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.EXPONENT, literalStyle);
		kindStyle.put(JavaParserConstants.CHARACTER_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.STRING_LITERAL, literalStyle);
		kindStyle.put(JavaParserConstants.IDENTIFIER, idStyle);
		kindStyle.put(JavaParserConstants.LETTER, literalStyle);
		kindStyle.put(JavaParserConstants.DIGIT, literalStyle);
		kindStyle.put(JavaParserConstants.LPAREN, punctStyle);
		kindStyle.put(JavaParserConstants.RPAREN, punctStyle);
		kindStyle.put(JavaParserConstants.LBRACE, punctStyle);
		kindStyle.put(JavaParserConstants.RBRACE, punctStyle);
		kindStyle.put(JavaParserConstants.LBRACKET, punctStyle);
		kindStyle.put(JavaParserConstants.RBRACKET, punctStyle);
		kindStyle.put(JavaParserConstants.SEMICOLON, punctStyle);
		kindStyle.put(JavaParserConstants.COMMA, punctStyle);
		kindStyle.put(JavaParserConstants.DOT, punctStyle);
		kindStyle.put(JavaParserConstants.AT, punctStyle);
		kindStyle.put(JavaParserConstants.ASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.LT, punctStyle);
		kindStyle.put(JavaParserConstants.BANG, punctStyle);
		kindStyle.put(JavaParserConstants.TILDE, punctStyle);
		kindStyle.put(JavaParserConstants.HOOK, punctStyle);
		kindStyle.put(JavaParserConstants.COLON, punctStyle);
		kindStyle.put(JavaParserConstants.EQ, punctStyle);
		kindStyle.put(JavaParserConstants.LE, punctStyle);
		kindStyle.put(JavaParserConstants.GE, punctStyle);
		kindStyle.put(JavaParserConstants.NE, punctStyle);
		kindStyle.put(JavaParserConstants.SC_OR, punctStyle);
		kindStyle.put(JavaParserConstants.SC_AND, punctStyle);
		kindStyle.put(JavaParserConstants.INCR, punctStyle);
		kindStyle.put(JavaParserConstants.DECR, punctStyle);
		kindStyle.put(JavaParserConstants.PLUS, punctStyle);
		kindStyle.put(JavaParserConstants.MINUS, punctStyle);
		kindStyle.put(JavaParserConstants.STAR, punctStyle);
		kindStyle.put(JavaParserConstants.SLASH, punctStyle);
		kindStyle.put(JavaParserConstants.BIT_AND, punctStyle);
		kindStyle.put(JavaParserConstants.BIT_OR, punctStyle);
		kindStyle.put(JavaParserConstants.XOR, punctStyle);
		kindStyle.put(JavaParserConstants.REM, punctStyle);
		kindStyle.put(JavaParserConstants.LSHIFT, punctStyle);
		kindStyle.put(JavaParserConstants.PLUSASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.MINUSASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.STARASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.SLASHASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.ANDASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.ORASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.XORASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.REMASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.LSHIFTASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.RSIGNEDSHIFTASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.RUNSIGNEDSHIFTASSIGN, punctStyle);
		kindStyle.put(JavaParserConstants.ELLIPSIS, punctStyle);
		kindStyle.put(JavaParserConstants.RUNSIGNEDSHIFT, punctStyle);
		kindStyle.put(JavaParserConstants.RSIGNEDSHIFT, punctStyle);
		kindStyle.put(JavaParserConstants.GT, punctStyle);
		kindStyle.put(JavaParserConstants.DEFAULT, punctStyle);
		kindStyle.put(JavaParserConstants.IN_SINGLE_LINE_COMMENT, commentStyle);
		kindStyle.put(JavaParserConstants.IN_FORMAL_COMMENT, commentStyle);
		kindStyle.put(JavaParserConstants.IN_MULTI_LINE_COMMENT, commentStyle);
	}

	public JavaDocument(JTextPane textPane) {
		super(textPane);
	}

	@Override
	protected void colorIn(int start, int end) throws BadLocationException {
		String javaText = textPane.getText();
		int startText = start;
		int endText = end;
		if (endText > javaText.length()) {
			endText = javaText.length();
		}
		javaText = javaText.substring(startText, endText);
		if (javaText.trim().length() == 0) {
			return;
		}
		JavaParserTokenManager tm = new JavaParserTokenManager(
				new JavaCharStream(
						new ByteArrayInputStream(javaText.getBytes())));
		Token token;
		int lastStyledPos = 0;
		try {
			do {
				token = tm.getNextToken();
				int beginPos;
				if (token.kind == JavaParserConstants.EOF) {
					beginPos = javaText.length();
				} else {
					beginPos = NodeUtils.getPosition(javaText, token.beginLine,
							token.beginColumn);
				}

				// check comments
				if (beginPos >= lastStyledPos) {
					styleComment(javaText, startText, lastStyledPos, beginPos);
				}

				// draw token
				lastStyledPos = NodeUtils.getPosition(javaText, token.endLine,
						token.endColumn) + 1;
				styling = true;
				super.remove(startText + beginPos, token.image.length());
				super.insertString(startText + beginPos, token.image,
						getStyle(token.kind));
				styling = false;
			} while (token.kind != JavaParserTokenManager.EOF);
		} catch (TokenMgrError e1) {
			styleComment(javaText, startText, lastStyledPos, javaText.length());
		}

	}

	private AttributeSet getStyle(int kind) {
		SimpleAttributeSet style = kindStyle.get(kind);
		if (style == null) {
			return getStyle(Color.black);
		} else {
			return style;
		}
	}

	@Override
	protected AttributeSet getCommentStyle() {
		return commentStyle;
	}

	@Override
	protected int[] getTokenBounds(int offset, int length) {
		String javaText = textPane.getText();

		JavaParserTokenManager tm = new JavaParserTokenManager(
				new JavaCharStream(
						new ByteArrayInputStream(javaText.getBytes())));
		Token token = null;
		Token initialToken = null;
		Token endToken = null;
		try {
			do {
				token = tm.getNextToken();
				int beginPos = NodeUtils.getPosition(javaText, token.beginLine,
						token.beginColumn);
				int endPos = NodeUtils.getPosition(javaText, token.endLine,
						token.endColumn);

				if (beginPos < offset) {
					initialToken = token;
				}

				if (endPos > offset + length) {
					endToken = token;
				}

			} while ((token.kind != JavaParserTokenManager.EOF)
					&& ((initialToken == null) || (endToken == null)));
		} catch (TokenMgrError e1) {
		}

		int init;
		if (initialToken == null) {
			init = 0;
		} else {
			init = NodeUtils.getPosition(javaText, initialToken.beginLine,
					initialToken.beginColumn);
		}

		int end;
		if (endToken == null) {
			end = javaText.length();
		} else {
			end = NodeUtils.getPosition(javaText, endToken.endLine,
					endToken.endColumn) + 1;
		}
		return new int[] { Math.max(init, 0), Math.max(end, 0) };
	}

}