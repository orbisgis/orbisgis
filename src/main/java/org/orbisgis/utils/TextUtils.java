package org.orbisgis.utils;

public class TextUtils {

	/**
	 * Convert a string to HTML. From S. Bayer.
	 * 
	 * @param string
	 * @return
	 */
	public static String stringToHTMLString(String string) {
		StringBuffer sb = new StringBuffer(string.length());
		// true if last char was blank
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = string.charAt(i);
			if (c == ' ') {
				// blank gets extra work,
				// this solves the problem you get if you replace all
				// blanks with &nbsp;, if you do that you loss
				// word breaking
				if (lastWasBlankChar) {
					lastWasBlankChar = false;
					sb.append("&nbsp;");
				} else {
					lastWasBlankChar = true;
					sb.append(' ');
				}
			} else {
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
					sb.append("&quot;");
				else if (c == '&')
					sb.append("&amp;");
				else if (c == '<')
					sb.append("&lt;");
				else if (c == '>')
					sb.append("&gt;");
				else if (c == '\n')
					// Handle Newline
					sb.append("&lt;br/&gt;");
				else {
					int ci = 0xffff & c;
					if (ci < 160)
						// nothing special only 7 Bit
						sb.append(c);
					else {
						// Not 7 Bit use the unicode system
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Return the absolute start and end position of the specified positions in
	 * a text composed by the specified lines
	 * 
	 * @param bl
	 *            start position line
	 * @param bc
	 *            start position column
	 * @param el
	 *            end position line
	 * @param ec
	 *            end position column
	 * @param lines
	 *            lines representing the multiline text
	 * @return An array of two elements. The first one is the start position
	 *         (inclusive) and the second is the end position (exclusive)
	 */
	public static int[] getLocation(int bl, int bc, int el, int ec,
			String[] lines) {
		int start = getPosition(bl, bc, lines) + bl;
		int end = getPosition(el, ec, lines) + el;

		return new int[] { start, end + 1 };
	}

	private static int getPosition(int line, int column, String[] lines) {
		int start = 0;
		for (int i = 0; i < line; i++) {
			start += lines[i].length();
		}
		start += column;
		return start;
	}

}
