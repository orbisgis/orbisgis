package org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

public class SRSListModel extends AbstractListModel implements ListModel {

	private String[] srsNames;
	private String nameFilter;
	private String[] srsNamesIn;

	public SRSListModel(String[] srsNamesIn) {
		this.srsNamesIn = srsNamesIn;
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
		srsNames = srsNamesIn;
		if (nameFilter != null) {

			ArrayList<String> names = new ArrayList<String>();

			for (String srsName : srsNames) {
				if (srsName.contains(nameFilter)) {
					names.add(srsName);
				}
			}
			this.srsNames = names.toArray(new String[names.size()]);

		}

		fireIntervalRemoved(this, 0, getSize());
		fireIntervalAdded(this, 0, getSize());

	}
	
}
