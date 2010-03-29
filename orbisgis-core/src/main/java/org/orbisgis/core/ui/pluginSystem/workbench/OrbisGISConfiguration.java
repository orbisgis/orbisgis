package org.orbisgis.core.ui.pluginSystem.workbench;

import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.BeanShellConsoleViewPlugIn;
import org.orbisgis.core.ui.plugins.views.EditorViewPlugIn;
import org.orbisgis.core.ui.plugins.views.GeoCatalogViewPlugIn;
import org.orbisgis.core.ui.plugins.views.GeocognitionViewPlugIn;
import org.orbisgis.core.ui.plugins.views.GeomarkViewPlugIn;
import org.orbisgis.core.ui.plugins.views.InformationViewPlugIn;
import org.orbisgis.core.ui.plugins.views.OutputViewPlugIn;
import org.orbisgis.core.ui.plugins.views.SQLConsoleViewPlugIn;
import org.orbisgis.core.ui.plugins.views.TocViewPlugIn;

//all views plugins so orbisgis UI
public class OrbisGISConfiguration {

	public static void loadOrbisGISPlugIns(
			final WorkbenchContext workbenchContext) throws Exception {
		PlugInContext pluginContext = workbenchContext.createPlugInContext();

		EditorViewPlugIn editorViewPlugIn = new EditorViewPlugIn();
		editorViewPlugIn.initialize(pluginContext);

		OutputViewPlugIn outputViewPlugIn = new OutputViewPlugIn();
		outputViewPlugIn.initialize(pluginContext);

		GeocognitionViewPlugIn geocognitionPlugin = new GeocognitionViewPlugIn();
		geocognitionPlugin.initialize(pluginContext);	

		BeanShellConsoleViewPlugIn beanShellConsoleViewPlugIn = new BeanShellConsoleViewPlugIn();
		beanShellConsoleViewPlugIn.initialize(pluginContext);

		InformationViewPlugIn informationViewPlugIn = new InformationViewPlugIn();
		informationViewPlugIn.initialize(pluginContext);

		GeoCatalogViewPlugIn catalogViewPlugIn = new GeoCatalogViewPlugIn();
		catalogViewPlugIn.initialize(pluginContext);

		SQLConsoleViewPlugIn consoleViewPlugIn = new SQLConsoleViewPlugIn();
		consoleViewPlugIn.initialize(pluginContext);

		GeomarkViewPlugIn geomarkViewPlugIn = new GeomarkViewPlugIn();
		geomarkViewPlugIn.initialize(pluginContext);

		TocViewPlugIn tocViewPlugIn = new TocViewPlugIn();
		tocViewPlugIn.initialize(pluginContext);
	}
}
