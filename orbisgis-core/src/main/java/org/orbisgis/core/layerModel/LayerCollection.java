/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/
package org.orbisgis.core.layerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.grap.model.GeoRaster;
import org.orbisgis.core.layerModel.persistence.LayerCollectionType;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.RasterLegend;
import org.orbisgis.core.renderer.legend.WMSLegend;

import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.core.renderer.se.FeatureTypeStyle;
import org.orbisgis.core.renderer.se.Rule;

public class LayerCollection extends AbstractLayer {
	private List<ILayer> layerCollection;

        /**
         * Create a new LayerCollection with the name name.
         * @param name
         */
	public LayerCollection(String name) {
		super(name);
		layerCollection = new ArrayList<ILayer>();
	}

        /**
         * Retrieve the layer collection as a list of layers.
         * @return
         */
	List<ILayer> getLayerCollection() {
		return layerCollection;
	}

        /**
         * Returns the index of the first occurrence of the specified element in this list,
         * or -1 if this list does not contain the element.
         * @param layer
         * @return
         */
	public int getIndex(ILayer layer) {
		return layerCollection.indexOf(layer);
	}

        /**
         * Get the layer stored at the given index in the collection.
         * @param index
         * @return
         */
	public ILayer getLayer(final int index) {
            //TODO : get will throw a IndexOutOfBoundsException which is nor catch neither managed here...
		return layerCollection.get(index);
	}



	public void addLayer(final ILayer layer) throws LayerException {
		addLayer(layer, false);
	}

	/**
         * Insert layer at the given index.
         * @param layer
         * @param index
         * @throws LayerException
         */
	public void insertLayer(final ILayer layer, int index)
			throws LayerException {
		insertLayer(layer, index, false);
	}

	/**
	 * Removes the layer from the collection
	 * 
	 * @param layerName
	 * @return the layer removed or null if the layer does not exists
	 * @throws LayerException
	 * 
	 */
	public ILayer remove(final String layerName) throws LayerException {
		for (int i = 0; i < size(); i++) {
			if (layerName.equals(layerCollection.get(i).getName())) {
				return remove(layerCollection.get(i));
			}
		}
		return null;
	}

        /**
         * Retrieve the children of this node as an array.
         * @return
         */
	public ILayer[] getChildren() {
		if (null != layerCollection) {
			ILayer[] result = new ILayer[size()];
			return layerCollection.toArray(result);
		} else {
			return null;
		}
	}

        /**
         * Return the number of children in this collection.
         * @return
         */
	private int size() {
		return layerCollection.size();
	}

