package org.orbisgis.pluginManager.updates;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.codehaus.plexus.util.FileUtils;

public class CreateUpdate {

	private File pub;
	private File next;
	private File output;
	private ArrayList<File> added = new ArrayList<File>();
	private ArrayList<File> removed = new ArrayList<File>();
	private ArrayList<File> modified = new ArrayList<File>();

	public CreateUpdate(File pub, File next, File output) {
		this.pub = pub;
		this.next = next;
		this.output = output;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: java CreateUpdate "
					+ "latest-public-binary-folder new"
					+ "-binary-folder update-output-dir");
		}

		File pub = new File(args[0]);
		File next = new File(args[1]);
		File output = new File(args[2]);

		CreateUpdate cu = new CreateUpdate(pub, next, output);
		cu.create();

	}

	public synchronized void create() throws Exception {
		if (!pub.exists() || !next.exists()) {
			throw new RuntimeException("Both folders must exist");
		}

		diff();

		generateRelease();
	}

	private void generateRelease() throws IOException {
		if (!output.exists()) {
			if (!output.mkdirs()) {
				throw new RuntimeException("Cannot create output dir");
			}
		}

		generateAnt();

		copyResources();
	}

	private void copyResources() throws IOException {
		ArrayList<File> filesToAdd = new ArrayList<File>();
		filesToAdd.addAll(added);
		filesToAdd.addAll(modified);
		for (int i = 0; i < filesToAdd.size(); i++) {
			File toAdd = filesToAdd.get(i);
			if (toAdd.isDirectory()) {
				String relativePath = getRelativePath(next, toAdd);
				File outputDir = new File(output, relativePath);
				if (!outputDir.mkdirs()) {
					throw new IOException("Cannot create: " + outputDir);
				}
				copyRecursively(toAdd, outputDir);
			} else {
				String relativePath = getRelativePath(next, toAdd
						.getParentFile());
				File outputDir = new File(output, relativePath);
				FileUtils.copyFileToDirectory(toAdd, outputDir);
			}
		}
	}

	private void copyRecursively(File source, File dest) throws IOException {
		File[] sourceChildren = source.listFiles();
		for (File file : sourceChildren) {
			if (file.isDirectory()) {
				File destDir = new File(dest, file.getName());
				if (!destDir.mkdirs()) {
					throw new IOException("Cannot create: " + destDir);
				}
				copyRecursively(file, destDir);
			} else {
				FileUtils.copyFileToDirectory(file, dest);
			}
		}
	}

	private void generateAnt() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(output, "update.xml"));
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
					+ getRelativePath(pub, toRemove) + "\"/>");
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
				File nextFile = getFileIn(next, getRelativePath(pub, pubFile));
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
				File pubFile = getFileIn(pub, getRelativePath(next, nextFile));
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

	private String getRelativePath(File base, File file) {
		String absolutePath = file.getAbsolutePath();
		String path = absolutePath.substring(base.getAbsolutePath().length());
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
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
}