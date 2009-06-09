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
package org.orbisgis.core.ui.components.sif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.orbisgis.sif.AbstractUIPanel;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;

public class ChoosePanel extends AbstractUIPanel {

	private String[] names;
	private String title;
	private JList lst;
	private DefaultListModel model;
	private Object[] ids;
	private boolean multiple = false;
	private JPanel pnlButtons;
	private JPanel pane;

	public ChoosePanel(String title, String[] names, Object[] ids) {
		this.title = title;
		this.names = names;
		this.ids = ids;
		initComponent();
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
		pnlButtons.setVisible(multiple);
		if (multiple) {
			lst
					.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		} else {
			lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
	}

	public Component getComponent() {
		return pane;
	}

	private void initComponent() {
		pane = new JPanel();
		pane.setLayout(new BorderLayout());
		lst = new JList();
		model = new DefaultListModel();
		for (int i = 0; i < names.length; i++) {
			model.addElement(names[i]);
		}
		lst.setModel(model);
		pane.add(new JScrollPane(lst), BorderLayout.CENTER);
		pnlButtons = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.LEFT);
		pnlButtons.setLayout(flowLayout);
		JButton btnAll = new JButton("Select All");
		btnAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lst.getSelectionModel().setSelectionInterval(0,
						lst.getModel().getSize() - 1);
			}

		});
		JButton btnNone = new JButton("Select None");
		btnNone.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lst.clearSelection();
			}

		});
		pnlButtons.add(btnAll);
		pnlButtons.add(new CarriageReturn());
		pnlButtons.add(btnNone);
		pnlButtons.setVisible(multiple);
		pane.add(pnlButtons, BorderLayout.EAST);
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

	public Object getSelected() {
		return ids[lst.getSelectedIndex()];
	}

	public int getSelectedIndex() {
		return lst.getSelectedIndex();
	}

	public Object[] getSelectedElements() {
		ArrayList<Object> ret = new ArrayList<Object>();
		int[] indexes = lst.getSelectedIndices();
		for (int index : indexes) {
			ret.add(ids[index]);
		}

		return ret.toArray();
	}

}
