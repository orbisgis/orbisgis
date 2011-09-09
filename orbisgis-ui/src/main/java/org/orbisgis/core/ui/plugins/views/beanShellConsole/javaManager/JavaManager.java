package org.orbisgis.core.ui.plugins.views.beanShellConsole.javaManager;

import java.io.File;
import java.util.List;

public interface JavaManager {


	/**
	 * Adds the specified files to the compile classpath of the manager
	 * 
	 * @param files
	 */
	void addFilesToClassPath(List<File> files);

	/**
	 * Gets an object to explore the packages in the classpath
	 * 
	 * @return
	 */
	PackageReflection getPackageReflection();

}
