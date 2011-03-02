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
