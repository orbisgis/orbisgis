package org.orbisgis;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal source
 * @aggregator
 * 
 * @author Fernando Gonzalez Cortes
 */
public class SourcePack extends AbstractReleaseMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!new File(".", "plugin-manager").exists()) {
			throw new MojoExecutionException("This plugin is expected "
					+ "to be run in the project root");
		} else {
			// write ant file
			try {
				Utils.executeSource(getFileName(true));
			} catch (IOException e1) {
				throw new MojoExecutionException("Cannot get ant script", e1);
			}
		}
	}

}
