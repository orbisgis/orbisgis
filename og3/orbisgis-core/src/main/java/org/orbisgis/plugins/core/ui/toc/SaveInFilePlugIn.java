package org.orbisgis.plugins.core.ui.toc;

import java.io.File;
import java.util.Observable;

import javax.swing.JOptionPane;

import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.driverManager.Driver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.source.AndDriverFilter;
import org.gdms.source.FileDriverFilter;
import org.gdms.source.SourceManager;
import org.gdms.source.VectorialDriverFilter;
import org.gdms.source.WritableDriverFilter;
import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;
import org.orbisgis.plugins.sif.SaveFilePanel;
import org.orbisgis.plugins.sif.UIFactory;

public class SaveInFilePlugIn extends AbstractPlugIn {

	public boolean execute(PlugInContext context) {
		MapContext mapContext = context.getWorkbenchContext().getWorkbench()
				.getFrame().getToc().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();

		if (selectedResources.length == 0) {
			execute(mapContext, null);
		} else {
			for (ILayer resource : selectedResources) {
				execute(mapContext, resource);
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
						Names.POPUP_TOC_EXPORT_PATH2 },
				Names.POPUP_TOC_EXPORT_GROUP, false, null, wbContext);
	}

	@Override
	public void update(Observable o, Object arg) {
	}

	public void execute(MapContext mapContext, ILayer layer) {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.plugins.core.ui.editorViews.toc.actions.SaveInFile",
				"Choose a file format");
		try {
			DataManager dm = Services.getService(DataManager.class);
			SourceManager sourceManager = dm.getSourceManager();
			DriverManager driverManager = sourceManager.getDriverManager();

			Driver[] filtered = driverManager.getDrivers(new AndDriverFilter(
					new FileDriverFilter(), new VectorialDriverFilter(),
					new WritableDriverFilter()));
			for (int i = 0; i < filtered.length; i++) {
				FileDriver fileDriver = (FileDriver) filtered[i];
				String[] extensions = fileDriver.getFileExtensions();
				outfilePanel.addFilter(extensions, fileDriver
						.getTypeDescription());
			}

			if (UIFactory.showDialog(outfilePanel)) {
				final File savedFile = new File(outfilePanel.getSelectedFile()
						.getAbsolutePath());
				String fileName = savedFile.getName();
				int index = fileName.lastIndexOf('.');
				if (index != -1) {
					fileName = fileName.substring(0, index);
				}
				final FileSourceDefinition def = new FileSourceDefinition(
						savedFile);
				Services.getService(DataManager.class).getDSF()
						.getSourceManager().register(fileName, def);
				Services.getService(DataManager.class).getDSF().saveContents(
						fileName, layer.getDataSource());
				JOptionPane.showMessageDialog(null,
						"The file has been saved and added in the geocatalog.");
			}
		} catch (DriverException e) {
			Services.getErrorManager().error("Cannot save the layer.", e);
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isVisible() {
		return getUpdateFactory().checkLayerAvailability();
	}

	public boolean accepts(MapContext mc, ILayer layer) {
		try {
			return layer.isVectorial();
		} catch (DriverException e) {
			return false;
		}
	}

	public boolean acceptsSelectionCount(int selectionCount) {
		return 1 == selectionCount;
	}
}
