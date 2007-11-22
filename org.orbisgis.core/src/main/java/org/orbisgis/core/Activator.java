package org.orbisgis.core;

import org.orbisgis.core.errorListener.ErrorFrame;
import org.orbisgis.core.errorListener.ErrorMessage;
import org.orbisgis.core.rasterDrivers.AscDriver;
import org.orbisgis.core.rasterDrivers.TifDriver;
import org.orbisgis.core.rasterDrivers.XYZDEMDriver;
import org.orbisgis.pluginManager.ErrorListener;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.pluginManager.PluginManager;

public class Activator implements PluginActivator {

	public void start() {
		EPWindowHelper.showInitial();

		PluginManager.addMessageListener(new ErrorListener() {

			public void warning(String userMsg, Throwable e) {
				error(new ErrorMessage(userMsg, e));
			}

			private void error(ErrorMessage errorMessage) {
				IWindow[] wnds = EPWindowHelper
						.getWindows("org.orbisgis.core.ErrorWindow");
				IWindow wnd;
				if (wnds.length == 0) {
					wnd = EPWindowHelper
							.createWindow("org.orbisgis.core.ErrorWindow");
				} else {
					wnd = wnds[0];
				}
				((ErrorFrame) wnd).addError(errorMessage);
				wnd.showWindow();
			}

			public void error(String userMsg, Throwable e) {
				error(new ErrorMessage(userMsg, e));
			}

		});

		OrbisgisCore.getDSF().getSourceManager().getDriverManager()
				.registerDriver("asc driver", AscDriver.class);
		OrbisgisCore.getDSF().getSourceManager().getDriverManager()
				.registerDriver("tif driver", TifDriver.class);
		OrbisgisCore.getDSF().getSourceManager().getDriverManager()
		.registerDriver("xyzDEM driver", XYZDEMDriver.class);
	}

	public void stop() {
	}

}