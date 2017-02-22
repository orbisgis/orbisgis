/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.sqlconsole.util;

/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.util.StringTokenizer;

public class QuoteUtilities {
	public static String quoteText(String textToQuote, boolean sbAppend) {
		if (null == textToQuote) {
			throw new IllegalArgumentException("textToQuote can not be null");
		}

		String[] lines = textToQuote.split("\n");

		StringBuilder ret = new StringBuilder();

		if (sbAppend) {
			ret.append("sb.append(\"").append(
					trimRight(lines[0].replaceAll("\"", "\\\\\"")));
		} else {
			ret.append("\"").append(
					trimRight(lines[0].replaceAll("\"", "\\\\\"")));
		}

		for (int i = 1; i < lines.length; ++i) {
			if (sbAppend) {
				ret.append(" \"); \nsb.append(\"").append(
						trimRight(lines[i].replaceAll("\"", "\\\\\"")));
			} else {
				ret.append(" \" +\n\"").append(
						trimRight(lines[i].replaceAll("\"", "\\\\\"")));
			}
		}

		if (sbAppend) {
			ret.append(" \");");
		} else {
			ret.append(" \";");
		}

		return ret.toString();
	}

	/**
	 * textToUnquote is seen as a tokens separated by quotes. All tokens that
	 * contain a new line character are left out.
	 * 
	 * @param textToUnquote
	 *            Text to be unquoted.
	 * 
	 * @return The unquoted text.
	 */
	public static String unquoteText(String textToUnquote) {
		//First of all, check if the text to unquote begins with '"' and ends with '";'
		if(!(textToUnquote.charAt(0) == '"' && textToUnquote.substring(textToUnquote.length()-2).equals("\";"))){
			return textToUnquote;
		}
		// new line to the begining so that sb.append( will be removed
		// new line to the end so that a semi colon at the end will be removed.
		textToUnquote = "\n" + textToUnquote + "\n";

		StringTokenizer st = new StringTokenizer(textToUnquote, "\"");

		StringBuilder ret = new StringBuilder();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			String trimmedToken = token;
			if (0 != token.trim().length() && -1 == token.indexOf('\n')) {
				if (trimmedToken.endsWith("\\n")) {
					// Some people put new line characters in their SQL to have
					// nice debug output.
					// Remove these new line characters too.
					trimmedToken = trimmedToken.substring(0, trimmedToken
							.length() - 2);
				}

				if (trimmedToken.endsWith("\\")) {
					ret.append(
							trimmedToken
									.substring(0, trimmedToken.length() - 1))
							.append("\"");
				} else {
					ret.append(trimmedToken).append("\n");
				}
			}
		}
		if (ret.toString().endsWith("\n")) {
			ret.setLength(ret.length() - 1);
		}
		return ret.toString();
	}

	static String trimRight(String toTrim) {
		if (0 >= toTrim.length()) {
			return toTrim;
		}

		int i;
		for (i = toTrim.length(); i > 0; --i) {
			if (!Character.isWhitespace(toTrim.charAt(i - 1))) {
				break;
			}
		}

		return toTrim.substring(0, i);
	}

}
