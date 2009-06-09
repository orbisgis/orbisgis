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
package org.orbisgis.sif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.orbisgis.images.IconLoader;

public class ControlPanel extends JPanel {
	private JList list;
	private JButton btnSave;
	private JLabel collapsed;
	private JButton btnDelete;
	private JTextField txtNew;
	private PersistentPanelDecorator sqlPanel;
	private JButton btnLoad;
	private JPanel east;

	public ControlPanel(SQLUIPanel panel) throws DriverException,
			DataSourceCreationException {
		this.sqlPanel = new PersistentPanelDecorator(panel);
		this.setLayout(new BorderLayout());
		list = new JList(sqlPanel.getContents());
		list.setVisible(false);
		list.setBorder(BorderFactory.createLoweredBevelBorder());
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateButtons();
			}

		});
		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					sqlPanel.loadEntry(list.getSelectedIndex());
					sqlPanel.validateInput();
				}
			}

		});
		this.add(list, BorderLayout.CENTER);
		txtNew = new JTextField(8);
		txtNew.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				updateButtons();
			}

		});
		btnSave = new JButton();
		btnSave.setMargin(new Insets(0, 0, 0, 0));
		btnSave.setVisible(false);
		btnSave.setIcon(IconLoader.getIcon("save.png"));
		btnSave.setToolTipText("Clic here to save a favorite");

		btnSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sqlPanel.saveInput(txtNew.getText());
				list.setListData(sqlPanel.getContents());
			}

		});
		JPanel south = new JPanel();
		south.add(btnSave);
		south.add(txtNew);
		this.add(south, BorderLayout.SOUTH);
		btnDelete = new JButton();
		btnDelete.setIcon(IconLoader.getIcon("cancel.png"));
		btnDelete.setToolTipText("Clic here to delete a favorite");
		btnDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sqlPanel.removeInput(list.getSelectedIndex());
				list.setListData(sqlPanel.getContents());
			}

		});
		btnLoad = new JButton();
		btnLoad.setIcon(IconLoader.getIcon("folder_user.png"));
		btnLoad.setToolTipText("Clic here to load a favorite");
		btnLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sqlPanel.loadEntry(list.getSelectedIndex());
				sqlPanel.validateInput();
			}

		});
		east = new JPanel();
		east.setLayout(new CRFlowLayout());
		east.add(btnDelete);
		east.add(new CarriageReturn());
		east.add(btnLoad);
		this.add(east, BorderLayout.EAST);

		this.setBackground(Color.white);
		this.setMinimumSize(new Dimension(100, 40));

		collapsed = new JLabel(getVertical("FAVORITES"));
		collapsed.setHorizontalAlignment(JLabel.CENTER);
		this.add(collapsed, BorderLayout.WEST);
		listen(this);
		this.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		collapse();
		updateButtons();
	}

	private void updateButtons() {
		boolean somethingSelected = list.getSelectedIndex() != -1;
		btnDelete.setEnabled(somethingSelected);
		btnLoad.setEnabled(somethingSelected);

		if (txtNew.getText().length() > 0) {
			btnSave.setEnabled(true);
		} else {
			btnSave.setEnabled(false);
		}
	}

	private String getVertical(String string) {
		String ret = "<html>";
		for (int i = 0; i < string.length(); i++) {
			ret += string.charAt(i) + "<br/>";
		}

		return ret + "</html>";
	}

	private void collapse() {
		ControlPanel.this.setPreferredSize(new Dimension(20, 0));
		btnSave.setVisible(false);
		list.setVisible(false);
		btnDelete.setVisible(false);
		btnLoad.setVisible(false);
		east.setVisible(false);
		txtNew.setVisible(false);
		collapsed.setVisible(true);
		this.setBackground(btnSave.getBackground());
	}

	private void expand() {
		ControlPanel.this.setPreferredSize(null);
		btnSave.setVisible(true);
		list.setVisible(true);
		btnDelete.setVisible(true);
		btnLoad.setVisible(true);
		east.setVisible(true);
		txtNew.setVisible(true);
		collapsed.setVisible(false);
		this.setBackground(btnSave.getBackground());
	}

	private void listen(Component comp) {
		comp.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseExited(MouseEvent e) {
				if ((ControlPanel.this.getMousePosition() == null)
						&& !exitInLeftEdge(e)) {
					collapse();
				}
			}

			private boolean exitInLeftEdge(MouseEvent e) {
				int x = e.getLocationOnScreen().x;
				int controlX = ControlPanel.this.getLocationOnScreen().x;
				if (Math.abs(x - controlX) < 10) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (!list.isVisible()) {
					expand();
				}
			}

		});

		if (comp instanceof Container) {
			Component[] children = ((Container) comp).getComponents();
			for (Component component : children) {
				listen(component);
			}
		}
	}
}
