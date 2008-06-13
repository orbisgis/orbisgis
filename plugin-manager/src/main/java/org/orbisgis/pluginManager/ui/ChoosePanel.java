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
package org.orbisgis.pluginManager.ui;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.sif.AbstractUIPanel;

public class ChoosePanel extends AbstractUIPanel {

	private String[] names;
	private String title;
	private JList lst;
	private DefaultListModel model;
	private String[] ids;

	public ChoosePanel(String title, String[] names, String[] ids) {
		this.title = title;
		this.names = names;
		this.ids = ids;
	}

	public Component getComponent() {
		lst = new JList();
		model = new DefaultListModel();
		for (int i = 0; i < names.length; i++) {
			model.addElement(names[i]);
		}
		lst.setModel(model);
		return lst;
	}

	public String getTitle() {
		return title;
	}

	public String validateInput() {
		if (lst.getSelectedIndex() == -1) {
			return "An item must be selected";
		}

		return null;
	}

	public String getSelected() {
		return ids[lst.getSelectedIndex()];
	}

	public int getSelectedIndex() {
		return lst.getSelectedIndex();
	}

}
