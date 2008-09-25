package org.orbisgis.pluginManager.updates;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.orbisgis.pluginManager.updates.persistence.Update;
import org.orbisgis.pluginManager.updates.persistence.UpdateSite;
import org.orbisgis.utils.FileUtils;

public class CreateUpdate {

	static final String SITE_UPDATES_FILE_NAME = "site-updates.xml";
	private static final String ANT_FILE_NAME = "update.xml";
	private File pub;
	private File next;
	private File output;
	private URL updateSite;
	private String description;
	private String versionName;
	private String versionNumber;
	private ArrayList<File> added = new ArrayList<File>();
	private ArrayList<File> removed = new ArrayList<File>();
	private ArrayList<File> modified = new ArrayList<File>();

	public CreateUpdate(File pub, File next, File output, URL updateSite,
			String versionNumber, String versionName, String description) {
		this.pub = pub;
		this.next = next;
		this.output = output;
		this.updateSite = updateSite;
		this.description = description;
		this.versionName = versionName;
		this.versionNumber = versionNumber.trim();
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 7) {
			System.err.println("Usage: java CreateUpdate "
					+ "latest-public-binary-folder new"
					+ "-binary-folder update-output-dir update-site-url "
					+ "version-number version-name version-description");
		}

		File pub = new File(args[0]);
		File next = new File(args[1]);
		File output = new File(args[2]);
		URL updateSite = new URL(args[3]);

