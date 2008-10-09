package org.orbisgis.updates;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.orbisgis.ApplicationInfo;
import org.orbisgis.Services;
import org.orbisgis.errorManager.ErrorManager;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.utils.ExecutionException;
import org.orbisgis.workspace.Workspace;

public class DefaultUpdateManager implements Runnable, UpdateManager {

	private static final Logger logger = Logger
			.getLogger(DefaultUpdateManager.class);

	private URL updateSiteURL;

	private boolean searchAtStartup;

	private Exception error;

	private ArrayList<File> updateFiles;

	private ApplyUpdate au;

	public DefaultUpdateManager() {
		this.searchAtStartup = true;
		// default URL
		try {
			this.updateSiteURL = new URL("file:/tmp/");
		} catch (MalformedURLException e) {
			throw new RuntimeException("bug!", e);
		}
	}

	/**
	 * @see org.orbisgis.updates.UpdateManager#startSearch()
	 */
	public void startSearch() {
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/**
	 * @see org.orbisgis.updates.UpdateManager#run()
	 */
	@Override
	public void run() {
		this.error = null;
		try {
			ApplicationInfo ai = Services.getService(ApplicationInfo.class);
			UpdateDiscovery ud = new UpdateDiscovery("OrbisGIS", updateSiteURL);
			UpdateInfo[] updates = ud.getAvailableUpdatesInfo(ai
					.getVersionNumber());
			if (updates.length > 0) {
				int ret = JOptionPane
						.showConfirmDialog(
								null,
								"There are available updates. Install them?\n"
										+ "(They can be installed manually in the Help menu).",
								"OrbisGIS update", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) {
					updateFiles = new ArrayList<File>();
					for (int i = 0; i < updates.length; i++) {
						Workspace ws = Services.getService(Workspace.class);
						File tempFile = File.createTempFile("update", ".zip",
								ws.getTempFolder());
						updateFiles.add(tempFile);
						ud.download(updates[i], tempFile);
					}

					// Apply update
					String classpath = System.getProperty("java.class.path");
					File binaryDir = getBinaryDir(classpath);
					au = new ApplyUpdate(binaryDir, "orbisgis");
					au.applyUpdates(updateFiles.toArray(new File[0]));

					// Restart
					PluginManager pm = Services.getService(PluginManager.class);
					JOptionPane.showMessageDialog(null,
							"The system will be restarted to "
									+ "finish the installation",
							"Install updates", JOptionPane.INFORMATION_MESSAGE);
					pm.stop();
				}

			}
		} catch (Exception e) {
			this.error = e;
			logger.error("Cannot update system", e);
		}
	}

	private File getBinaryDir(String classpath) {
		String separator = System.getProperty("path.separator");
		String[] jars = classpath.split("\\Q" + separator + "\\E");
		return new File(jars[0]).getAbsoluteFile().getParentFile()
				.getParentFile();
	}

	public void applyUpdates() {
		if (au != null) {
			try {
				au.commitNewBinary();
			} catch (IOException e) {
				Services.getService(ErrorManager.class).error(
						"Cannot launch the update commit", e);
			} catch (ExecutionException e) {
				Services.getService(ErrorManager.class).error(
						"Cannot launch the update commit", e);
			}
		}
	}

	/**
	 * @see org.orbisgis.updates.UpdateManager#getError()
	 */
	public Exception getError() {
		return error;
	}

	public boolean isSearchAtStartup() {
		return searchAtStartup;
	}

	public void setSearchAtStartup(boolean searchAtStartup) {
		this.searchAtStartup = searchAtStartup;
	}

	public URL getUpdateSiteURL() {
		return updateSiteURL;
	}

	public void setUpdateSiteURL(URL updateSiteURL) {
		this.updateSiteURL = updateSiteURL;
	}
}
