/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.cluster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;

public class Cluster {
	private List<Long> listOfDataPointsIndex = null;
	private int dimension;
	private DataSet inDs;
	private int cellIndexFieldId;

        private static final Logger LOG = Logger.getLogger(Cluster.class);

	public Cluster(final int dimension, final DataSet inDs,
			final int cellIndexFieldId) {
            LOG.trace("Constructor");
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
            LOG.trace("Getting centroid");
		final DataPoint centroid = new DataPoint(dimension);
		for (long dataPointIndex : listOfDataPointsIndex) {
                        Value[] v = new Value[inDs.getMetadata().getFieldCount()];
                        for (int i = 0; i < v.length; i++) {
                                v[i] = inDs.getFieldValue(dataPointIndex, i);
                        }
			centroid.addDataPoint(new DataPoint(v,
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