package org.orbisgis.pluginManager.updates;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.orbisgis.pluginManager.updates.persistence.Update;
import org.orbisgis.pluginManager.updates.persistence.UpdateSite;
import org.orbisgis.utils.FileUtils;

public class UpdateDiscovery {

	private static final Logger logger = Logger
			.getLogger(UpdateDiscovery.class);

	private URL updateSite;
	private String artifactName;

	public UpdateDiscovery(String artifactName, URL updateSite) {
		this.artifactName = artifactName;
		this.updateSite = updateSite;
	}

	public UpdateInfo[] getAvailableUpdatesInfo(String currentVersion)
			throws IOException, JAXBException {
		return getAvailableUpdatesInfo(updateSite, currentVersion);
	}

	UpdateInfo[] getAvailableUpdatesInfo(URL updateSiteURL,
			String currentVersion) throws IOException, JAXBException {
		URL updateDescriptorURL = new URL(updateSiteURL.toExternalForm() + "/"
				+ UpdateUtils.SITE_UPDATES_FILE_NAME);
		File updateSiteFile = File.createTempFile("site-updates", ".xml");

		// create or modify update content
		JAXBContext context = JAXBContext.newInstance(UpdateSite.class
				.getPackage().getName());
		UpdateSite us = null;
		FileUtils.download(updateDescriptorURL, updateSiteFile);
		us = (UpdateSite) context.createUnmarshaller()
				.unmarshal(updateSiteFile);
		List<Update> updateList = us.getUpdate();
		TreeSet<UpdateInfo> ret = new TreeSet<UpdateInfo>(
				new Comparator<UpdateInfo>() {

					@Override
					public int compare(UpdateInfo o1, UpdateInfo o2) {
						return compareVersions(o1.getVersionNumber(), o2
								.getVersionNumber());
					}
				});
		for (int i = 0; i < updateList.size(); i++) {
			Update update = updateList.get(i);
			if (update.getArtifactName().equals(artifactName)
					&& compareVersions(update.getVersionNumber(),
							currentVersion) > 0) {
				URL updateURL = new URL(update.getReleaseFileUrl());
				ret.add(new UpdateInfo(updateURL, update));
			}
		}

		return ret.toArray(new UpdateInfo[0]);
	}

	int compareVersions(String version1, String version2) {
		String[] numbers1 = version1.split("\\Q.\\E");
		String[] numbers2 = version2.split("\\Q.\\E");
		if (numbers2.length < numbers1.length) {
			numbers2 = complete(numbers2, numbers1.length);
		}
		if (numbers1.length < numbers2.length) {
			numbers1 = complete(numbers1, numbers2.length);
		}
		for (int i = 0; i < numbers2.length; i++) {
			int comparison = numbers1[i].compareTo(numbers2[i]);
			if (comparison != 0) {
				return comparison;
			}
		}

		return 0;
	}

	private String[] complete(String[] numbers, int newSize) {
		String[] ret = new String[newSize];
		System.arraycopy(numbers, 0, ret, 0, numbers.length);
		for (int i = numbers.length; i < ret.length; i++) {
			ret[i] = "0";
		}
		return ret;
	}

	/**
	 * Download the update file of the specified update to the specified file
	 * 
	 * @param updateInfo
	 * @param zip
	 * @throws IOException
	 */
	public void download(UpdateInfo updateInfo, File zip) throws IOException {
		FileUtils.download(updateInfo.getFileURL(), zip);

		// verify checksum
		try {
			byte[] calculatedMD5 = FileUtils.getMD5(zip);
			String calculatedString = FileUtils.toHexString(calculatedMD5);
			if (!calculatedString.equals(updateInfo.getChecksum())) {
				throw new IOException("md5 checksum failed");
			}
		} catch (NoSuchAlgorithmException e) {
			logger.error("Cannot verify checksum", e);
			// We just don't verify the checksum
		}
	}

}
