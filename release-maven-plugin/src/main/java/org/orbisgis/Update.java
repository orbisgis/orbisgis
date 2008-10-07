package org.orbisgis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal update
 * @aggregator
 * @author Fernando Gonzalez Cortes
 */
public class Update extends AbstractMojo {

	private static final int BUF_SIZE = 1024 * 8;

	/**
	 * @parameter expression="Application name"
	 */
	private String appName;

	/**
	 * @parameter expression="Version number"
	 */
	private String versionNumber;

	/**
	 * @parameter expression
	 */
	private String latestBinary;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File latestBinZip = File.createTempFile("latestBinary", ".zip");
			File tempFolder = latestBinZip.getParentFile();
			URL url = new URL(latestBinary);
			download(latestBinZip, url);

			File outFolder = new File(tempFolder, "latestBinary"
					+ System.nanoTime());
			Utils.unzip(new File("target", appName + "-" + versionNumber
					+ ".zip"), outFolder);
			File currentBinary = outFolder.listFiles()[0];

			outFolder = new File(tempFolder, "latestBinary" + System.nanoTime());
			Utils.unzip(latestBinZip, outFolder);
			File latestBinaryHome = outFolder.listFiles()[0];
			
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot create update", e);
		}
	}

	private void download(File file, URL url) throws FileNotFoundException,
			IOException {
		OutputStream out = null;
		InputStream in = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			URLConnection conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[BUF_SIZE];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
			}
		}
	}

}
