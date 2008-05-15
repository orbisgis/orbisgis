/**
 *
 */
package org.orbisgis.views.sqlConsole.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.gdms.sql.parser.ParseException;
import org.gdms.sql.parser.SQLEngine;
import org.gdms.sql.parser.SQLEngineConstants;
import org.gdms.sql.parser.SimpleNode;
import org.gdms.sql.parser.Token;
import org.gdms.sql.parser.TokenMgrError;
import org.gdms.sql.strategies.SQLProcessor;

public class SHDocument extends DefaultStyledDocument {

	private SimpleAttributeSet red = new SimpleAttributeSet();
	private SimpleAttributeSet comment = new SimpleAttributeSet();
	private JTextPane textPane;
	private Timer timer;

	private static HashMap<Integer, SimpleAttributeSet> kindStyle = new HashMap<Integer, SimpleAttributeSet>();

	static {
		Color brown = new Color(128, 64, 0);
		SimpleAttributeSet idStyle = getStyle(Color.gray);
		SimpleAttributeSet keywordStyle = getStyle(Color.blue);
		SimpleAttributeSet literalStyle = getStyle(brown);
		SimpleAttributeSet commentStyle = getStyle(Color.green);
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
		kindStyle.put(SQLEngineConstants.EXISTS, keywordStyle);
		kindStyle.put(SQLEngineConstants.FLOATING_POINT_LITERAL, literalStyle);
		kindStyle.put(SQLEngineConstants.FROM, keywordStyle);
		kindStyle.put(SQLEngineConstants.GROUP, keywordStyle);
		kindStyle.put(SQLEngineConstants.HAVING, keywordStyle);
		kindStyle.put(SQLEngineConstants.IN, keywordStyle);
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
		kindStyle.put(SQLEngineConstants.ORDER, keywordStyle);
		kindStyle.put(SQLEngineConstants.PRIMARY, keywordStyle);
		kindStyle.put(SQLEngineConstants.QUOTED_ID, idStyle);
		kindStyle.put(SQLEngineConstants.SELECT, keywordStyle);
		kindStyle.put(SQLEngineConstants.SET, keywordStyle);
		kindStyle.put(SQLEngineConstants.STRING_LITERAL, literalStyle);
		kindStyle.put(SQLEngineConstants.TABLE, keywordStyle);
		kindStyle.put(SQLEngineConstants.UNION, keywordStyle);
		kindStyle.put(SQLEngineConstants.UPDATE, keywordStyle);
		kindStyle.put(SQLEngineConstants.VALUES, keywordStyle);
		kindStyle.put(SQLEngineConstants.WHERE, keywordStyle);
	}

	private static SimpleAttributeSet getStyle(Color color) {
		SimpleAttributeSet id = new SimpleAttributeSet();
		StyleConstants.setForeground(id, color);
		return id;
	}

	public SHDocument(JTextPane textPane) {
		StyleConstants.setForeground(red, Color.red);
		StyleConstants.setForeground(comment, Color.green);
		this.textPane = textPane;
		timer = new Timer(500, new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					colorize(0, 0);
					timer.stop();
				} catch (Exception e1) {
				}
			}

		});
		timer.setCoalesce(true);
		timer.start();
	}

	@Override
	public void remove(int offset, int len) throws BadLocationException {
		super.remove(offset, len);
		timer.restart();
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		super.insertString(offset, str, a);
		timer.restart();
	}

	private void colorize(int offset, int len) throws BadLocationException {
		String sql = getText(0, getLength());
		ByteArrayInputStream is = new ByteArrayInputStream(sql.getBytes());
		SQLEngine parser = new SQLEngine(is);
		Token lastToken = new Token();
		lastToken.beginColumn = Integer.MAX_VALUE;
		int lastParsedPos = 0;
		while ((parser.token.beginColumn != lastToken.beginColumn)
				|| (parser.token.beginLine != lastToken.beginLine)) {
			lastToken = parser.token;
			try {
				parser.resetTree();
				parser.SQLStatement();
				SimpleNode root = (SimpleNode) parser.getRootNode();

				int pos = textPane.getCaretPosition();
				// int start = SQLProcessor.getPosition(sql, root.first_token);
				// int end = SQLProcessor.getPosition(sql,
				// root.last_token.endLine, root.last_token.endColumn);
				// int dist = Math.abs(start - pos);
				// dist = Math.min(dist, Math.abs(end - pos));
				// if (dist < 100) {
				lastParsedPos = colorizeComment(sql, root.first_token,
						root.last_token, lastParsedPos);

				colorize(sql, root.first_token, root.last_token);
				textPane.setCaretPosition(pos);
				// } else {
				// lastParsedPos = SQLProcessor.getPosition(sql,
				// root.last_token.endLine, root.last_token.endColumn) + 1;
				// }
			} catch (ParseException e) {
				if (parser.token != lastToken) {

					Token first = lastToken.next;
					Token last = parser.token.next;
					lastParsedPos = colorizeComment(sql, first, last,
							lastParsedPos);

					int pos = textPane.getCaretPosition();
					int offset1 = SQLProcessor.getPosition(sql, first);
					int offset2 = SQLProcessor.getPosition(sql, last) + 1;
					String text = sql.substring(offset1, offset2);
					super.remove(offset1, offset2 - offset1);
					super.insertString(offset1, text, red);
					textPane.setCaretPosition(pos);

				} else {
					parser.getNextToken();
				}
			} catch (TokenMgrError e) {
				// ignore all script
				return;
			}
		}
		int pos = textPane.getCaretPosition();
		int endCommentPos = sql.length();
		if (endCommentPos > lastParsedPos) {
			String text = sql.substring(lastParsedPos, endCommentPos);
			super.remove(lastParsedPos, endCommentPos - lastParsedPos);
			super.insertString(lastParsedPos, text, comment);
		}
		textPane.setCaretPosition(pos);
	}

	private int colorizeComment(String sql, Token firstParsedToken,
			Token lastParsedToken, int lastParsedPos)
			throws BadLocationException {
		int pos = textPane.getCaretPosition();
		int endCommentPos = SQLProcessor.getPosition(sql, firstParsedToken);
		if (endCommentPos > lastParsedPos) {
			String text = sql.substring(lastParsedPos, endCommentPos);
			super.remove(lastParsedPos, endCommentPos - lastParsedPos);
			super.insertString(lastParsedPos, text, comment);
		}
		lastParsedPos = SQLProcessor.getPosition(sql, lastParsedToken.endLine,
				lastParsedToken.endColumn) + 1;
		textPane.setCaretPosition(pos);
		return lastParsedPos;
	}

	private void colorize(String sql, Token firstToken, Token lastToken)
			throws BadLocationException {
		Token color = firstToken;
		while (color != lastToken.next) {
			int offset1 = SQLProcessor.getPosition(sql, color);
			int offset2;
			if (color.next == null) {
				offset2 = sql.length();
			} else {
				offset2 = SQLProcessor.getPosition(sql, color.next) + 1;
			}
			String text = sql.substring(offset1, offset2);
			super.remove(offset1, text.length());
			super.insertString(offset1, text, getStyle(color.kind));

			color = color.next;
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
}