package org.orbisgis.geoview;


public class FromGeocatalogDnD {

//	public boolean drop(ResourceTreeModel model, IResource[] draggedNodes,
//			IResource dropNode) {
//		boolean managed = false;
//		for (IResource resource : draggedNodes) {
//			if (resource instanceof AbstractGdmsSource) {
//				String name = resource.getName();
//				try {
//					DataSource ds = OrbisgisCore.getDSF().getDataSource(name);
//					ILayer vector = LayerFactory.createVectorialLayer(name, ds);
//					((ILayerResource) dropNode).getLayer().put(vector);
//					managed = true;
//				} catch (DriverLoadException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (NoSuchTableException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (DataSourceCreationException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (CRSException e) {
//					// TODO ERROR: THE USER SHOULD KNOW THIS!
//				}
//			}
//		}
//
//		return managed;
//	}
}