		CreateUpdate cu = new CreateUpdate(pub, next, output, updateSite,
				args[4], args[5], args[6]);
		cu.create();

	}

	public synchronized void create() throws Exception {
		if (!pub.exists() || !next.exists()) {
			throw new RuntimeException("Both folders must exist");
		}

		diff();

		File tempUpdate = new File(output, "tozip");
		generateUpdateContent(tempUpdate);

		modifySiteDescriptor(output, updateSite);

		zipUpdate(new File(output, getUpdateFileName()), tempUpdate);
	}

	String getUpdateFileName() {
		return "update" + versionNumber + ".zip";
	}

	private void zipUpdate(File destFile, File tempUpdate) throws IOException {
		FileUtils.zip(tempUpdate, destFile);
		FileUtils.deleteDir(tempUpdate);
	}

	/**
	 * Modifies the site description. It will add an update element with the
	 * specified version information or it will replace an existing one that
	 * matches the version number
	 * 
	 * @param outputFolder
	 * @param updateSiteURL
	 * 
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	public void modifySiteDescriptor(File outputFolder, URL updateSiteURL)
			throws IOException, JAXBException {
		updateSiteURL = new URL(updateSiteURL.toExternalForm() + "/"
				+ SITE_UPDATES_FILE_NAME);
		File updateSiteFile = new File(outputFolder, SITE_UPDATES_FILE_NAME);

		// create or modify update content
		JAXBContext context = JAXBContext.newInstance(UpdateSite.class
				.getPackage().getName());
		UpdateSite us = null;
		boolean versionExists = false;
		try {
			FileUtils.download(updateSiteURL, updateSiteFile);
			us = (UpdateSite) context.createUnmarshaller().unmarshal(
					updateSiteFile);
			for (int i = 0; i < us.getUpdate().size(); i++) {
				Update update = us.getUpdate().get(i);
				if (update.getVersionNumber().equals(versionNumber)) {
					update.setDescription(description);
					update.setVersionName(versionName);
					versionExists = true;
					break;
				}
			}
		} catch (IOException e) {
			// ignore, either the url doesn't exists or it will fail later
		}

		if (!versionExists) {
			if (us == null) {
				us = new UpdateSite();
			}
			Update update = new Update();
			update.setDescription(description);
			update.setVersionName(versionName);
			update.setVersionNumber(versionNumber);
			us.getUpdate().add(update);
		}

		// write the new content
		context.createMarshaller().marshal(us, updateSiteFile);
	}

	/**
	 * Creates the update content to the specified output folder
	 * 
	 * @param outputDir
	 * @throws IOException
	 */
	public void generateUpdateContent(File outputDir) throws IOException {
		if (outputDir.exists()) {
			FileUtils.deleteDir(output);
		}
		if (!outputDir.mkdirs()) {
			throw new RuntimeException("Cannot create output dir");
		}

		generateAnt(outputDir);

		copyResources(outputDir);
	}

	private void copyResources(File output) throws IOException {
		ArrayList<File> filesToAdd = new ArrayList<File>();
		filesToAdd.addAll(added);
		filesToAdd.addAll(modified);
		for (int i = 0; i < filesToAdd.size(); i++) {
			File toAdd = filesToAdd.get(i);
			if (toAdd.isDirectory()) {
				String relativePath = FileUtils.getRelativePath(next, toAdd);
				File outputDir = new File(output, relativePath);
				if (!outputDir.exists() && !outputDir.mkdirs()) {
					throw new IOException("Cannot create: " + outputDir);
				}
				FileUtils.copyDirsRecursively(toAdd, outputDir);
			} else {
				String relativePath = FileUtils.getRelativePath(next, toAdd
						.getParentFile());
				File outputDir = new File(output, relativePath);
				FileUtils.copyFileToDirectory(toAdd, outputDir);
			}
		}
	}

	private void generateAnt(File output) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(output, ANT_FILE_NAME));
		pw.println("<?xml version=\"1.0\"?>");
		pw.println("<project name=\"org.orbisgis\" "
				+ "default=\"update\" basedir=\".\">");

		pw.println("<property name=\"update-dir\" value=\"[UPDATE_DIR]\" />");
		pw
				.println("<property name=\"orbisgis-home\" value=\"[ORBISGIS_HOME]\" />");

		pw.println("<target name=\"update\">");
		pw.println(" <copy todir=\"${orbisgis-home}\" overwrite=\"true\">");
		pw.println("  <fileset dir=\"${update-dir}\">");
		pw.println("   <include name=\"**/*\"/>");
		pw.println("   <exclude name=\"update.xml\"/>");
		pw.println("  </fileset>");
		pw.println(" </copy>");
		for (File toRemove : removed) {
			String param = "file";
			if (toRemove.isDirectory()) {
				param = "dir";
			}
			pw.println(" <delete " + param + "=\"${orbisgis-home}/"
					+ FileUtils.getRelativePath(pub, toRemove) + "\"/>");
		}
		pw.println("</target>");

		pw.println("</project>");

		pw.close();
	}

	private void checkFolders(File pub, File next) throws Exception {
		if (!pub.exists()) {
			if (!next.exists()) {
				throw new RuntimeException("bug!");
			} else {
				added.add(next);
			}
		} else if (!next.exists()) {
			removed.add(pub);
		} else {
			// both exist

			/*
			 * Check content in last published modified or not in next release
			 */
			File[] pubChildren = pub.listFiles();
			for (File pubFile : pubChildren) {
				if (pubFile.getAbsolutePath().contains(".svn")) {
					// for tests
					continue;
				}
				File nextFile = getFileIn(next, FileUtils.getRelativePath(pub,
						pubFile));
				if (nextFile != null) {
					if (nextFile.isDirectory()) {
						if (pubFile.isDirectory()) {
							checkFolders(pubFile, nextFile);
						} else {
							modified.add(nextFile);
						}
					} else {
						// Check modification nextFile pubFile
						byte[] pubContent = getContent(pubFile);
						byte[] nextContent = getContent(nextFile);
						if (pubContent.length != nextContent.length) {
							modified.add(nextFile);
						} else {
							for (int i = 0; i < nextContent.length; i++) {
								if (pubContent[i] != nextContent[i]) {
									modified.add(nextFile);
									break;
								}
							}
						}
					}
				} else {
					removed.add(pubFile);
				}
			}

			/*
			 * Check content in last published modified or not in next release
			 */
			File[] nextChildren = next.listFiles();
			for (File nextFile : nextChildren) {
				if (nextFile.getAbsolutePath().contains(".svn")) {
					// for tests:
					continue;
				}
				File pubFile = getFileIn(pub, FileUtils.getRelativePath(next,
						nextFile));
				if (pubFile != null) {
					// modification already checked. Do nothing
				} else {
					added.add(nextFile);
				}
			}
		}
	}

	private byte[] getContent(File file) throws FileNotFoundException,
			IOException {
		DataInputStream dis = new DataInputStream(new FileInputStream(file));
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		return buffer;
	}

	private File getFileIn(File baseDir, String toSearch) {
		File file = new File(baseDir, toSearch);
		if (file.exists()) {
			return file;
		} else {
			return null;
		}
	}

	public void diff() throws Exception {
		checkFolders(pub, next);
	}

	public File getPub() {
		return pub;
	}

	public File getNext() {
		return next;
	}

	public ArrayList<File> getAdded() {
		return added;
	}

	public ArrayList<File> getRemoved() {
		return removed;
	}

	public ArrayList<File> getModified() {
		return modified;
	}

	/**
	 * Applies the update in the specified folder to the specified binary folder
	 * 
	 * @param updateZipFile
	 * @param binaryDir
	 * @throws IOException
	 */
	public void applyUpdate(File updateZipFile, File binaryDir)
			throws IOException {
		// unzip update
		File tempUpdateDir = new File(updateZipFile.getParentFile(),
				"orbisgis-update" + System.currentTimeMillis());
		FileUtils.unzip(updateZipFile, tempUpdateDir);

		// substitute variables in ant script
		File updateAntFile = new File(tempUpdateDir, ANT_FILE_NAME);
		FileInputStream fis = new FileInputStream(updateAntFile);
		DataInputStream dis = new DataInputStream(fis);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		String content = new String(buffer);
		content = content.replaceAll("\\Q[UPDATE_DIR]\\E", tempUpdateDir
				.getAbsolutePath());
		content = content.replaceAll("\\Q[ORBISGIS_HOME]\\E", binaryDir
				.getAbsolutePath());
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(
				updateAntFile));
		dos.write(content.getBytes());
		dos.close();

		// Execute ant
		File buildFile = new File(tempUpdateDir, ANT_FILE_NAME);
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		p.addBuildListener(consoleLogger);

		try {
			p.fireBuildStarted();
			p.init();
			ProjectHelper helper = ProjectHelper.getProjectHelper();
			p.addReference("ant.projectHelper", helper);
			helper.parse(p, buildFile);
			p.executeTarget(p.getDefaultTarget());
			p.fireBuildFinished(null);
		} catch (BuildException e) {
			p.fireBuildFinished(e);
		}

	}
}