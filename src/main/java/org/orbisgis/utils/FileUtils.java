package org.orbisgis.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

	public static void copyFileToDirectory(File file, File destDir)
			throws IOException {
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

	public static void download(URL url, File file) throws IOException {
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

	/**
	 * Zips the specified file or folder
	 * 
	 * @param toZip
	 * @param outFile
	 * @throws IOException
	 */
	public static void zip(File toZip, File outFile) throws IOException {
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(outFile)));

		// writing stream
		BufferedInputStream in = null;

		byte[] data = new byte[BUF_SIZE];
		ArrayList<File> listToZip = new ArrayList<File>();
		listToZip.add(toZip);

		while (listToZip.size() > 0) {
			File file = listToZip.remove(0);
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				for (File child : children) {
					listToZip.add(child);
				}
			} else {
				in = new BufferedInputStream(new FileInputStream(file),
						BUF_SIZE);

				out.putNextEntry(new ZipEntry(getRelativePath(toZip, file)));
				int count;
				while ((count = in.read(data, 0, BUF_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				out.closeEntry(); // close each entry
			}
		}
		out.flush();
		out.close();
		in.close();
	}

	public static void unzip(File zipFile, File destDir) throws IOException {
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(zipFile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			int count;
			byte data[] = new byte[BUF_SIZE];
			// write the files to the disk
			File newFile = new File(destDir, entry.getName());
			File parentFile = newFile.getParentFile();
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Cannot create directory:" + parentFile);
			}
			if (!entry.isDirectory()) {
				FileOutputStream fos = new FileOutputStream(newFile);
				dest = new BufferedOutputStream(fos, BUF_SIZE);
				while ((count = zis.read(data, 0, BUF_SIZE)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		}
		zis.close();
	}

	public static String getRelativePath(File base, File file) {
		String absolutePath = file.getAbsolutePath();
		String path = absolutePath.substring(base.getAbsolutePath().length());
		while (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	public static byte[] getContent(File file) throws FileNotFoundException,
			IOException {
		FileInputStream fis = new FileInputStream(file);
		return getContent(fis);
	}

	public static byte[] getContent(InputStream fis) throws IOException {
		DataInputStream dis = new DataInputStream(fis);
		byte[] buffer = new byte[dis.available()];
		dis.readFully(buffer);
		dis.close();
		return buffer;
	}

	public static byte[] getMD5(File file) throws FileNotFoundException,
			IOException, NoSuchAlgorithmException {
		byte[] content = getContent(file);
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(content, 0, content.length);
		return m.digest();
	}

	public static String toHexString(byte[] messageDigest) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
		}

		return hexString.toString();
	}

	public static void setContents(File file, String content) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(content.getBytes());
		bos.close();
	}

	public static String getFileNameWithoutExtensionU(File file) {
		String name = file.getName();
		int extensionStart = name.lastIndexOf('.');
		String ret = name;
		if (extensionStart != -1) {
			ret = name.substring(0, name.indexOf(name
					.substring(extensionStart)));
		}
		
		return ret;
	}
}
