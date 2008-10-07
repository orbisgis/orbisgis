package org.orbisgis;

import org.apache.maven.plugin.AbstractMojo;

public abstract class AbstractReleaseMojo extends AbstractMojo {

	/**
	 * @parameter expression=""
	 */
	protected String urlSuffix;

	/**
	 * @parameter expression
	 */
	protected String urlPrefix;

	/**
	 * @parameter expression
	 */
	protected String updateURL;

	/**
	 * @parameter expression="Version name"
	 */
	protected String versionName;

	/**
	 * @parameter expression
	 */
	protected String latestBinary;

	/**
	 * @parameter expression="../platform/target/OrbisGIS"
	 */
	protected String binaryDir;

	/**
	 * @parameter expression
	 */
	protected String lzPackHome;

	/**
	 * @parameter expression
	 */
	protected String pluginList;

	/**
	 * @parameter expression
	 */
	protected String mainClass;

	/**
	 * @parameter expression="Application name"
	 */
	protected String appName;

	/**
	 * @parameter expression="Version number"
	 */
	protected String versionNumber;

	protected String getFileName(boolean sources) {
		String ret = appName + "-" + versionNumber;
		if (sources) {
			ret += "-sources";
		}
		ret += ".zip";
		
		return ret;
	}
}
