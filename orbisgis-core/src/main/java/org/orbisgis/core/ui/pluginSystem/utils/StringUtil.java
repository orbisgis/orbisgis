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
