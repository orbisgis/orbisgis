package org.orbisgis.core.ui.views.sqlConsole.ui;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SQLEngineTokenManager;
import org.gdms.sql.parser.SimpleCharStream;
import org.gdms.sql.parser.Token;
import org.gdms.sql.parser.TokenMgrError;
import org.orbisgis.core.javaManager.autocompletion.NodeUtils;

public class SQLDocument extends AbstractSyntaxColoringDocument {

	private static SimpleAttributeSet commentStyle = new SimpleAttributeSet();
	private static SimpleAttributeSet keywordStyle;
	private static HashMap<Integer, SimpleAttributeSet> kindStyle = new HashMap<Integer, SimpleAttributeSet>();

	static {
		Color brown = new Color(128, 64, 0);
		SimpleAttributeSet idStyle = getStyle(Color.gray);
		keywordStyle = getStyle(Color.blue);
		SimpleAttributeSet literalStyle = getStyle(brown);
		commentStyle = getStyle(new Color(63, 127, 95));
		kindStyle.put(SQLEngineConstants.ID, idStyle);
		kindStyle.put(SQLEngineConstants.ALL, keywordStyle);
		kindStyle.put(SQLEngineConstants.AND, keywordStyle);
		kindStyle.put(SQLEngineConstants.AS, keywordStyle);
		kindStyle.put(SQLEngineConstants.ASC, keywordStyle);
		kindStyle.put(SQLEngineConstants.BEGIN, keywordStyle);
		kindStyle.put(SQLEngineConstants.BETWEEN, keywordStyle);
		kindStyle.put(SQLEngineConstants.BOOLEAN_LITERAL, literalStyle);
		kindStyle.put(SQLEngineConstants.BY, keywordStyle);
		kindStyle.put(SQLEngineConstants.COMMENT_BLOCK, commentStyle);
		kindStyle.put(SQLEngineConstants.COMMENT_LINE, commentStyle);
		kindStyle.put(SQLEngineConstants.CREATE, keywordStyle);
		kindStyle.put(SQLEngineConstants.DEFAULT, keywordStyle);
		kindStyle.put(SQLEngineConstants.DELETE, keywordStyle);
		kindStyle.put(SQLEngineConstants.DESC, keywordStyle);
		kindStyle.put(SQLEngineConstants.DISTINCT, keywordStyle);
		kindStyle.put(SQLEngineConstants.DROP, keywordStyle);
		kindStyle.put(SQLEngineConstants.ALTER, keywordStyle);
		kindStyle.put(SQLEngineConstants.ADD, keywordStyle);
		kindStyle.put(SQLEngineConstants.RENAME, keywordStyle);
		kindStyle.put(SQLEngineConstants.COLUMN, keywordStyle);
		kindStyle.put(SQLEngineConstants.TO, keywordStyle);
		kindStyle.put(SQLEngineConstants.RESTRICT, keywordStyle);
		kindStyle.put(SQLEngineConstants.CASCADE, keywordStyle);
		kindStyle.put(SQLEngineConstants.EXISTS, keywordStyle);
		kindStyle.put(SQLEngineConstants.FLOATING_POINT_LITERAL, literalStyle);
		kindStyle.put(SQLEngineConstants.FROM, keywordStyle);
		kindStyle.put(SQLEngineConstants.GROUP, keywordStyle);
		kindStyle.put(SQLEngineConstants.HAVING, keywordStyle);
		kindStyle.put(SQLEngineConstants.IN, keywordStyle);
		kindStyle.put(SQLEngineConstants.INDEX, keywordStyle);
		kindStyle.put(SQLEngineConstants.INSERT, keywordStyle);
		kindStyle.put(SQLEngineConstants.INTEGER_LITERAL, literalStyle);
		kindStyle.put(SQLEngineConstants.INTO, keywordStyle);
		kindStyle.put(SQLEngineConstants.IS, keywordStyle);
		kindStyle.put(SQLEngineConstants.KEY, keywordStyle);
		kindStyle.put(SQLEngineConstants.LIKE, keywordStyle);
		kindStyle.put(SQLEngineConstants.LIMIT, keywordStyle);
		kindStyle.put(SQLEngineConstants.NOT, keywordStyle);
		kindStyle.put(SQLEngineConstants.NULL, keywordStyle);
		kindStyle.put(SQLEngineConstants.OFFSET, keywordStyle);
		kindStyle.put(SQLEngineConstants.OR, keywordStyle);
		kindStyle.put(SQLEngineConstants.ON, keywordStyle);
		kindStyle.put(SQLEngineConstants.ORDER, keywordStyle);
		kindStyle.put(SQLEngineConstants.PRIMARY, keywordStyle);
		kindStyle.put(SQLEngineConstants.SELECT, keywordStyle);
		kindStyle.put(SQLEngineConstants.SET, keywordStyle);
		kindStyle.put(SQLEngineConstants.STRING_LITERAL, literalStyle);
		kindStyle.put(SQLEngineConstants.TABLE, keywordStyle);
		kindStyle.put(SQLEngineConstants.UNION, keywordStyle);
		kindStyle.put(SQLEngineConstants.UPDATE, keywordStyle);
		kindStyle.put(SQLEngineConstants.VALUES, keywordStyle);
		kindStyle.put(SQLEngineConstants.WHERE, keywordStyle);
		kindStyle.put(SQLEngineConstants.EXCEPT, keywordStyle);
		kindStyle.put(SQLEngineConstants.NUMERIC, literalStyle);
		kindStyle.put(SQLEngineConstants.TEXT, literalStyle);
		kindStyle.put(SQLEngineConstants.INTERGER, literalStyle);
	}

