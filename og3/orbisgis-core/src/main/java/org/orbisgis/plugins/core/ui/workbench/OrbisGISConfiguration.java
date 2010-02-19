package org.orbisgis.plugins.core.ui.workbench;

import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.views.BeanShellConsoleViewPlugIn;
import org.orbisgis.plugins.core.ui.views.EditorViewPlugIn;
import org.orbisgis.plugins.core.ui.views.GeoCatalogViewPlugIn;
import org.orbisgis.plugins.core.ui.views.GeocognitionViewPlugIn;
import org.orbisgis.plugins.core.ui.views.GeomarkViewPlugIn;
import org.orbisgis.plugins.core.ui.views.InformationViewPlugIn;
import org.orbisgis.plugins.core.ui.views.JobViewPlugIn;
import org.orbisgis.plugins.core.ui.views.MemoryViewPlugIn;
import org.orbisgis.plugins.core.ui.views.OutputViewPlugIn;
import org.orbisgis.plugins.core.ui.views.SQLConsoleViewPlugIn;
import org.orbisgis.plugins.core.ui.views.TocViewPlugIn;

public class OrbisGISConfiguration {

	public static void loadOrbisGISPlugIns(
			final WorkbenchContext workbenchContext) throws Exception {
		PlugInContext pluginContext = workbenchContext.createPlugInContext();

		EditorViewPlugIn editorViewPlugIn = new EditorViewPlugIn();
		editorViewPlugIn.initialize(pluginContext);

		OutputViewPlugIn outputViewPlugIn = new OutputViewPlugIn();
		outputViewPlugIn.initialize(pluginContext);

		MemoryViewPlugIn memoryPlugIn = new MemoryViewPlugIn();
		memoryPlugIn.initialize(pluginContext);

		GeocognitionViewPlugIn geocognitionPlugin = new GeocognitionViewPlugIn();
		geocognitionPlugin.initialize(pluginContext);

		JobViewPlugIn jobViewPlugIn = new JobViewPlugIn();
		jobViewPlugIn.initialize(pluginContext);

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
