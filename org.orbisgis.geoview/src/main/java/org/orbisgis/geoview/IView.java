package org.orbisgis.geoview;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

public interface IView {

	Component getComponent(GeoView2D geoview);

	void saveStatus(OutputStream oos);

	void loadStatus(InputStream ois);

	void delete();

}
