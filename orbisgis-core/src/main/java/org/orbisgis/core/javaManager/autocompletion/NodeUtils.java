package org.orbisgis.core.javaManager.autocompletion;

import org.orbisgis.core.javaManager.parser.Node;
import org.orbisgis.core.javaManager.parser.SimpleNode;

public class NodeUtils {

	private String text;

	private int line;

	private int col;

	public NodeUtils(String text, int line, int col) {
		this.text = text;
		this.line = line;
		this.col = col;
	}

	public int[] getPosition(int pos) {
		return getPosition(text, pos);
	}

	public static int[] getPosition(String text, int pos) {
		String[] lines = text.split("\n");
		int line = 0;
		while (lines[line].length() + 1 < pos) {
			pos -= lines[line].length() + 1;
			line++;
		}

		return new int[] { line + 1, pos + 1 };
	}

	public boolean isAtCursor(Node n, int line, int col) {
		int bl = getBeginLine(n);
		int el = getEndLine(n);
		if ((bl == el) && (line == bl)) {
			int bc = getBeginColumn(n);
			int ec = getEndColumn(n) + 1;
			if ((bc <= col) && (ec >= col)) {
				return true;
			}
		}

		return false;
	}

	public int getEndColumn(Node n) {
		SimpleNode sn = (SimpleNode) n;
		return sn.last_token.endColumn;
	}

	public int getBeginColumn(Node n) {
		SimpleNode sn = (SimpleNode) n;
		return sn.first_token.beginColumn;
	}

	public int getEndLine(Node n) {
		SimpleNode sn = (SimpleNode) n;
		return sn.last_token.endLine;
	}

	public int getBeginLine(Node n) {
		SimpleNode sn = (SimpleNode) n;
		return sn.first_token.beginLine;
	}

	public String getPrefix(Node node, int line, int col) {
		String str = getText(node);
		return str.substring(0, Math.min(col - getBeginColumn(node), str
				.length()));
	}

	public String getTextUntilCursor(Node node, int line, int col) {
		return getText(node, line, col);
	}

	public String getText(Node n) {
		return getText(n, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public String getText(Node n, int maxLine, int maxCol) {
		int bl = getBeginLine(n) - 1;
		int el = Math.min(getEndLine(n) - 1, maxLine);
		int bc = getBeginColumn(n) - 1;
		int ec = Math.min(getEndColumn(n), maxCol);
		String[] lines = text.split("\n");
		StringBuffer str = new StringBuffer();
		for (int i = bl; i <= el; i++) {
			int start = bc;
			if (bl < i) {
				start = 0;
			}
			int end;
			if (i < el) {
				end = lines[i].length();
			} else {
				end = Math.min(ec, lines[i].length());
			}

			str.append(lines[i].substring(start, end)).append("\n");
		}

		return str.toString().trim();
	}

	public int getPosition(int line, int column) {
		return getPosition(text, line, column);
	}

	public static int getPosition(String text, int line, int column) {
		line = line - 1;
		column = column - 1;
		int acum = 0;
		int lastPos = 0;
		int breakPos;
		int i = 0;
		while ((i < line) && ((breakPos = text.indexOf("\n", lastPos)) != -1)) {
			acum = acum + breakPos - lastPos + 1;
			lastPos = breakPos + 1;
			i++;
		}
		return acum + column;
	}

	/**
	 * Gets the node text until the specified position split by the '.'
	 * character.
	 * 
	 * @param node
	 * @return
	 */
	public String[] getParts(Node node, int line, int col) {
		String cutted = getPrefix(node, line, col);
		String[] parts = cutted.split("\\Q.\\E");
		String[] aux;
		boolean addEmpty = cutted.endsWith(".");
		if (addEmpty) {
			aux = new String[parts.length + 1];
		} else {
			aux = new String[parts.length];
		}
		for (int i = 0; i < parts.length; i++) {
			aux[i] = parts[i].trim();
		}
		if (addEmpty) {
			aux[aux.length - 1] = "";
		}
		parts = aux;
		return parts;
	}

	public String getPrefix(Node node) {
		return getPrefix(node, line, col);
	}

	/**
	 * Gets the node text until cursor split by the '.' character.
	 * 
	 * @param node
	 * @return
	 */
	public String[] getParts(Node node) {
		return getParts(node, line, col);
	}

	public boolean isAtCursor(Node node) {
		return isAtCursor(node, line, col);
	}

	public boolean isCursorInside(Node n) {
		int bl = CompletionUtils.getNodeUtils().getBeginLine(n);
		int el = CompletionUtils.getNodeUtils().getEndLine(n);
		if ((bl <= line) && (el >= line)) {
			int bc = CompletionUtils.getNodeUtils().getBeginColumn(n);
			int ec = CompletionUtils.getNodeUtils().getEndColumn(n);
			if ((bl == line) && (bc <= col)) {
				return true;
			}
			if ((el == line) && (ec >= col)) {
				return true;
			}
			if ((bl < line) && (el > line)) {
				return true;
			}
		}

		return false;
	}

}
