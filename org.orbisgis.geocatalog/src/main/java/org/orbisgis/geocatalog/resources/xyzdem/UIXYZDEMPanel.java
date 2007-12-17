package org.orbisgis.geocatalog.resources.xyzdem;

import java.awt.Component;

import org.sif.AbstractUIPanel;

public class UIXYZDEMPanel extends AbstractUIPanel {

	XYZDEMPanel xyzDEMPanel;

	public Component getComponent() {
		xyzDEMPanel = new XYZDEMPanel();
		return xyzDEMPanel;
	}

	public String getTitle() {

		return "XYZ DEM convert step 1";
	}

	public String initialize() {
		xyzDEMPanel.getPixelSizeField().setText("1.0");
		return null;
	}

	public String validateInput() {
		if (xyzDEMPanel.getPixelSize() == 0) {
			return "The value is not correct!";
		} else if (xyzDEMPanel.getPixelSize() == Float.NaN) {
			return "The value is not correct!";
		}
		return null;
	}

	public float getPixelSize() {
		return xyzDEMPanel.getPixelSize();

	}

}