	/**
	 * Check if this layer is visible or not. It is visible if at least one of its children is visible,
         * false otherwise.
	 * @see org.orbisgis.core.layerModel.ILayer#isVisible()
	 */
	public boolean isVisible() {
		for (ILayer layer : getChildren()) {
			if (layer.isVisible()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the visible attribute. We don't see this object (which is a collection,
         * not a layer) but its leaves. Consequently, whe using this method, we set
         * the visible attribute to isVisible for all the leaves of this collection.
	 * @throws LayerException
	 * @see org.orbisgis.core.layerModel.ILayer#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible) throws LayerException {
		for (ILayer layer : getChildren()) {
			layer.setVisible(isVisible);
		}
		fireVisibilityChanged();
	}

    @Override
    public void setFeatureTypeStyle(FeatureTypeStyle fts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public ArrayList<Rule> getRenderingRule() throws DriverException {
		throw new UnsupportedOperationException("A layer collection doesn't have any rule !");
	}

        /**
         *
         * @return
         */
	public Envelope getEnvelope() {
		final GetEnvelopeLayerAction tmp = new GetEnvelopeLayerAction();
		processLayersLeaves(this, tmp);
		return tmp.getGlobalEnvelope();
	}

	public ILayer remove(ILayer layer) throws LayerException {
		return remove(layer, false);
	}

        /**
         * Inform if children can be added to this layer. It is a collection, so they are.
         * @return true.
         */
	public boolean acceptsChilds() {
		return true;
	}

        /**Add the LayerListener listener to this, and to each child of this.
         *
         * @param listener
         */
	public void addLayerListenerRecursively(LayerListener listener) {
		this.addLayerListener(listener);
		for (ILayer layer : layerCollection) {
			layer.addLayerListenerRecursively(listener);
		}
	}

        /**
         * Remove the LayerListener listener of this' listeners, and of its children's listeners
         * @param listener
         */
	public void removeLayerListenerRecursively(LayerListener listener) {
		this.removeLayerListener(listener);
		for (ILayer layer : layerCollection) {
			layer.removeLayerListenerRecursively(listener);
		}
	}

        /**
         * Close this layer and all its children.
         * @throws LayerException
         */
	public void close() throws LayerException {
		for (ILayer layer : layerCollection) {
			layer.close();
		}
	}
        /**
         * Open the layer and all its children.
         * @throws LayerException
         */
	public void open() throws LayerException {
		for (ILayer layer : layerCollection) {
			layer.open();
		}
	}
        /**
         * Add a new layer to this collection.
         * @param layer
         * @param isMoving
         * @throws LayerException
         */

	public void addLayer(ILayer layer, boolean isMoving) throws LayerException {
		if (null != layer) {
			if (isMoving) {
				layerCollection.add(layer);
				layer.setParent(this);
			} else {
				setNamesRecursively(layer, getRoot().getAllLayersNames());
				layerCollection.add(layer);
				layer.setParent(this);
				fireLayerAddedEvent(new ILayer[] { layer });
			}
		}
	}


	public ILayer remove(ILayer layer, boolean isMoving) throws LayerException {
		if (layerCollection.contains(layer)) {
			if (isMoving) {
				if (layerCollection.remove(layer)) {
					return layer;
				} else {
					return null;
				}
			} else {
				ILayer[] toRemove = new ILayer[] { layer };
				if (fireLayerRemovingEvent(toRemove)) {
					if (layerCollection.remove(layer)) {
						fireLayerRemovedEvent(toRemove);
						return layer;
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	public void insertLayer(ILayer layer, int index, boolean isMoving)
			throws LayerException {
		if (null != layer) {
			if (isMoving) {
				layerCollection.add(index, layer);
				layer.setParent(this);
			} else {
				setNamesRecursively(layer, getRoot().getAllLayersNames());
				layerCollection.add(index, layer);
				layer.setParent(this);
				fireLayerAddedEvent(new ILayer[] { layer });
			}
		}

	}

	public int getLayerCount() {
		return layerCollection.size();
	}

	public LayerType saveLayer() {
		LayerCollectionType xmlLayer = new LayerCollectionType();
		xmlLayer.setName(getName());
		for (ILayer child : layerCollection) {
			LayerType xmlChild = child.saveLayer();
			xmlLayer.getLayer().add(xmlChild);
		}

		return xmlLayer;
	}

	public void restoreLayer(LayerType layer) throws LayerException {
	}

	public ILayer getLayerByName(String layerName) {
		for (ILayer layer : layerCollection) {
			if (layer.getName().equals(layerName)) {
				return layer;
			} else {
				ILayer ret = layer.getLayerByName(layerName);
				if (ret != null) {
					return ret;
				}
			}
		}
		return null;
	}
        /**
         * Retrieve the children of this collection that are raster layers
         * @return
         * @throws DriverException
         */
	public ILayer[] getRasterLayers() throws DriverException {
		ILayer[] allLayers = getLayersRecursively();

		ArrayList<ILayer> filterLayer = new ArrayList<ILayer>();

		for (int i = 0; i < allLayers.length; i++) {
			if (allLayers[i].isRaster()) {
				filterLayer.add(allLayers[i]);

			}
		}

		return filterLayer.toArray(new ILayer[filterLayer.size()]);
	}

        /**
         * Retrieve the children of this collection that are vector layers
         * @return
         * @throws DriverException
         */
	public ILayer[] getVectorLayers() throws DriverException {
		ILayer[] allLayers = getLayersRecursively();

		ArrayList<ILayer> filterLayer = new ArrayList<ILayer>();

		for (int i = 0; i < allLayers.length; i++) {
			if (allLayers[i].isVectorial()) {
				filterLayer.add(allLayers[i]);

			}
		}

		return filterLayer.toArray(new ILayer[filterLayer.size()]);
	}

        /**
         * Used to determine if this layer is a raster layer. It is not, it is a layer collection.
         * @return false
         */
	public boolean isRaster() {
		return false;
	}

        /**
         * Used to determine if this layer is a vector layer. It is not, it is a layer collection.
         * @return false
         */
	public boolean isVectorial() {
		return false;
	}

        /**
         * Supposed to return the datasource associated to this layer. But it's a collection,
         * so it is null. 
         * @return
         */
	public SpatialDataSourceDecorator getDataSource() {
		return null;
	}

        //////////////////Unsupported methods////////////////////////

	public RasterLegend[] getRasterLegend() throws DriverException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public RasterLegend[] getRasterLegend(String fieldName)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public Legend[] getVectorLegend() throws DriverException,
			UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public Legend[] getVectorLegend(String fieldName)
			throws IllegalArgumentException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public void setLegend(String fieldName, Legend... legends)
			throws DriverException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public void setLegend(Legend... l) throws DriverException {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}

	public GeoRaster getRaster() throws DriverException {
		throw new UnsupportedOperationException("Cannot do this "
				+ "operation on a layer collection");
	}

	public int[] getSelection() {
		return new int[0];
	}

	public void setSelection(int[] newSelection) {
	}

	public Legend[] getRenderingLegend() throws DriverException {
		throw new UnsupportedOperationException(
				"Cannot draw a layer collection");
	}

	@Override
	public WMSConnection getWMSConnection()
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Cannot do this "
				+ "operation on a layer collection");
	}

	@Override
	public boolean isWMS() {
		return false;
	}

	@Override
	public WMSLegend getWMSLegend() {
		throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
	}
    
    @Override
    public FeatureTypeStyle getFeatureTypeStyle(){
        throw new UnsupportedOperationException("Cannot set "
				+ "a legend on a layer collection");
    }
        
        ///////////Static methods///////////////////////////////

        /**
         * Aooky action to each leave of this layer tree
         * @param root
         * @param action
         */
        public static void processLayersLeaves(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			ILayer lc = (ILayer) root;
			ILayer[] layers = lc.getChildren();
			for (ILayer layer : layers) {
				processLayersLeaves(layer, action);
			}
		} else {
			action.action(root);
		}
	}

        /**
         * Apply action to each node of this tree of layers.
         * @param root
         * @param action
         */
	public static void processLayersNodes(ILayer root, ILayerAction action) {
		if (root instanceof LayerCollection) {
			ILayer lc = (ILayer) root;
			ILayer[] layers = lc.getChildren();
			for (ILayer layer : layers) {
				processLayersNodes(layer, action);
			}
		}
		action.action(root);
	}


        /**
         * Count the number of leaves in this tree of layers.
         * @param root
         * @return
         */
        public static int getNumberOfLeaves(final ILayer root) {
		CountLeavesAction ila = new CountLeavesAction();
		LayerCollection.processLayersLeaves(root, ila);
		return ila.getNumberOfLeaves();
	}
        ///////////Private methods//////////////////////////
        
        /*
         * This method will guarantee that layer, and all its potential inner
         * layers, will have names that are not already owned by another, declared, layer.
         */
	private void setNamesRecursively(final ILayer layer,
			final Set<String> allLayersNames) throws LayerException {
		layer.setName(provideNewLayerName(layer.getName(), allLayersNames));
		if (layer instanceof LayerCollection) {
			LayerCollection lc = (LayerCollection) layer;
			if (null != lc.getLayerCollection()) {
				for (ILayer layerItem : lc.getChildren()) {
					setNamesRecursively(layerItem, allLayersNames);
				}
			}
		}
	}

        /*
         * Check that name is not already contained in allLayersNames.
         * If it is in, a new String is created and returned, with the form name_i
         * where i is as small as possible.
         */
	private String provideNewLayerName(final String name,
			final Set<String> allLayersNames) {
		String tmpName = name;
		if (allLayersNames.contains(tmpName)) {
			int i = 1;
			while (allLayersNames.contains(tmpName + "_" + i)) {
				i++;
			}
			tmpName += "_" + i;
		}
		allLayersNames.add(tmpName);
		return tmpName;
	}
        
        //////////Private classes//////////////////////////

	private class GetEnvelopeLayerAction implements ILayerAction {
		private Envelope globalEnvelope;

		public void action(ILayer layer) {
			if (null == globalEnvelope) {
				globalEnvelope = new Envelope(layer.getEnvelope());
			} else {
				globalEnvelope.expandToInclude(layer.getEnvelope());
			}
		}

		public Envelope getGlobalEnvelope() {
			return globalEnvelope;
		}
	}

	private static class CountLeavesAction implements ILayerAction {
		private int numberOfLeaves = 0;

		public void action(ILayer layer) {
			numberOfLeaves++;
		}

		public int getNumberOfLeaves() {
			return numberOfLeaves;
		}
	}
}