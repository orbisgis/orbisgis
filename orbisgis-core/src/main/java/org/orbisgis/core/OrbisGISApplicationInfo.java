package org.orbisgis.core;

import java.io.File;

import org.orbisgis.core.ApplicationInfo;

public class OrbisGISApplicationInfo implements ApplicationInfo {

	@Override
	public String getLogFile() {
		return new File(System.getProperty("user.home")
				+ "/OrbisGIS/orbisgis.log").getAbsolutePath();
	}

	@Override
	public File getHomeFolder() {
		return new File(System.getProperty("user.home") + "/OrbisGIS/");
	}

	@Override
	public String getName() {
		return "OrbisGIS";
	}

	@Override
	public String getOrganization() {
		return "IRSTV CNRS-FR-2488";
	}

	@Override
	public String getVersionName() {
		return "Lyon";
	}

	@Override
	public String getVersionNumber() {
		return "2.2";
	}

}
