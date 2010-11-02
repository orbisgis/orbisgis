/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Lead Erwan BOCHER, scientific researcher,
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer.
 *
 *  User support lead : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.geomark;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;

import com.vividsolutions.jts.geom.Envelope;

public class GeomarkPanel extends JPanel implements ListSelectionListener {

	// Cannot add more than 50 geomarks otherwise use a layer and the "zoom to"
	// feature option.
	int geomarkLimits = 50;
	private JList list;

	GeomarkListModel listModel;

	private static final String addString = "Add";

	private static final String removeString = "Remove";

	private JButton deleteButton;

	private JTextField geomarkName;

	private MapEditorPlugIn editor;

	// A folder in the workspace to store geomarks
	public String GEOMARK_FOLDER = "geomarks";

	private String geomarkFolderPath;

	private long idTime = -1;

	boolean isGeomarkListModified = false;

	public GeomarkPanel() {
		super(new BorderLayout());

		DefaultWorkspace workspace = (DefaultWorkspace) Services
				.getService(Workspace.class);

		geomarkFolderPath = workspace.getWorkspaceFolder() + File.separator
				+ GEOMARK_FOLDER;

		File folder = new File(geomarkFolderPath);
		if (!folder.exists()) {
			folder.mkdir();
		}

		listModel = new GeomarkListModel();
		// Create the list and put it in a scroll pane.
		list = new JList();
		list.setModel(listModel);
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
								listModel.getValue(geomarkLabel));
					}
				}
			}
		});
		list.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(list);

		JButton addButton = new JButton();
		addButton.setIcon(OrbisGISIcon.WORLD_ADD);
		addButton.setToolTipText("Press the button to add a geomark!");
		HireListener hireListener = new HireListener(addButton);
		addButton.setActionCommand(addString);
		addButton.addActionListener(hireListener);
		addButton.setEnabled(false);

		deleteButton = new JButton();
		deleteButton.setIcon(OrbisGISIcon.WORLD_DEL);
		deleteButton.setToolTipText("Press the button to delete a geomark!");
		deleteButton.setActionCommand(removeString);
		deleteButton.addActionListener(new FireListener());

		if (list.getModel().getSize() > 0) {
			deleteButton.setEnabled(true);
		} else {
			deleteButton.setEnabled(false);
		}

		geomarkName = new JTextField(10);
		geomarkName.addActionListener(hireListener);
		geomarkName.getDocument().addDocumentListener(hireListener);

		// Create a panel that uses BoxLayout.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.add(deleteButton);
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(Box.createHorizontalStrut(5));
		buttonPane.add(geomarkName);
		buttonPane.add(addButton);
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
				isGeomarkListModified = true;
			}
			repaint();
			int size = listModel.getSize();

			if (size == 0) { // Nobody's left, disable firing.
				deleteButton.setEnabled(false);
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

			MapContext vc = ((MapContextManager) Services
					.getService(MapContextManager.class)).getActiveMapContext();
			if (vc != null) {
				listModel.addElement(geomarkName.getText(), editor
						.getMapTransform().getAdjustedExtent());
				isGeomarkListModified = true;
				// Reset the text field.
				geomarkName.requestFocusInWindow();
				geomarkName.setText("");
			}
			repaint();

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
			if ((listModel.getSize() <= geomarkLimits) && (!alreadyEnabled)) {
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
				deleteButton.setEnabled(false);

			} else {
				// Selection, enable the fire button.
				deleteButton.setEnabled(true);
			}
		}
	}

	public void add(final String key, final Envelope envelope) {
		listModel.addElement(key, envelope);
	}

	public void setEditor(IEditor editor) {
		if (isGeomarkListModified) {
			saveGeoMarkFile();
		}
		isGeomarkListModified = false;
		this.editor = (MapEditorPlugIn) editor;
		updateGeoMarkFile();
	}

	public String getGeomarkFolderPath() {
		return geomarkFolderPath;
	}

	public void updateGeoMarkFile() {
		MapContext vc = ((MapContextManager) Services
				.getService(MapContextManager.class)).getActiveMapContext();
		if (vc != null) {
			if (idTime == -1) {
				idTime = vc.getIdTime();
			} else if (idTime != vc.getIdTime()) {
				idTime = vc.getIdTime();
			}
			readGeomarkFile();
		}
	}

	private void readGeomarkFile() {
		File geoMarkFile = new File(getGeomarkFolderPath() + File.separator
				+ idTime);
		if (geoMarkFile != null) {
			if (geoMarkFile.exists()) {
				try {
					FileInputStream fis = new FileInputStream(geoMarkFile);
					ObjectInputStream ois = new ObjectInputStream(fis);
					listModel.refresh((ArrayList<Geomark>) ois.readObject());
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			} else {
				listModel.refresh(new ArrayList<Geomark>());
			}
		}

	}

	public boolean isGeomarkListModified() {
		return isGeomarkListModified;
	}

	public void saveGeoMarkFile() {
		listModel.save(getGeomarkFolderPath() + File.separator + idTime);
	}

}