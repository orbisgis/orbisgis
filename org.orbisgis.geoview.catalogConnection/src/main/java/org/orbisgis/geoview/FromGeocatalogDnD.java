package org.orbisgis.geoview;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.IResourceDnD;
import org.orbisgis.core.resourceTree.ResourceTreeModel;
import org.orbisgis.geocatalog.resources.FileGdmsSource;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.toc.ILayerResource;

public class FromGeocatalogDnD implements IResourceDnD {

	public boolean drop(ResourceTreeModel model, IResource[] draggedNodes,
			IResource dropNode) {
		boolean managed = false;
		for (IResource resource : draggedNodes) {
			if (resource instanceof FileGdmsSource) {
				String name = resource.getName();
				try {
					DataSource ds = OrbisgisCore.getDSF().getDataSource(name);
					ILayer vector = LayerFactory.createVectorialLayer(name, ds);
					((ILayerResource)dropNode).getLayer().put(vector);
					managed = true;
				} catch (DriverLoadException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchTableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DataSourceCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CRSException e) {
					// TODO ERROR: THE USER SHOULD KNOW THIS!
				}
			}
		}

		return managed;
	}
}
