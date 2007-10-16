/**
 * 
 */
package org.gdms;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverUtilities;

class FileTestSource extends TestSource {

	private String fileName;
	private File originalFile;

	public FileTestSource(String name, String file) {
		super(name);
		this.fileName = new File(file).getName();
		this.originalFile = new File(file);
	}

	public void backup() throws Exception {
		File dest = new File(SourceTest.backupDir.getAbsolutePath() + "/"
				+ name);
		dest.mkdirs();
		File backupFile = new File(dest, fileName);
		String prefix = originalFile.getAbsolutePath();
		prefix = prefix.substring(0, prefix.length() - 4);
		copyGroup(new File(prefix), dest);

		FileSourceDefinition def = new FileSourceDefinition(backupFile);
		SourceTest.dsf.registerDataSource(name, def);
	}

	public void copyGroup(final File prefix, File dir) throws IOException {
		File[] dbFiles = prefix.getParentFile().listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(prefix.getName());
			}
		});

		for (int i = 0; i < dbFiles.length; i++) {
			DriverUtilities.copy(dbFiles[i], new File(dir, dbFiles[i]
					.getName()));
		}
	}

}