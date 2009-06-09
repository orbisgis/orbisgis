package org.orbisgis.core;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File file = new File("../lib/batik-awt-util-1.6.jar");
		ZipFile jar = null;
		try {
			jar = new ZipFile(file.getPath());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
