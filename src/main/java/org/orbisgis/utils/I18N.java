package org.orbisgis.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class I18N {	
	
	private final static String PROPERTIES = "properties";
	private final static String BACKSLASH = System.getProperty("file.separator");
	
	//I18N file must be in "properties/" repository
	private final static String I18N_PATH = PROPERTIES + BACKSLASH;
	//Orbisgis loacle. This local would applied to all plugin (if -i18n local has been given by command line)
	private static Locale I18N_SETLOCALE;
	//Orbisgis key, for find back Resource bundle about orbisgis Context.
	private static String ORBISGIS_CTX = "orbisgis";
	//ResourceBundle dictionnary (contains all resource bundle : plugIns, orbisgis, gdms ...)
	private static HashMap<String, ResourceBundle> I18NS = new HashMap<String, ResourceBundle>();	
	
	
	//translate 
	public static String getText(final String key) {
		//Get context (ResourceBundle) with the key:
		//myplugin.key = my translation -> get context : myplugin
		int contextIndex = key.indexOf(".");
		if (contextIndex==-1) return key; //context not found. No translation
		String contextKey = key.substring(0,contextIndex);
		if(contextKey!=null) {							
			if(I18NS.get(contextKey) == null )
				//context not found. No translation : return key
				return key; 
			try {
				//return translation
				return I18NS.get(contextKey).getString(key);
			}catch(MissingResourceException m) {
				//key not found in context. No translation : return key
				return key;
			}		
		}
		else
			//context not found. No translation : return key
			return key;
	}
	
	//Add I18N context : orbisgis context, gdms context... and context for each PlugIn.
	public static void addI18n(String lang, String fileName, Class<?> loader) {
		//for PlugIn : keep name of plugIn's class
		if(fileName==null)
			fileName = loader.getSimpleName().toLowerCase();
		//Get base name for construct ResourceBundle
		String baseName = getBundleBaseName(fileName, loader);
		//Get Locale
		Locale locale  = getLocale(lang);
		//Get bundle
		ResourceBundle bundle = null;
		try{
			bundle = ResourceBundle.getBundle(baseName,locale, loader.getClassLoader());
			if(I18NS.get(fileName)!=null) {
				//This PlugIn (or i18n context for this PlugIn) already exists
			}
			else
				//Put bundle into dictionnary with context key (file name)
				I18NS.put(fileName, bundle);			
		}catch(MissingResourceException missingResException){
			//Do nothing. The ResourceBundle is not loaded --> key will return at translation.
		}
		//Record OrbisGIS locale. Only if locale has added on command line.
		if(fileName.equals(ORBISGIS_CTX) && !lang.equals(""))
			//Set OrbisGIS Locale as default locale (For subProject and plugins) 
			I18N_SETLOCALE = locale;
	}
	
	public static void delI18n(String fileName, Class<?> loader) {
		//Get base name for construct ResourceBundle
		if(fileName==null)
			fileName = loader.getSimpleName().toLowerCase();
		if(fileName!=null)	
			I18NS.remove(fileName);			
	}
	
	private static String getBundleBaseName(String fileName, Class<?> loader) {
		//find classloader package
		String i18nPackage = loader.getPackage().getName().replace(".", BACKSLASH) + BACKSLASH;	
		//Add properties repository. This is a constraint to create PlugIn
		//(PlugIn properties must be below properties repository)
		String i18nRepository = I18N_PATH + fileName ;
		String baseName = i18nPackage + i18nRepository;
		//return Bundle base name 
		return baseName;
	}	

	
	private static Locale getLocale(String lang) {
		Locale locale = null;
		if (I18N_SETLOCALE == null) {
			//No default language fix in orbisgis
			locale = Locale.getDefault();
			if(lang!=null && !lang.equals("")) {
				//Set specific local for this context
				String[] lc = lang.split("_");		
				if (lc.length > 1) {
					//LOG.debug("lang:" + lc[0] + " " + "country:" + lc[1]);
					locale = new Locale(lc[0], lc[1]);
				} else if (lc.length > 0) {
					//LOG.debug("lang:" + lc[0]);
					locale = new Locale(lc[0]);
				} else {
					//LOG.debug(langcountry
							//+ " is an illegal argument to define lang [and country]");
				}
				
			}
		}
		else
			//keep orbisgis language (because command line fix i18n)
			locale = I18N_SETLOCALE;	

		return locale;
	}	
}
