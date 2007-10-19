package org.sif;

import java.awt.Component;
import java.net.URL;

public interface UIPanel {

	URL getIconURL();

	String getTitle();

	void initialize();

	String validate();

	Component getComponent();

}
