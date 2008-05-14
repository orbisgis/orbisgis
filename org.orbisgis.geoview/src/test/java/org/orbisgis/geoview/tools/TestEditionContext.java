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
/**
 *
 */
package org.orbisgis.geoview.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import org.orbisgis.IProgressMonitor;
import org.orbisgis.PersistenceException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.layerModel.MapContextListener;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class TestEditionContext implements MapContext {

	public ArrayList<Geometry> features = new ArrayList<Geometry>();

	public ArrayList<Integer> selected = new ArrayList<Integer>();

	public String geometryType;

	public TestEditionContext(String geometryType) {
		this.geometryType = geometryType;
	}

	public ILayer getLayerModel() {
		return null;
	}

	public ILayer[] getLayers() {
		return null;
	}

	public ILayer[] getSelectedLayers() {
		return new ILayer[0];
	}

	public void setSelectedLayers(ILayer[] selectedLayers) {
	}

	public void addMapContextListener(MapContextListener listener) {

	}

	public void removeMapContextListener(MapContextListener listener) {

	}

	public void saveStatus(File file) {

	}

	public void draw(BufferedImage inProcessImage, Envelope extent,
			IProgressMonitor pm) {
	}

	public ILayer getActiveLayer() {
		return null;
	}

	public void loadStatus(File file) throws PersistenceException {
	}

	public void setActiveLayer(ILayer activeLayer) {

	}
}