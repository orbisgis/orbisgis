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
package org.orbisgis.editorViews.geomark;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.Services;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editors.map.MapEditor;
import org.orbisgis.editors.map.MapContextManager;
import org.orbisgis.images.IconLoader;
import org.orbisgis.layerModel.MapContext;

import com.vividsolutions.jts.geom.Envelope;

public class GeomarkPanel extends JPanel implements ListSelectionListener {
	private Map<String, Envelope> geomarksMap = new HashMap<String, Envelope>();

	private JList list;

	private DefaultListModel listModel;

	private static final String addString = "Add";

	private static final String removeString = "Remove";

	private JButton fireButton;

	private JTextField geomarkName;

	private MapEditor editor;

	public GeomarkPanel() {
		super(new BorderLayout());

		listModel = new DefaultListModel();
		// Create the list and put it in a scroll pane.
		list = new JList(listModel);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (2 == e.getClickCount()) {
					final String geomarkLabel = listModel.getElementAt(
							list.locationToIndex(e.getPoint())).toString();
					MapContext vc = ((MapContextManager) Services
							.getService(MapContextManager.class))
							.getActiveMapContext();
					if (vc != null) {
						editor.getMapTransform().setExtent(
								geomarksMap.get(geomarkLabel));
					}
				}
			}
		});
		list.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(list);

		JButton hireButton = new JButton();
		hireButton.setIcon(IconLoader.getIcon("world_add.png"));
		hireButton.setToolTipText("Press the button to add a geomark!");
		HireListener hireListener = new HireListener(hireButton);
		hireButton.setActionCommand(addString);
		hireButton.addActionListener(hireListener);
		hireButton.setEnabled(false);

		fireButton = new JButton();
		fireButton.setIcon(IconLoader.getIcon("world_delete.png"));
		fireButton.setToolTipText("Press the button to delete a geomark!");
		fireButton.setActionCommand(removeString);
		fireButton.addActionListener(new FireListener());

		if (list.getModel().getSize() > 0) {
			fireButton.setEnabled(true);
		} else {
			fireButton.setEnabled(false);
		}

		geomarkName = new JTextField(10);
		geomarkName.addActionListener(hireListener);
		geomarkName.getDocument().addDocumentListener(hireListener);

		// Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(fireButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(geomarkName);
		buttonPane.add(hireButton);
		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(listScrollPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);
	}

	class FireListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// This method can be called only if
			// there's a valid selection
			// so go ahead and remove whatever's selected.
			Object[] selectedValues = list.getSelectedValues();
			for (Object selectedValue : selectedValues) {
				listModel.removeElement(selectedValue);
			}

			int size = listModel.getSize();

			if (size == 0) { // Nobody's left, disable firing.
				fireButton.setEnabled(false);
			}
		}
	}

	// This listener is shared by the text field and the hire button.
	class HireListener implements ActionListener, DocumentListener {
		private boolean alreadyEnabled = false;

		private JButton button;

		public HireListener(JButton button) {
			this.button = button;
		}

		// Required by ActionListener.
		public void actionPerformed(ActionEvent e) {
			String name = geomarkName.getText();

			// User didn't type in a unique name...
			if (name.equals("") || alreadyInList(name)) {
				Toolkit.getDefaultToolkit().beep();
				geomarkName.requestFocusInWindow();
				geomarkName.selectAll();
				return;
			}

			int index = list.getSelectedIndex(); // get selected index
			if (index == -1) { // no selection, so insert at beginning
				index = 0;
			} else { // add after the selected item
				index++;
			}

			listModel.insertElementAt(geomarkName.getText(), index);
			MapContext vc = ((MapContextManager) Services
					.getService(MapContextManager.class))
					.getActiveMapContext();
			if (vc != null) {
				geomarksMap.put(geomarkName.getText(), editor.getMapTransform()
						.getAdjustedExtent());
				// Reset the text field.
				geomarkName.requestFocusInWindow();
				geomarkName.setText("");
			}

			// Select the new item and make it visible.
			list.setSelectedIndex(index);
			list.ensureIndexIsVisible(index);
		}

		// This method tests for string equality. You could certainly
		// get more sophisticated about the algorithm. For example,
		// you might want to ignore white space and capitalization.
		protected boolean alreadyInList(String name) {
			return listModel.contains(name);
		}

		// Required by DocumentListener.
		public void insertUpdate(DocumentEvent e) {
			enableButton();
		}

		// Required by DocumentListener.
		public void removeUpdate(DocumentEvent e) {
			handleEmptyTextField(e);
		}

		// Required by DocumentListener.
		public void changedUpdate(DocumentEvent e) {
			if (!handleEmptyTextField(e)) {
				enableButton();
			}
		}

		private void enableButton() {
			if (!alreadyEnabled) {
				button.setEnabled(true);
			}
		}

		private boolean handleEmptyTextField(DocumentEvent e) {
			if (e.getDocument().getLength() <= 0) {
				button.setEnabled(false);
				alreadyEnabled = false;
				return true;
			}
			return false;
		}
	}

	// This method is required by ListSelectionListener.
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {

			if (list.getSelectedIndex() == -1) {
				// No selection, disable fire button.
				fireButton.setEnabled(false);

			} else {
				// Selection, enable the fire button.
				fireButton.setEnabled(true);
			}
		}
	}

	public void add(final String key, final Envelope envelope) {
		listModel.addElement(key);
		geomarksMap.put(key, envelope);
	}

	public void setEditor(IEditor editor) {
		this.editor = (MapEditor) editor;
	}
}