package org.orbisgis.pluginManager.updates;

import java.net.URL;

import org.orbisgis.pluginManager.updates.persistence.Update;

public class UpdateInfo {

	private Update update;
	private URL updateFileURL;

	public UpdateInfo(URL updateFileURL, Update update) {
		this.updateFileURL = updateFileURL;
		this.update = update;
	}

	public URL getFileURL() {
		return updateFileURL;
	}

	public String getVersionNumber() {
		return update.getVersionNumber();
	}

}
