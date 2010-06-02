package org.orbisgis.core.ui.plugins.toc;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.SourceManager;
import org.gdms.source.VectorialDriverFilter;
import org.gdms.source.WritableDriverFilter;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.SelectionAvailability;
import org.orbisgis.core.ui.pluginSystem.PlugInContext.LayerAvailability;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class SaveInFilePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) {
		MapContext mapContext = getPlugInContext().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();		
		for (ILayer resource : selectedResources) {
			final SaveFilePanel outfilePanel = new SaveFilePanel(
					"org.orbisgis.core.ui.editorViews.toc.actions.SaveInFile",
					"Choose a file format");

			DataManager dm = Services.getService(DataManager.class);
			final DataSourceFactory dsf = dm.getDSF();
			SourceManager sourceManager = dm.getSourceManager();
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
				bm.backgroundOperation(new ExportInFileOperation(dsf, resource
						.getName(), savedFile));

			}			
		}
		return true;
	}

	public void initialize(PlugInContext context) throws Exception {
		WorkbenchContext wbContext = context.getWorkbenchContext();
		WorkbenchFrame frame = wbContext.getWorkbench().getFrame().getToc();
		context.getFeatureInstaller().addPopupMenuItem(
				frame,
				this,
				new String[] { Names.POPUP_TOC_EXPORT_PATH1, 
						Names.TOC_EXPORT_SAVEIN_FILE},
				Names.POPUP_TOC_EXPORT_GROUP, false, null, wbContext);
	}

	public boolean isEnabled() {
		return getPlugInContext().checkLayerAvailability(
				new SelectionAvailability[] {SelectionAvailability.SUPERIOR},
				0,
				new LayerAvailability[] {LayerAvailability.VECTORIAL});
	}
	
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}
}
