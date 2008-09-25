package org.orbisgis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

	private static int BUF_SIZE = 1024 * 64;

	/**
	 * Copies the specified folder in the destination folder
	 * 
	 * @param sourceDir
	 * @param destDir
	 * @throws IOException
	 */
	public static void copyDirsRecursively(File sourceDir, File destDir)
			throws IOException {
		File[] sourceChildren = sourceDir.listFiles();
		for (File file : sourceChildren) {
			if (file.isDirectory()) {
				File childDir = new File(destDir, file.getName());
				if (!childDir.exists() && !childDir.mkdirs()) {
					throw new IOException("Cannot create: " + childDir);
				}
				copyDirsRecursively(file, childDir);
			} else {
				copyFileToDirectory(file, destDir);
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	public static void copyFileToDirectory(File file, File destDir) throws IOException {
		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				throw new IOException("Cannot create directories:" + destDir);
			}
		}
		
		File output = new File(destDir, file.getName());
		copy(file, output);
	}

	public static long copy(File input, File output) throws IOException {
		FileInputStream in = null;
		try {
			in = new FileInputStream(input);
			return copy(in, output);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static long copy(File input, File output, byte[] copyBuffer)
			throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(input);
			out = new FileOutputStream(output);
			return copy(in, out, copyBuffer);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static long copy(InputStream in, File outputFile) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
			return copy(in, out);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static long copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		return copy(in, out, buf);
	}

	public static long copy(InputStream in, OutputStream out, byte[] copyBuffer)
			throws IOException {
		long bytesCopied = 0;
		int read = -1;

		while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
			out.write(copyBuffer, 0, read);
			bytesCopied += read;
		}
		return bytesCopied;
	}

}
