package org.sif;

import java.net.URL;

public abstract class AbstractUIPanel implements UIPanel {

	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	public String getInfoText() {
		return UIFactory.getDefaultOkMessage();
	}

	public String initialize() {
		return null;
	}

}
