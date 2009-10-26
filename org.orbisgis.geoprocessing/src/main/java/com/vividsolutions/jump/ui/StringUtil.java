package com.vividsolutions.jump.ui;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.vividsolutions.jts.util.Assert;

/**
 * Useful String-related utilities.
 */
public class StringUtil {

    /**
     * Warning: hinders internationalization
     */
    public static String s(int n) {
        return (n != 1) ? "s" : "";
    }
    /**
     * Warning: hinders internationalization
     */
    public static String ies(int n) {
        return (n != 1) ? "ies" : "y";
    }

    public static String substitute(String string, Object[] substitutions) {
        for (int i = 0; i < substitutions.length; i++) {
            string = StringUtil.replaceAll(string, "$" + (i + 1),
                    substitutions[i].toString());
        }
        return string;
    }

    public static String classNameWithoutQualifiers(String className) {
        return className.substring(
            Math.max(className.lastIndexOf("."), className.lastIndexOf("$")) + 1);
    }

    public static String classNameWithoutPackageQualifiers(String className) {
        return className.substring(className.lastIndexOf(".") + 1);
    }

    public static String repeat(char c, int n) {
        StringBuffer b = new StringBuffer();

        for (int i = 0; i < n; i++) {
            b.append(c);
        }

        return b.toString();
    }

    /**
     *  Line-wraps s by inserting a newline instead of the first space after the nth
     *  column. Word-wraps.
     */
    public static String split(String s, int n) {
        StringBuffer b = new StringBuffer();
        boolean wrapPending = false;

        for (int i = 0; i < s.length(); i++) {
            if (((i % n) == 0) && (i > 0)) {
                wrapPending = true;
            }

            char c = s.charAt(i);

            if (wrapPending && (c == ' ')) {
                b.append("\n");
                wrapPending = false;
            } else {
                b.append(c);
            }
        }

        return b.toString();
    }

    public static String capitalize(String word) {
        if (word.length() == 0) {
            return word;
        }

        return (word.charAt(0) + "").toUpperCase() + word.substring(1);
    }

    public static String uncapitalize(String word) {
        if (word.length() == 0) {
            return word;
        }

        return (word.charAt(0) + "").toLowerCase() + word.substring(1);
    }

    /**
     * Converts the comma-delimited string into a List of trimmed strings.
     * @param s a String with comma-delimited values
     * @return a List of the Strings that were delimited by commas
     */
    public static List fromCommaDelimitedString(String s) {
        if (s.trim().length() == 0) { return new ArrayList(); }
        ArrayList result = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(s, ",");

        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken().toString().trim());
        }

        return result;
    }

    /**
     * Returns a List of empty Strings.
     * @param size the size of the List to create
     * @return a List of blank Strings
     */
    public static List blankStringList(int size) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < size; i++) {
            list.add("");
        }

        return list;
    }

    public static String toFriendlyName(String className) {
        return toFriendlyName(className, null);
    }

    public static String friendlyName(Class c) {
        return toFriendlyName(c.getName());
    }

    public static String toFriendlyName(String className, String substringToRemove) {
        String name = className;

        //Remove substring sooner rather than later because, for example,
        //?"PlugIn" will become "Plug In". [Jon Aquino]
        if (substringToRemove != null) {
            name = StringUtil.replaceAll(name, substringToRemove, "");
        }

        name = StringUtil.classNameWithoutQualifiers(name);
        name = insertSpaces(name);

        return name;
    }

    public static String insertSpaces(String s) {
        if (s.length() < 2) {
            return s;
        }

        String result = "";

        for (int i = 0; i < (s.length() - 2); i++) { //-2
            result += s.charAt(i);

            if ((Character.isLowerCase(s.charAt(i))
                && Character.isUpperCase(s.charAt(i + 1)))
                || (Character.isUpperCase(s.charAt(i + 1))
                    && Character.isLowerCase(s.charAt(i + 2)))) {
                result += " ";
            }
        }

        result += s.charAt(s.length() - 2);
        result += s.charAt(s.length() - 1);

        return result.trim();
    }

    /**
     * Returns the elements of c separated by commas. If c is empty, an empty
     * String will be returned.
     * @param c a Collection of objects to convert to Strings and delimit by commas
     * @return a String containing c's elements, delimited by commas
     */
    public static String toCommaDelimitedString(Collection c) {
        return toDelimitedString(c, ", ");
    }

    /**
     *  Returns original with all occurrences of oldSubstring replaced by
     *  newSubstring
     */
    public static String replaceAll(
        String original,
        String oldSubstring,
        String newSubstring) {
        return replace(original, oldSubstring, newSubstring, true);
    }

    /**
     *  Returns original with occurrences of oldSubstring replaced by
     *  newSubstring. Set all to true to replace all occurrences, or false to
     *  replace the first occurrence only.
     */
    public static String replace(
        String original,
        String oldSubstring,
        String newSubstring,
        boolean all) {
        StringBuffer b = new StringBuffer(original);
        replace(b, oldSubstring, newSubstring, all);

        return b.toString();
    }

    /**
     *  Replaces all instances of the String o with the String n in the
     *  StringBuffer orig if all is true, or only the first instance if all is
     *  false. Posted by Steve Chapel <schapel@breakthr.com> on UseNet
     */
    public static void replace(StringBuffer orig, String o, String n, boolean all) {
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

    /**
     * Returns an throwable's stack trace
     */
    public static String stackTrace(Throwable t) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        t.printStackTrace(ps);

        return os.toString();
    }

    public static String head(String s, int lines) {
        int newlinesEncountered = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\n') {
                newlinesEncountered++;
                if (newlinesEncountered == lines) {
                    return s.substring(0, i);
                }
            }
        }
        return s;
    }

    public static String limitLength(String s, int maxLength) {
        Assert.isTrue(maxLength >= 3);

        if (s == null) {
            return null;
        }

        if (s.length() > maxLength) {
            return s.substring(0, maxLength - 3) + "...";
        }

        return s;
    }

    public static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String toDelimitedString(Collection c, String delimiter) {
        if (c.isEmpty()) {
            return "";
        }

        StringBuffer result = new StringBuffer();

        for (Iterator i = c.iterator(); i.hasNext();) {
            Object o = i.next();
            result.append(delimiter + ((o == null) ? "" : o.toString()));
        }

        return result.substring(delimiter.length());
    }

    


    public static boolean isEmpty(String value) {
        return (value == null || value.trim().length() == 0);
    }
}
