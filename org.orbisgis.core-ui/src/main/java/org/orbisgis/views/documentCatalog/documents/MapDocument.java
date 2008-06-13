/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
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
