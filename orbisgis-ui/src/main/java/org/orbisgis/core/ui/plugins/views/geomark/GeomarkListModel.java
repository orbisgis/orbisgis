/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.geomark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import com.vividsolutions.jts.geom.Envelope;

public class GeomarkListModel extends AbstractListModel implements ListModel {

	private ArrayList<Geomark> geomarksMap;

	public GeomarkListModel() {
		this.geomarksMap = new ArrayList<Geomark>();		
	}

	@Override
	public Object getElementAt(int index) {
		return geomarksMap.get(index).getName();
	}

	@Override
	public int getSize() {
		return geomarksMap.size();
	}

	public void refresh(ArrayList<Geomark> geomarksMap) {
		this.geomarksMap = geomarksMap;
		fireContentsChanged(this, 0, getSize());
	}

	public void removeElement(Object selectedValue) {
		if (getSize() == 1) {
			geomarksMap.clear();
		} else {
			boolean removed = false;
			// To allow concurent modification
			List<Geomark> copy = new ArrayList(geomarksMap);
			for (Iterator it = copy.iterator(); it.hasNext();) {
				Geomark geomark = (Geomark) it.next();
				if (geomark.getName().equals(selectedValue)) {
					removed = geomarksMap.remove(geomark);
				}
			}
			if (removed) {
				fireContentsChanged(this, 0, getSize());
				copy = null;
			}
		}
	}

	public boolean contains(String name) {
		for (Geomark geomark : geomarksMap) {
			if (geomark.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void addElement(String key, Envelope envelope) {
		if (geomarksMap.add(new Geomark(key, envelope))) {
			fireContentsChanged(this, 0, getSize());
		}
	}

	public void save(String path) {
		File geoMarkFile = new File(path);
		if (geoMarkFile.exists()) {
			geoMarkFile.delete();
		}
		if (!geomarksMap.isEmpty()) {
			try {
				FileOutputStream fos = new FileOutputStream(geoMarkFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(geomarksMap);
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public Envelope getValue(String geomarkLabel) {

		for (Geomark geomark : geomarksMap) {
			if (geomark.getName().equals(geomarkLabel)) {
				return geomark.getEnvelope();
			}
		}
		throw new RuntimeException("Cannot find the geomark name "
				+ geomarkLabel);
	}
}