	public SQLDocument(JTextPane textPane) {
		super(textPane);
	}

	protected void colorIn(int start, int end) throws BadLocationException {
		String sqlText = textPane.getText();

		int startText = start;
		int endText = end;
		if (endText > sqlText.length()) {
			endText = sqlText.length();
		}
		sqlText = sqlText.substring(startText, endText);
		if (sqlText.trim().length() == 0) {
			return;
		}
		SQLEngineTokenManager tm = new SQLEngineTokenManager(
				new SimpleCharStream(new ByteArrayInputStream(sqlText
						.getBytes())));
		Token token;
		int lastStyledPos = 0;
		try {
			do {
				token = tm.getNextToken();
				int beginPos;
				if (token.kind == SQLEngineConstants.EOF) {
					beginPos = sqlText.length();
				} else {
					beginPos = NodeUtils.getPosition(sqlText, token.beginLine,
							token.beginColumn);
				}

				// check comments
				if (beginPos >= lastStyledPos) {
					styleComment(sqlText, startText, lastStyledPos, beginPos);
				}

				// draw token
				lastStyledPos = NodeUtils.getPosition(sqlText, token.endLine,
						token.endColumn) + 1;
				styling = true;
				super.remove(startText + beginPos, token.image.length());
				super.insertString(startText + beginPos, token.image,
						getStyle(token.kind));
				styling = false;
			} while (token.kind != SQLEngineTokenManager.EOF);
		} catch (TokenMgrError e1) {
			styleComment(sqlText, startText, lastStyledPos, sqlText.length());
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
		String sqlText = textPane.getText();

		SQLEngineTokenManager tm = new SQLEngineTokenManager(
				new SimpleCharStream(new ByteArrayInputStream(sqlText
						.getBytes())));
		Token token = null;
		Token initialToken = null;
		Token endToken = null;
		try {
			do {
				token = tm.getNextToken();
				int beginPos = NodeUtils.getPosition(sqlText, token.beginLine,
						token.beginColumn);
				int endPos = NodeUtils.getPosition(sqlText, token.endLine,
						token.endColumn);

				if (beginPos < offset) {
					initialToken = token;
				}

				if (endPos > offset + length) {
					endToken = token;
				}

			} while ((token.kind != SQLEngineTokenManager.EOF)
					&& ((initialToken == null) || (endToken == null)));
		} catch (TokenMgrError e1) {
		}

		int init;
		if (initialToken == null) {
			init = 0;
		} else {
			init = NodeUtils.getPosition(sqlText, initialToken.beginLine,
					initialToken.beginColumn);
		}

		int end;
		if (endToken == null) {
			end = sqlText.length();
		} else {
			end = NodeUtils.getPosition(sqlText, endToken.endLine,
					endToken.endColumn) + 1;
		}
		return new int[] { Math.max(init, 0), Math.max(end, 0) };
	}
}
