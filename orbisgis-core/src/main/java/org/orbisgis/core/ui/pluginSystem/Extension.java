package org.orbisgis.core.ui.pluginSystem;

import org.orbisgis.core.ui.pluginSystem.utils.StringUtil;

public abstract class Extension implements Configuration {
	public String getName() {
		// Package is null if default package.
		return StringUtil.toFriendlyName(getClass().getName(), "Extension")
				+ (getClass().getPackage() == null ? "" : " ("
						+ getClass().getPackage().getName() + ")");
	}

	public String getVersion() {
		return "";
	}
}
