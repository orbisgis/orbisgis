package org.sif;

import java.awt.Component;
import java.net.URL;

public interface UIPanel {

	URL getIconURL();

	String getTitle();

	String initialize();

	String validate();

	Component getComponent();

}
