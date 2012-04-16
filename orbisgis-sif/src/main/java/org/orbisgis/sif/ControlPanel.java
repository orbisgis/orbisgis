/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.sif;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gdms.data.DataSourceCreationException;
import org.gdms.driver.DriverException;
import org.orbisgis.sif.translation.I18N;
import org.orbisgis.sif.icons.SifIcon;

public class ControlPanel extends JPanel {
	private JList list;
	private JButton btnSave;
	private JLabel collapsed;
	private JButton btnDelete;
	private JTextField txtNew;
	private PersistentPanelDecorator sqlPanel;
	private JButton btnLoad;
	private JToolBar east;

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
		btnSave.setIcon(SifIcon.getIcon("disk"));
		btnSave.setToolTipText(I18N
				.tr("sif.ControlPanel.SaveFavorite"));

		btnSave.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sqlPanel.saveInput(txtNew.getText());
				list.setListData(sqlPanel.getContents());
			}

		});
		btnSave.setBorderPainted(false);
		JPanel south = new JPanel();
		south.add(btnSave);
		south.add(txtNew);
		this.add(south, BorderLayout.SOUTH);
		btnDelete = new JButton();
		btnDelete.setIcon(SifIcon.getIcon("cancel"));
		btnDelete.setToolTipText(I18N
				.tr("sif.ControlPanel.DeleteFavorite"));
		btnDelete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sqlPanel.removeInput(list.getSelectedIndex());
				list.setListData(sqlPanel.getContents());
			}

		});
		btnDelete.setBorderPainted(false);

		btnLoad = new JButton();
		btnLoad.setIcon(SifIcon.getIcon("folder_user"));
		btnLoad.setToolTipText(I18N
				.tr("sif.ControlPanel.LoadFavorite"));
		btnLoad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				sqlPanel.loadEntry(list.getSelectedIndex());
				sqlPanel.validateInput();
			}

		});
		btnLoad.setBorderPainted(false);

		JButton btnCollapse = new JButton();
		btnCollapse.setIcon(SifIcon.getIcon("go-previous"));
		btnCollapse.setToolTipText(I18N
				.tr("sif.ControlPanel.CollapseFavorites"));
		btnCollapse.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				collapse();
			}

		});
		btnCollapse.setBorderPainted(false);
		east = new JToolBar();
		east.setFloatable(false);
		east.add(btnCollapse);
		east.add(btnDelete);
		east.add(btnLoad);		
		east.setOpaque(false);
		this.add(east, BorderLayout.NORTH);
		this.setOpaque(false);

		this.setBackground(Color.white);
		this.setMinimumSize(new Dimension(100, 40));

		collapsed = new JLabel(getVertical(I18N
				.tr("sif.ControlPanel.Favorites")),
                        SifIcon.getIcon("go-next"), JLabel.CENTER);
		collapsed.setIconTextGap(20);
		collapsed.setVerticalTextPosition(JLabel.BOTTOM);
		collapsed.setHorizontalTextPosition(JLabel.CENTER);
		collapsed.setToolTipText(I18N
				.tr("sif.ControlPanel.ExpandFavorites"));

		this.add(collapsed, BorderLayout.WEST);
		collapsed.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (!list.isVisible()) {
					expand();
				}
			}
		});
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

}
