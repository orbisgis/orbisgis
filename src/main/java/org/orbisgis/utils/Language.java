package org.orbisgis.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Language {
	
	private final static String BASE_PACKAGE = "org";
	private final static String PROPERTIES = "properties";
	private final static String BACKSLASH = System.getProperty("file.separator");
	
	private HashMap<String, ResourceBundle> i18ns;
	
	public Language() {
		i18ns = new HashMap<String, ResourceBundle>();
	}	
	
	public String getText(String i18nFile, final String key) {
		try {
			return i18ns.get(i18nFile).getString(key);
		} catch (java.util.MissingResourceException e) {
			//String[] labelpath = key.split("\\.");
			//LOG.debug("No resource bundle or no translation found for the key : "
			//				+ key);
			return key;//labelpath[labelpath.length - 1];
		}
	}
	

	public void addSubProject(String string, String fileName,Class<?> subProjectClass) {
		String i18nPath = subProjectClass.getResource(PROPERTIES+BACKSLASH+fileName+"."+PROPERTIES).getPath();
		String i18nFile = i18nPath.substring(
				i18nPath.indexOf(BASE_PACKAGE),
				i18nPath.indexOf("."+PROPERTIES));		
		
		Locale locale = Locale.getDefault();
		ResourceBundle bundle = null;
		try{
			bundle = ResourceBundle.getBundle(i18nFile,locale, subProjectClass.getClassLoader());
			i18ns.put(fileName, bundle);			
		}catch(MissingResourceException missingResException){
			
		}catch(NullPointerException nullException){
			
		}
		
	}
}
