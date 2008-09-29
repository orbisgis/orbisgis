package org.orbisgis.pluginManager.updates;

public class UpdateUtils {
	static final String SITE_UPDATES_FILE_NAME = "site-updates.xml";
	static final String ANT_FILE_NAME = "update.xml";

	public static String getUpdateFileName(String versionNumber) {
		return "update-" + versionNumber + ".zip";
	}
}
