package org.orbisgis.plugins.core.ui;

import org.orbisgis.plugins.core.ui.utils.StringUtil;

public abstract class Extension implements Configuration {
	public String getName() {
		// Package is null if default package. [Jon Aquino]
		return StringUtil.toFriendlyName(getClass().getName(), "Extension")
				+ (getClass().getPackage() == null ? "" : " ("
						+ getClass().getPackage().getName() + ")");
	}

	public String getVersion() {
		return "";
	}
}
