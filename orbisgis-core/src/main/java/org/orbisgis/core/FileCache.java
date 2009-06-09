/*
 * GearScape is a Geographic Information System focused on geo-processing. 
 * It is able to retrieve, process and display spatial data of both vector 
 * and raster type. GearScape is distributed under GPL 3 license.
 *
 * Copyright (C) 2009 the GearScape team
 *
 * This file is part of the GearScape's project source code.
 *
 * GearScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GearScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GearScape. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://gearscape.forge.osor.eu/>
 */
package org.orbisgis.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;

import org.orbisgis.utils.FileUtils;

public class FileCache {

	private static final String CACHE_DESCRIPTOR_FILE = "cache.descriptor";
	private File localCacheDir;
	private HashMap<URL, CacheInfo> url2path = new HashMap<URL, CacheInfo>();
	private String suffix;

	public FileCache(File localCacheDir, String suffix) throws IOException {
		this.suffix = suffix;
		if (suffix == null) {
			this.suffix = "";
		}
		this.localCacheDir = localCacheDir;
		if (!localCacheDir.exists() && !localCacheDir.mkdirs()) {
			throw new IOException("The local cache does "
					+ "not exists and cannot be created");
		}

		File desc = new File(localCacheDir, CACHE_DESCRIPTOR_FILE);
		if (desc.exists()) {
			String content = new String(FileUtils.getContent(desc));
			String[] lines = content.split("\n");
			int index = 0;
			while (index < lines.length) {
				URL url = new URL(lines[index]);
				String fileName = lines[index + 1];
				long lastModified = Long.parseLong(lines[index + 2]);
				url2path.put(url, new CacheInfo(fileName, lastModified));
				index = index + 3;
			}
		}
	}

	public FileCache(File tempDir) throws IOException {
		this(tempDir, "");
	}

	/**
	 * Clear the cache local directory. Caution! It removes all files in the
	 * local cache directory so it is recommended not to store any file manually
	 * there
	 * 
	 * @throws IOException
	 */
	public void clear() throws IOException {
		File[] files = localCacheDir.listFiles();
		for (File file : files) {
			if (!file.delete()) {
				throw new IOException("Cannot delete file: "
						+ file.getAbsolutePath());
			}
		}
		url2path.clear();
	}

	public boolean isUpdated(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		long lastModified = conn.getLastModified();
		CacheInfo info = url2path.get(url);
		if (info != null) {
			return info.lastModified == lastModified;
		} else {
			return false;
		}
	}

	public File getFile(URL url) throws IOException {
		CacheInfo info = url2path.get(url);
		if (info == null) {
			File file = File.createTempFile("file", ".cache" + suffix,
					localCacheDir);
			info = new CacheInfo(file.getName(), -1);
		}
		URLConnection conn = url.openConnection();
		long lm = conn.getLastModified();
		File file = new File(localCacheDir, info.fileName);
		if (!file.exists() || (lm != info.lastModified)) {
			FileUtils.copy(conn.getInputStream(), new FileOutputStream(file));
			info.lastModified = lm;
			url2path.put(url, info);
			persist();
		}

		return file;
	}

	private void persist() throws IOException {
		StringBuilder content = new StringBuilder();
		Iterator<URL> it = url2path.keySet().iterator();
		while (it.hasNext()) {
			URL url = it.next();
			CacheInfo info = url2path.get(url);
			content.append(url.toString()).append("\n");
			content.append(info.fileName).append("\n");
			content.append(info.lastModified).append("\n");
		}
		FileUtils.setContents(new File(localCacheDir, CACHE_DESCRIPTOR_FILE),
				content.toString());
	}

	public class CacheInfo {
		private long lastModified;
		private String fileName;

		public CacheInfo(String absolutePath, long lastModified) {
			fileName = absolutePath;
			this.lastModified = lastModified;
		}
	}

}
