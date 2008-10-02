package org.orbisgis.updates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.orbisgis.Services;
import org.orbisgis.pluginManager.ApplicationInfo;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.updates.UpdateDiscovery;
import org.orbisgis.pluginManager.updates.UpdateInfo;
import org.orbisgis.workspace.Workspace;

public class DefaultUpdateManager implements Runnable, UpdateManager {

	private static final Logger logger = Logger.getLogger(DefaultUpdateManager.class);

	private static final String UPDATE_SITE_PROPERTY_NAME = "update-site";
	private URL updateSiteURL;

	private Exception error;

	private ArrayList<File> updateFiles;

	public DefaultUpdateManager() {
		// default URL
		try {
			this.updateSiteURL = new URL("file:/tmp/");
		} catch (MalformedURLException e) {
			throw new RuntimeException("bug!", e);
		}
	}

	private void loadConfiguration() throws IOException {
		Workspace ws = Services.getService(Workspace.class);
		File propertyFile = ws.getFile("org.orbisgis.updates.conf.properties");
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(propertyFile));
			updateSiteURL = new URL(p.getProperty(UPDATE_SITE_PROPERTY_NAME));
		} catch (FileNotFoundException e) {
			// keep the previous configuration
		}
	}

	/* (non-Javadoc)
	 * @see org.orbisgis.updates.UpdateManager#startSearch()
	 */
	public void startSearch() {
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	/* (non-Javadoc)
	 * @see org.orbisgis.updates.UpdateManager#run()
	 */
	@Override
	public void run() {
		this.error = null;
		try {
			loadConfiguration();
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

					// Restart the system
					PluginManager pm = Services.getService(PluginManager.class);
					JOptionPane.showMessageDialog(null,
							"The system needs to be restarted to "
									+ "install the updates", "Install updates",
							JOptionPane.INFORMATION_MESSAGE);
					pm.stop();
				}

			}
		} catch (Exception e) {
			this.error = e;
			logger.error("Cannot update system", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.orbisgis.updates.UpdateManager#getUpdateFiles()
	 */
	public ArrayList<File> getUpdateFiles() {
		return updateFiles;
	}

	/* (non-Javadoc)
	 * @see org.orbisgis.updates.UpdateManager#getError()
	 */
	public Exception getError() {
		return error;
	}

}
