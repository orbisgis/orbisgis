/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.layerModel;

import java.awt.image.BufferedImage;
import java.io.File;

import org.orbisgis.IProgressMonitor;
import org.orbisgis.PersistenceException;

import com.vividsolutions.jts.geom.Envelope;

/**
 * This interface provides information to the tool system and receives
 * notifications from it. Also registers the tool system as a listener in order
 * to notify it about certain events during edition
 */
public interface MapContext {

	/**
	 * Gets the root layer of the layer collection in this edition context
	 *
	 * @return
	 */
	ILayer getLayerModel();

	public ILayer[] getLayers();

	public ILayer[] getSelectedLayers();

	public void addMapContextListener(MapContextListener listener);

	public void removeMapContextListener(MapContextListener listener);

	public void setSelectedLayers(ILayer[] selectedLayers);

	void saveStatus(File file) throws PersistenceException;

	void loadStatus(File file) throws PersistenceException;

	/**
	 * Draws an image of the layers in the specified image.
	 *
	 * @param inProcessImage
	 *            Image where the drawing will take place
	 * @param extent
	 *            Extent of the data to take into account. It must have the same
	 *            proportions than the image
	 * @param pm
	 *            Object to report process and check the cancelation condition
	 */
	void draw(BufferedImage inProcessImage, Envelope extent, IProgressMonitor pm);

	/**
	 * Gets the layer where all the edition actions take place
	 *
	 * @return
	 */
	ILayer getActiveLayer();

	/**
	 * Sets the layer where all the edition actions take place
	 *
	 * @return
	 */
	void setActiveLayer(ILayer activeLayer);
}
