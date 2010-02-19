/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package org.orbisgis.plugins.core.language;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Singleton for the Internationalization (I18N)
 * 
 * <pre>
 * [1] HOWTO TRANSLATE JUMP IN MY OWN LANGUAGE
 *  Copy theses files and add the locales extension for your language and country instead of the *.
 *  - resources/jump_*.properties
 *  - com/vividsolutions/jump/workbench/ui/plugin/KeyboardPlugIn_*.html
 * [2] HOWTO TRANSLATE MY PLUGIN AND GIVE THE ABILITY TO TRANSLATE IT
 *  Use theses methods to use your own *.properties files :
 *  [Michael Michaud 2007-03-23] the 3 following methods have been deactivated
 *  com.vividsolutions.jump.I18N#setPlugInRessource(String, String)
 *  com.vividsolutions.jump.I18N#get(String, String)
 *  com.vividsolutions.jump.I18N#getMessage(String, String, Object[])
 *  you still can use plugInsResourceBundle (as in Pirol's plugin)
 *  
 *  And use jump standard menus
 * </pre>
 * 
 * Code example : [Michael Michaud 2007-03-23 : the following code example is no
 * more valid and has to be changed]
 * 
 * <pre>
 * public class PrintPlugIn extends AbstractPlugIn
 *  {
 *    private String name = &quot;print&quot;;
 * public void initialize(PlugInContext context) throws Exception
 * {
 *   I18N.setPlugInRessource(name, &quot;org.agil.core.jump.plugin.print&quot;);
 *   context.getFeatureInstaller().addMainMenuItem(this,
 *                                                 new String[]
 *                                                 {MenuNames.TOOLS,I18N.get(name, &quot;print&quot;)},
 *                                                 I18N.get(name, &quot;print&quot;), false, null, null);
 * }
 * ...
 * </pre>
 * 
 * <pre>
 * TODO :I18N (1) Improve translations
 * TODO :I18N (2) Separate config (customization) and I18N
 * TODO :I18N (3) Explore and discuss about I18N integration and Jakarta Common Ressources
 * (using it as a ressource interface)
 * </pre>
 * 
 * @author Basile Chandesris - <chandesris@pt-consulting.lu>
 * @see com.vividsolutions.jump.workbench.ui.MenuNames
 * @see com.vividsolutions.jump.workbench.ui.VTextIcon text rotation
 */
public final class I18N {

	private static final Logger LOG = Logger.getLogger(I18N.class);

	// [Michael Michaud 2007-03-23] removed SingletonHolder internal class
	// 1 - getInstance is enough to guarantee I18N instance unicity
	// 2 - I18N should not be instanciated as the class has only static methods
	private static final I18N instance = new I18N();

	// use 'jump<locale>.properties' i18n mapping file
	// STanner changed the place where are stored bundles. Now are in /language
	// public static ResourceBundle rb =
	// ResourceBundle.getBundle("com.vividsolutions.jump.jump");
	public static ResourceBundle rb = ResourceBundle
			.getBundle("org/orbisgis/plugins/core/language/OrbisGIS");

	// [Michael Michaud 2007-03-23] plugInsResourceBundle is deactivated because
	// all the methods
	// using it have been deactivated.
	// [sstein] activated again since Pirol does use it
	public static Hashtable plugInsResourceBundle = new Hashtable();

	/** The map from category names to I18N instances. */
	private static Map<String, I18N> instances = new HashMap<String, I18N>();
	private static Map<ResourceBundle, I18N> instancesByRb = new HashMap<ResourceBundle, I18N>();

	private static ClassLoader classLoader;

	/** The resource bundle for the I18N instance. */
	private ResourceBundle resourceBundle;

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	/** The core OpenJUMP I18N instance. */
	private I18N() {
		resourceBundle = rb;
	}

	/**
	 * Construct an I18N instance for the category.
	 * 
	 * @param resourcePath
	 *            The path to the language files.
	 */
	private I18N(final String resourcePath) {
		resourceBundle = ResourceBundle.getBundle(resourcePath, Locale
				.getDefault(), classLoader);
	}
	
	private I18N(final ResourceBundle rb) {
		resourceBundle = rb;
	}

	/**
	 * Set the class loader used to load resource bundles, must only be called
	 * by the plug-in loader.
	 * 
	 * @param classLoader
	 *            the classLoader to set
	 */
	public static void setClassLoader(ClassLoader classLoader) {
		I18N.classLoader = classLoader;
	}

	/**
	 * Get the I18N text from the language file associated with this instance.
	 * If no label is defined then a default string is created from the last
	 * part of the key.
	 * 
	 * @param key
	 *            The key of the text in the language file.
	 * @return The I18Nized text.
	 */
	public String getText(final String key) {
		try {
			return resourceBundle.getString(key);
		} catch (java.util.MissingResourceException e) {
			String[] labelpath = key.split("\\.");
			LOG
					.debug("No resource bundle or no translation found for the key : "
							+ key);
			return labelpath[labelpath.length - 1];
		}
	}

	/**
	 * Get the I18N text from the language file associated with the specified
	 * category. If no label is defined then a default string is created from
	 * the last part of the key.
	 * 
	 * @param category
	 *            The category.
	 * @param key
	 *            The key of the text in the language file.
	 * @return The I18Nized text.
	 */
	public static String getText(final String category, final String key) {
		I18N i18n = getInstance(category);
		return i18n.getText(key);
	}

	/**
	 * Get the I18N instance for the category. A resource file must exist in the
	 * resource path for language/jump for the category.
	 * 
	 * @param category
	 *            The category.
	 * @return The instance.
	 */
	public static I18N getInstance(final String category) {
		I18N instance = instances.get(category);
		if (instance == null) {
			String resourcePath = category.replace('.', '/') + "/language/jump";
			instance = new I18N(resourcePath);
			instances.put(category, instance);
		}
		return instance;
	}
	
	public static I18N getInstanceByLoader(ResourceBundle rb) {		
		I18N instance = instances.get(rb);
		if (instance == null) {			
			instance = new I18N(rb);
			instancesByRb.put(rb, instance);
		}
		return instance;
	}

	public static I18N getInstance() {
		// [Michael Michaud 2007-03-04] guarantee I18N instance unicity without
		// creating a SingletonHolder inner class instance
		return (instance == null) ? new I18N() : instance;
		// return SingletonHolder._singleton;
	}

	/**
	 * Load file specified in command line (-i18n lang_country) (lang_country
	 * :language 2 letters + "_" + country 2 letters) Tries first to extract
	 * lang and country, and if only lang is specified, loads the corresponding
	 * resource bundle.
	 * 
	 * @param langcountry
	 */
	public static void loadFile(final String langcountry) {
		// [Michael Michaud 2007-03-04] handle the case where lang is the only
		// variable instead of catching an ArrayIndexOutOfBoundsException
		String[] lc = langcountry.split("_");
		Locale locale = Locale.getDefault();
		if (lc.length > 1) {
			LOG.debug("lang:" + lc[0] + " " + "country:" + lc[1]);
			locale = new Locale(lc[0], lc[1]);
		} else if (lc.length > 0) {
			LOG.debug("lang:" + lc[0]);
			locale = new Locale(lc[0]);
		} else {
			LOG.debug(langcountry
					+ " is an illegal argument to define lang [and country]");
		}
		rb = ResourceBundle.getBundle(
				"org/orbisgis/plugins/core/language/OrbisGIS", locale);
	}

	/**
	 * Process text with the locale 'jump_<locale>.properties' file
	 * 
	 * @param label
	 * @return i18n label [Michael Michaud 2007-03-23] If no resourcebundle is
	 *         found, returns a default string which is the last part of the
	 *         label
	 */
	public static String get(final String label) {
		try {
			return rb.getString(label);
		} catch (java.util.MissingResourceException e) {
			String[] labelpath = label.split("\\.");
			LOG
					.debug("No resource bundle or no translation found for the key : "
							+ label);
			return labelpath[labelpath.length - 1];
		}
	}

	/**
	 * Get the short signature for locale (letters extension :language 2 letters
	 * + "_" + country 2 letters)
	 * 
	 * @return string signature for locale
	 */
	public static String getLocale() {
		return rb.getLocale().getLanguage() + "_" + rb.getLocale().getCountry();
	}

	/**
	 * Get the short signature for language (letters extension :language 2
	 * letters)
	 * 
	 * @return string signature for language
	 */
	public static String getLanguage() {
		/*
		 * if (JUMPWorkbench.I18N_SETLOCALE == "") { // No locale has been
		 * specified at startup: choose default locale return
		 * rb.getLocale().getLanguage(); } else { return
		 * JUMPWorkbench.I18N_SETLOCALE.split("_")[0]; }
		 */
		return null;
	}

	/**
	 * Process text with the locale 'jump_<locale>.properties' file If no
	 * resourcebundle is found, returns default string contained inside
	 * com.vividsolutions.jump.jump
	 * 
	 * @param label
	 *            with argument insertion : {0}
	 * @param objects
	 * @return i18n label
	 */
	public static String getMessage(final String label, final Object[] objects) {
		try {
			final MessageFormat mformat = new MessageFormat(rb.getString(label));
			return mformat.format(objects);
		} catch (java.util.MissingResourceException e) {
			final String[] labelpath = label.split("\\.");
			LOG.warn(e.getMessage()
					+ " no default value, the resource key is used: "
					+ labelpath[labelpath.length - 1]);
			final MessageFormat mformat = new MessageFormat(
					labelpath[labelpath.length - 1]);
			return mformat.format(objects);
		}
	}

	/**
	 * Get the I18N text from the language file associated with the specified
	 * category. If no label is defined then a default string is created from
	 * the last part of the key.
	 * 
	 * @param category
	 *            The category.
	 * @param label
	 *            with argument insertion : {0}
	 * @param objects
	 * @return i18n label
	 */
	public static String getMessage(final String category, final String label,
			final Object[] objects) {
		I18N i18n = getInstance(category);
		return i18n.getMessage(label, objects);
	}
}
