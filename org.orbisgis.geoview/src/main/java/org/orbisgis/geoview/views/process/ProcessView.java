package org.orbisgis.geoview.views.process;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.IView;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;

public class ProcessView implements IView {

	private ProcessPanel processPanel = new ProcessPanel();

	public void delete() {
	}

	public Component getComponent(GeoView2D geoview) {
		return processPanel;
	}

	public void initialize(GeoView2D geoView2D) {
		PluginManager.addSystemListener(new SystemListener() {

			public void warning(String userMsg, Throwable e) {
			}

			public void statusChanged() {
				processPanel.refresh();
			}

			public void error(String userMsg, Throwable exception) {
			}

		});
	}

	public void loadStatus(InputStream ois) {
	}

	public void saveStatus(OutputStream oos) {
	}

}
