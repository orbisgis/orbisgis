package org.orbisgis.geoview;

import java.io.File;
import java.util.ArrayList;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.file.FileSourceDefinition;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.FileWizard;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;

/**
 *
 */
public class NewFileLayerWizard extends FileWizard implements INewLayer {

	public ILayer[] getLayers() {
		ArrayList<ILayer> ret = new ArrayList<ILayer>();
		File[] files = getSelectedFiles();
		for (File file : files) {
			DataSource ds;
			try {
				String registerName = OrbisgisCore.registerInDSF(
						file.getName(), new FileSourceDefinition(file));
				ds = OrbisgisCore.getDSF().getDataSource(registerName);
				VectorLayer vectorLayer = LayerFactory.createVectorialLayer(registerName, ds);
				ret.add(vectorLayer);
			} catch (DriverLoadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DataSourceCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchTableException e) {
				throw new RuntimeException("Bug!");
			}
		}

		return ret.toArray(new ILayer[0]);
	}

	public String getName() {
		return "Vectorial files";
	}

}
