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
package org.geoalgorithm.urbsat.kmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class Cluster {
	private List<Long> listOfDataPointsIndex = null;
	private int dimension;
	private DataSource inDs;
	private int cellIndexFieldId;

	public Cluster(final int dimension, final DataSource inDs,
			final int cellIndexFieldId) {
		this.dimension = dimension;
		this.inDs = inDs;
		this.cellIndexFieldId = cellIndexFieldId;
		listOfDataPointsIndex = new ArrayList<Long>();
	}

	public List<Long> getListOfDataPointsIndex() {
		return listOfDataPointsIndex;
	}

	public int size() {
		return listOfDataPointsIndex.size();
	}

	public DataPoint getCentroid() throws DriverException {
		final DataPoint centroid = new DataPoint(dimension);
		for (long dataPointIndex : listOfDataPointsIndex) {
			final Value[] dataPointValues = inDs.getRow(dataPointIndex);
			centroid.addDataPoint(new DataPoint(dataPointValues,
					cellIndexFieldId));
		}
		return centroid.divideBy(size());
	}

	public void addDataPointIndex(final Long dataPointIndex) {
		listOfDataPointsIndex.add(dataPointIndex);
	}

	public boolean isDifferentFrom(final Cluster cluster) {
		if (size() != cluster.size()) {
			return true;
		}
		Collections.sort(listOfDataPointsIndex);
		Collections.sort(cluster.getListOfDataPointsIndex());
		for (int i = 0; i < size(); i++) {
			if (listOfDataPointsIndex.get(i) != cluster
					.getListOfDataPointsIndex().get(i)) {
				return true;
			}
		}
		return false;
	}
}