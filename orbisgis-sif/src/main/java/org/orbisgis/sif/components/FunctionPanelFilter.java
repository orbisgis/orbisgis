/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE 
 * team located in University of South Brittany, Vannes.
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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
package org.orbisgis.sif.components;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

public class FunctionPanelFilter extends AbstractListModel implements ListModel {

	private String[] srsNames;
	private String nameFilter;
	private String[] srsNamesIn;
	private Object[] idsIn;
	public Object[] ids;

	public FunctionPanelFilter(String[] srsNamesIn, Object[] idsIn) {
		this.srsNamesIn = srsNamesIn;
		this.idsIn = idsIn;
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
		ids = idsIn;
		if (nameFilter != null) {

			ArrayList<String> names = new ArrayList<String>();
			ArrayList<Object> objs = new ArrayList<Object>();
			int i = 0;
			for (String srsName : srsNames) {
				if (srsName.toLowerCase().contains(nameFilter.toLowerCase())) {
					names.add(srsName);
					objs.add(idsIn[i]);
				}
				i++;
			}
			this.srsNames = names.toArray(new String[names.size()]);
			this.ids = objs.toArray(new Object[objs.size()]);

		}

		fireIntervalRemoved(this, 0, getSize());
		fireIntervalAdded(this, 0, getSize());

	}
}
