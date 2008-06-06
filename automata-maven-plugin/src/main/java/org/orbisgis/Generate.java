package org.orbisgis;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 *
 * @goal generate
 */
public class Generate extends AbstractMojo {

	/**
	 * @parameter expression="src/main/resources"
	 */
	private String automataFolder;

	/**
	 * @parameter expression="src/main/java"
	 */
	private String sourceFolder;

	/**
	 * @parameter expression="org"
	 */
	private String outputPackage;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			System.out.println("Generating tools...");
			AG.main(new String[] { automataFolder, sourceFolder, outputPackage });
		} catch (Exception e) {
			throw new MojoExecutionException("Cannot generate", e);
		}
	}
}
