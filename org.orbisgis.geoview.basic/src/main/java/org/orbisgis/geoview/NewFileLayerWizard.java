package org.orbisgis.geoview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.wizards.OpenGdmsFilePanel;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.pluginManager.PluginManager;
import org.sif.UIFactory;
import org.sif.UIPanel;

/**
 *
 */
public class NewFileLayerWizard implements INewLayer {

	public ILayer[] getLayers() {
		OpenGdmsFilePanel filePanel = new OpenGdmsFilePanel(
				"Select the file to add");
		ArrayList<ILayer> ret = new ArrayList<ILayer>();
		if (UIFactory.showDialog(new UIPanel[] { filePanel })) {
			File[] files = filePanel.getSelectedFiles();
			for (File file : files) {
				try {
					FileSourceDefinition fileSourceDefinition = new FileSourceDefinition(
							file);
					String registerName = OrbisgisCore.getDSF()
							.getSourceManager().getSourceName(
									fileSourceDefinition);
					if (registerName == null) {
						registerName = OrbisgisCore.registerInDSF(file
								.getName(), fileSourceDefinition);
					}
					ILayer layer = LayerFactory.createLayer(registerName);
					ret.add(layer);
				} catch (DriverLoadException e) {
					PluginManager.error("No suitable driver for file " + file,
							e);
				} catch (DataSourceCreationException e) {
					PluginManager.error(
							"Cannot instantiate data source for file " + file,
							e);
				} catch (NoSuchTableException e) {
					throw new RuntimeException("Bug!");
				} catch (IOException e) {
					PluginManager.error("Cannot access data " + file, e);
				}
			}
		}

		return ret.toArray(new ILayer[0]);
	}

	public String getName() {
		return "File";
	}

}
