package org.orbisgis.core.ui.geocatalog.newSourceWizards.wms;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

public class SRSListModel extends AbstractListModel implements ListModel {

	private String[] srsNames;
	private String nameFilter;
	private Vector allSrs;

	public SRSListModel(Vector allSrs) {
		this.allSrs = allSrs;
		refresh();
	}

	@Override
	public Object getElementAt(int index) {
		return srsNames[index];
	}

	@Override
	public int getSize() {
		return srsNames.length;
	}

	public void filter(String text) {
		if (text.trim().length() == 0) {
			text = null;
		}
		this.nameFilter = text;
		refresh();
	}

	private void refresh() {
		srsNames = new String[allSrs.size()];
		allSrs.toArray(srsNames);
		if (nameFilter != null) {

			ArrayList<String> names = new ArrayList<String>();

			for (String srsName : srsNames) {
				if (srsName.contains(nameFilter)) {
					names.add(srsName);
				}
			}
			this.srsNames = names.toArray(new String[0]);
		}

		fireIntervalRemoved(this, 0, getSize());
		fireIntervalAdded(this, 0, getSize());
	}
}
