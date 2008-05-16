package org.orbisgis.views.documentCatalog.documents;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;

import org.apache.log4j.Logger;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.DataManager;
import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.images.IconLoader;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerCollection;
import org.orbisgis.layerModel.LayerException;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.workspace.Workspace;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.views.documentCatalog.AbstractDocument;
import org.orbisgis.views.documentCatalog.DocumentException;
import org.orbisgis.views.documentCatalog.IDocument;

public class MapDocument extends AbstractDocument implements IDocument {

	private static final Logger logger = Logger.getLogger(MapDocument.class);

	private static final String FILE_NAME_PROPERTY = "file-name";

	private MapContext mapContext;

	private MySourceListener mySourceListener;

	private File persistenceFile;

	public MapDocument() {
		Workspace ws = (Workspace) Services
				.getService("org.orbisgis.Workspace");
		this.persistenceFile = ws.getNewFile(
				"org.orbisgis.document.MapContext", ".xml");
	}

	public void addDocument(IDocument document) {
		throw new RuntimeException("This document cannot have children");
	}

	public void removeDocument(IDocument document) {
		throw new RuntimeException("This document cannot have children");
	}

	public IDocument getDocument(int index) {
		throw new RuntimeException("This document cannot have children");
	}

	public int getDocumentCount() {
		return 0;
	}

	public MapContext getMapContext() {
		return mapContext;
	}

	public void openDocument(IProgressMonitor pm) throws DocumentException {
		logger.debug("Opening the view " + getName());
		mapContext = new DefaultMapContext();
		if (persistenceFile.exists()) {
			try {
				mapContext.loadStatus(persistenceFile, pm);
			} catch (PersistenceException e) {
				throw new DocumentException("Cannot restore view content", e);
			}
		}
		mySourceListener = new MySourceListener();
		((DataManager) Services.getService("org.orbisgis.DataManager"))
				.getDSF().getSourceManager()
				.addSourceListener(mySourceListener);
	}

	public void closeDocument(IProgressMonitor pm) throws DocumentException {
		fireClosing();
		logger.debug("Closing the view " + getName());
		((DataManager) Services.getService("org.orbisgis.DataManager"))
				.getDSF().getSourceManager().removeSourceListener(
						mySourceListener);
		saveDocument(pm);
		ILayer[] layers = mapContext.getLayers();
		for (ILayer layer : layers) {
			try {
				layer.close();
			} catch (LayerException e) {
				Services.getErrorManager().error(
						"Cannot close layer: " + layer.getName(), e);
			}
		}
		mapContext = null;
	}

	private final class MySourceListener implements SourceListener {

		public void sourceRemoved(final SourceRemovalEvent e) {
			LayerCollection.processLayersLeaves(mapContext.getLayerModel(),
					new DeleteLayerFromResourceAction(e));
		}

		public void sourceNameChanged(SourceEvent e) {
		}

		public void sourceAdded(SourceEvent e) {
		}
	}

	private final class DeleteLayerFromResourceAction implements
			org.orbisgis.layerModel.ILayerAction {

		private ArrayList<String> resourceNames = new ArrayList<String>();

		private DeleteLayerFromResourceAction(SourceRemovalEvent e) {
			String[] aliases = e.getNames();
			for (String string : aliases) {
				resourceNames.add(string);
			}

			resourceNames.add(e.getName());
		}

		public void action(ILayer layer) {
			String layerName = layer.getName();
			if (resourceNames.contains(layerName)) {
				try {
					layer.getParent().remove(layer);
				} catch (LayerException e) {
					Services.getErrorManager().error(
							"Cannot associate layer: " + layer.getName()
									+ ". The layer must be removed manually.");
				}
			}
		}
	}

	public HashMap<String, String> getPersistenceProperties()
			throws DocumentException {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put(FILE_NAME_PROPERTY, persistenceFile.getPath());

		return ret;
	}

	public void saveDocument(IProgressMonitor pm) throws DocumentException {
		try {
			mapContext.saveStatus(persistenceFile, pm);
		} catch (PersistenceException e) {
			throw new DocumentException("Cannot save the map", e);
		}
	}

	public void setPersistenceProperties(HashMap<String, String> properties)
			throws DocumentException {
		persistenceFile = new File(properties.get(FILE_NAME_PROPERTY));
	}

	public boolean allowsChildren() {
		return false;
	}

	public Icon getIcon() {

		return IconLoader.getIcon("map.png");

	}
}
