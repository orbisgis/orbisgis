/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.components.sif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.function.FunctionManager;
import org.orbisgis.core.sif.AbstractUIPanel;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.ui.components.text.JTextFilter;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.utils.I18N;

public class FunctionPanel extends AbstractUIPanel {

	private String[] names;
	private String title;
	private JList lst;
	private Object[] ids;
	private boolean multiple = false;
	private JPanel pnlButtons;
	private JPanel pane;
	private FunctionPanelFilter functionPanelFilter;
	private JPanel searchPanel;
	private JTextFilter txtFilter;
	private JLabel functionLabelCount;
	private static int functionsCount;
	static {
		functionsCount = FunctionManager.getFunctionNames().length
				+ QueryManager.getQueryNames().length;
	}

	public FunctionPanel(String title, String[] names, Object[] ids) {
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

		pane.add(getSearchSRSPanel(), BorderLayout.NORTH);
		lst = getJListFunction();
		pane.add(new JScrollPane(lst), BorderLayout.CENTER);
		pnlButtons = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.LEFT);
		pnlButtons.setLayout(flowLayout);
		JButton btnAll = new JButton(I18N.getText(Names.SELECT_ALL));
		btnAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lst.getSelectionModel().setSelectionInterval(0,
						lst.getModel().getSize() - 1);
			}

		});
		JButton btnNone = new JButton(I18N.getText(Names.SELECT_NONE));
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
		functionLabelCount = new JLabel(I18N
				.getText(Names.FUNCTION_PANEL_NUMBER + " : " + names.length)
				+ " on " + functionsCount + " functions.");
		pane.add(functionLabelCount, BorderLayout.SOUTH);

	}

	public int getNbAvailableFunctions() {
		return functionPanelFilter.ids.length;
	}

	public JList getJListFunction() {
		if (null == lst) {
			lst = new JList();
			functionPanelFilter = new FunctionPanelFilter(names, ids);
			lst.setModel(functionPanelFilter);
		}
		return lst;
	}

	public JPanel getSearchSRSPanel() {

		if (null == searchPanel) {
			searchPanel = new JPanel();
			JLabel label = new JLabel(I18N.getText(Names.SEARCH) + " : ");

			txtFilter = new JTextFilter();
			txtFilter.addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					doFilter();
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					doFilter();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					doFilter();
				}
			});
			searchPanel.add(label);
			searchPanel.add(txtFilter);
		}
		return searchPanel;

	}

	private void doFilter() {
		functionPanelFilter.filter(txtFilter.getText());
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
		return functionPanelFilter.ids[lst.getSelectedIndex()];
	}

	public int getSelectedIndex() {
		return lst.getSelectedIndex();
	}

	/**
	 * Get the selected function in the function panel
	 * 
	 * @return
	 */
	public Object[] getSelectedElements() {
		ArrayList<Object> ret = new ArrayList<Object>();
		int[] indexes = lst.getSelectedIndices();
		for (int index : indexes) {
			ret.add(functionPanelFilter.ids[index]);
		}

		return ret.toArray();
	}

}
