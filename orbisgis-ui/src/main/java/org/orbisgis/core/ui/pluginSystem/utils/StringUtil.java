/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.pluginSystem.utils;

public class StringUtil {

	public static String replaceAll(String original, String oldSubstring,
			String newSubstring) {
		return replace(original, oldSubstring, newSubstring, true);
	}

	/**
	 * Returns original with occurrences of oldSubstring replaced by
	 * newSubstring. Set all to true to replace all occurrences, or false to
	 * replace the first occurrence only.
	 */
	public static String replace(String original, String oldSubstring,
			String newSubstring, boolean all) {
		StringBuffer b = new StringBuffer(original);
		replace(b, oldSubstring, newSubstring, all);

		return b.toString();
	}

	/**
	 * Replaces all instances of the String o with the String n in the
	 * StringBuffer orig if all is true, or only the first instance if all is
	 * false. Posted by Steve Chapel <schapel@breakthr.com> on UseNet
	 */
	public static void replace(StringBuffer orig, String o, String n,
			boolean all) {
		if ((orig == null) || (o == null) || (o.length() == 0) || (n == null)) {
			throw new IllegalArgumentException("Null or zero-length String");
		}

		int i = 0;

		while ((i + o.length()) <= orig.length()) {
			if (orig.substring(i, i + o.length()).equals(o)) {
				orig.replace(i, i + o.length(), n);

				if (!all) {
					break;
				} else {
					i += n.length();
				}
			} else {
				i++;
			}
		}
	}

	public static String toFriendlyName(String className) {
		return toFriendlyName(className, null);
	}

	public static String friendlyName(Class c) {
		return toFriendlyName(c.getName());
	}

	public static String toFriendlyName(String className,
			String substringToRemove) {
		String name = className;
		// Remove substring sooner rather than later because, for example,		
		if (substringToRemove != null) {
			name = StringUtil.replaceAll(name, substringToRemove, "");
		}

		name = StringUtil.classNameWithoutQualifiers(name);
		name = insertSpaces(name);

		return name;
	}

	public static String classNameWithoutQualifiers(String className) {
		return className.substring(Math.max(className.lastIndexOf("."),
				className.lastIndexOf("$")) + 1);
	}

	public static String insertSpaces(String s) {
		if (s.length() < 2) {
			return s;
		}

		String result = "";

		for (int i = 0; i < (s.length() - 2); i++) { // -2
			result += s.charAt(i);

			if ((Character.isLowerCase(s.charAt(i)) && Character.isUpperCase(s
					.charAt(i + 1)))
					|| (Character.isUpperCase(s.charAt(i + 1)) && Character
							.isLowerCase(s.charAt(i + 2)))) {
				result += " ";
			}
		}

		result += s.charAt(s.length() - 2);
		result += s.charAt(s.length() - 1);

		return result.trim();
	}

}
