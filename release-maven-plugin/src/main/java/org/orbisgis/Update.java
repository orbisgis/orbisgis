package org.orbisgis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.orbisgis.updates.CreateUpdate;

/**
 * @goal update
 * @aggregator
 * @author Fernando Gonzalez Cortes
 */
public class Update extends AbstractReleaseMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File latestBinZip = File.createTempFile("latestBinary", ".zip");
			File tempFolder = latestBinZip.getParentFile();
			URL url = new URL(latestBinary);
			FileUtils.download(url, latestBinZip);

			File outFolder = new File(tempFolder, "latestBinary"
					+ System.nanoTime());
			Utils.unzip(new File("target", getFileName(false)), outFolder);
			File currentBinary = outFolder.listFiles()[0];

			outFolder = new File(tempFolder, "latestBinary" + System.nanoTime());
			Utils.unzip(latestBinZip, outFolder);
			File latestBinaryHome = outFolder.listFiles()[0];

			CreateUpdate cu = new CreateUpdate(appName, latestBinaryHome,
					currentBinary, new File("target", "updates"), new URL(
							updateURL), versionNumber, versionName);
			cu.setUrlPrefix(urlPrefix);
			cu.create();
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot create update", e);
		} catch (NoSuchAlgorithmException e) {
			throw new MojoExecutionException("Cannot create update", e);
		} catch (JAXBException e) {
			throw new MojoExecutionException("Cannot create update", e);
		}
	}

}
