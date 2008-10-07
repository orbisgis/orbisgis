package org.orbisgis;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal source
 * @aggregator
 * 
 * @author Fernando Gonzalez Cortes
 */
public class SourcePack extends AbstractMojo {

	/**
	 * @parameter expression="Application name"
	 */
	private String appName;

	/**
	 * @parameter expression="Version number"
	 */
	private String versionNumber;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!new File(".", "plugin-manager").exists()) {
			throw new MojoExecutionException("This plugin is expected "
					+ "to be run in the project root");
		} else {
			// write ant file
			try {
				String sourceFileName = appName + "-" + versionNumber
						+ "-sources.zip";
				Utils.executeSource(sourceFileName);
			} catch (IOException e1) {
				throw new MojoExecutionException("Cannot get ant script", e1);
			}
		}
	}

}
