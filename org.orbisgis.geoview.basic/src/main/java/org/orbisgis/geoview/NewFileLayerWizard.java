package org.orbisgis.geoview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.FileWizard;

/**
 *
 */
public class NewFileLayerWizard extends FileWizard implements INewLayer {

	public ILayer[] getLayers() {
		ArrayList<ILayer> ret = new ArrayList<ILayer>();
		File[] files = getSelectedFiles();
		for (File file : files) {
			try {
				FileSourceDefinition fileSourceDefinition = new FileSourceDefinition(
						file);
				String registerName = OrbisgisCore.getDSF().getSourceManager()
						.getSourceName(fileSourceDefinition);
				if (registerName == null) {
					registerName = OrbisgisCore.registerInDSF(file.getName(),
							fileSourceDefinition);
				}
				ILayer layer = LayerFactory.createLayer(registerName);
				ret.add(layer);
			} catch (DriverLoadException e) {
				PluginManager.error("No suitable driver for file " + file, e);
			} catch (DataSourceCreationException e) {
				PluginManager.error("Cannot instantiate data source for file "
						+ file, e);
			} catch (NoSuchTableException e) {
				throw new RuntimeException("Bug!");
			} catch (IOException e) {
				PluginManager.error("Cannot access data " + file, e);
			}
		}

		return ret.toArray(new ILayer[0]);
	}

	public String getName() {
		return "Vectorial files";
	}

}
