package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.io.File;
import java.util.Observable;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.source.VectorialDriverFilter;
import org.gdms.source.WritableDriverFilter;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.toc.ExportInFileOperation;

public class GeocatalogSaveInFilePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().executeGeocatalog();
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame()
				.getGeocatalog();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_GEOCATALOG_EXPORT,
						Names.POPUP_GEOCATALOG_EXPORT_INFILE },
				Names.POPUP_GEOCATALOG_EXPORT_INFILE, false, null, wbContext);

	}

	public void execute(SourceManager sourceManager, String currentNode) {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.core.ui.plugins.views.geocatalog.SaveInFile",
				"Choose a file format");

		DataManager dm = Services.getService(DataManager.class);
		DataSourceFactory dsf = dm.getDSF();
		DriverManager driverManager = sourceManager.getDriverManager();

		Driver[] filtered = driverManager.getDrivers(new AndDriverFilter(
				new FileDriverFilter(), new VectorialDriverFilter(),
				new WritableDriverFilter()));
		for (int i = 0; i < filtered.length; i++) {
			FileDriver fileDriver = (FileDriver) filtered[i];
			String[] extensions = fileDriver.getFileExtensions();
			outfilePanel.addFilter(extensions, fileDriver.getTypeDescription());
		}

		if (UIFactory.showDialog(outfilePanel)) {
			final File savedFile = new File(outfilePanel.getSelectedFile()
					.getAbsolutePath());
			BackgroundManager bm = Services.getService(BackgroundManager.class);
			bm.backgroundOperation(new ExportInFileOperation(dsf, currentNode,
					savedFile));
		}

	}

	public boolean isVisible() {
		return getPlugInContext().geocatalogIsVisible();
	}

	public boolean accepts(SourceManager sourceManager, String selectedNode) {
		Source source = sourceManager.getSource(selectedNode);
		return (source.getType() & SourceManager.WMS) == 0;
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return selectionCount == 1;
	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
