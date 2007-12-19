package org.sif;

import java.awt.Component;
import java.net.URL;

public interface UIPanel {

	URL getIconURL();

	String getTitle();

	String initialize();

	String postProcess();

	String validateInput();

	Component getComponent();

	String getInfoText();

}
