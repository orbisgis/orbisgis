package org.orbisgis.core;

import java.io.File;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.driver.DriverException;
import org.orbisgis.core.actions.ActionControlsRegistry;
import org.orbisgis.core.errorListener.ErrorFrame;
import org.orbisgis.core.errorListener.ErrorMessage;
import org.orbisgis.core.rasterDrivers.AscDriver;
import org.orbisgis.core.rasterDrivers.TifDriver;
import org.orbisgis.core.windows.EPWindowHelper;
import org.orbisgis.core.windows.IWindow;
import org.orbisgis.pluginManager.PluginActivator;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.pluginManager.workspace.Workspace;

public class OrbisgisCore implements PluginActivator {
	private static DataSourceFactory dsf;

	public static DataSourceFactory getDSF() {
		if (dsf == null) {
			dsf = new DataSourceFactory();
		}
		return dsf;
	}

	/**
	 * Registers the source in the DataSourceFactory. If the name collides with
	 * some existing name, a derivation of it is used
	 *
	 * @param tmpName
	 * @param fileSourceDefinition
	 *
	 * @return The name used to register
	 */
	public static String registerInDSF(String name, DataSourceDefinition dsd) {
		String extension = FileUtility.getFileExtension(name);
		String nickname = name.substring(0, name.indexOf("." + extension));
		String tmpName = nickname;
		int i = 0;
		while (OrbisgisCore.getDSF().exists(tmpName)) {
			i++;
			tmpName = nickname + "_" + i;
		}

		OrbisgisCore.getDSF().registerDataSource(tmpName, dsd);

		return tmpName;
	}

	public void start() throws Exception {
		// Initialize data source factory
		Workspace workspace = PluginManager.getWorkspace();
		File sourcesDir = workspace.getFile("sources");
		if (!sourcesDir.exists()) {
			sourcesDir.mkdirs();
		}
		File tempDir = workspace.getFile("temp");
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		dsf = new DataSourceFactory(sourcesDir.getAbsolutePath(), tempDir
				.getAbsolutePath());

		// Register raster drivers
		OrbisgisCore.getDSF().getSourceManager().getDriverManager()
				.registerDriver("asc driver", AscDriver.class);
		OrbisgisCore.getDSF().getSourceManager().getDriverManager()
				.registerDriver("tif driver", TifDriver.class);
		

		// Load windows status
		EPWindowHelper.loadStatus(workspace);

		// Install the error listener
		PluginManager.addSystemListener(new SystemListener() {

			public void warning(String userMsg, Throwable e) {
				// TODO remove after Strasbourg error(new ErrorMessage(userMsg,
				// e, false));
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
				error(new ErrorMessage(userMsg, e, true));
			}

			public void statusChanged() {
				ActionControlsRegistry.refresh();
			}

		});

		// Pipeline the warnings in gdms to the warning system in the
		// application
		OrbisgisCore.getDSF().setWarninglistener(new WarningListener() {

			public void throwWarning(String msg) {
				PluginManager.warning(msg, null);
			}

			public void throwWarning(String msg, Throwable t, Object source) {
				PluginManager.warning(msg, t);
			}

		});
	}

	public void stop() {
		EPWindowHelper.saveStatus(PluginManager.getWorkspace());
		try {
			dsf.getSourceManager().saveStatus();
		} catch (DriverException e) {
			PluginManager.error("Cannot save source information", e);
		}
	}

	public boolean allowStop() {
		return true;
	}

}
