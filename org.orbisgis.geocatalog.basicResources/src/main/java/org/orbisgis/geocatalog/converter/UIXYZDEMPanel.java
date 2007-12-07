package org.orbisgis.geocatalog.converter;

import java.awt.Component;
import java.net.URL;

import org.sif.UIPanel;

public class UIXYZDEMPanel implements UIPanel {

	XYZDEMPanel xyzDEMPanel;

	public Component getComponent() {
		xyzDEMPanel = new XYZDEMPanel();
		return xyzDEMPanel;
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {

		return "XYZ DEM convert step 1";
	}

	public String initialize() {
		xyzDEMPanel.getPixelSizeField().setText("1.0");
		// XYZDEMPanel.getNodataValueField().setText("1.0");
		return null;
	}

	public String validate() {
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
