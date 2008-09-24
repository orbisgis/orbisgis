package org.orbisgis.pluginManager.updates;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class CreateUpdate {

	private File pub;
	private File next;
	private ArrayList<File> added = new ArrayList<File>();
	private ArrayList<File> removed = new ArrayList<File>();
	private ArrayList<File> modified = new ArrayList<File>();

	public CreateUpdate(File pub, File next) {
		this.pub = pub;
		this.next = next;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Usage: java CreateUpdate "
					+ "latest-public-binary-folder new"
					+ "-binary-folder update-output-dir");
		}

		File pub = new File(args[0]);
		File next = new File(args[1]);

		CreateUpdate cu = new CreateUpdate(pub, next);
		cu.create();

	}

	public synchronized void create() throws Exception {
		if (!pub.exists() || !next.exists()) {
			throw new RuntimeException("Both folders must exist");
		}

		diff();

		generateRelease();
	}

	private void generateRelease() {
		// TODO Auto-generated method stub

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