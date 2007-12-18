package org.orbisgis.geoview.views.geomark;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.geoview.GeoView2D;

public class GeomarkPanel extends JPanel implements ListSelectionListener {
	private Map<String, Rectangle2D> geomarksMap = new HashMap<String, Rectangle2D>();

	private JList list;

	private DefaultListModel listModel;

	private static final String addString = "Add";

	private static final String removeString = "Remove";

	private JButton fireButton;

	private JTextField geomarkName;

	private GeoView2D geoview;

	public GeomarkPanel(final GeoView2D geoview) {
		super(new BorderLayout());
		this.geoview = geoview;

		listModel = new DefaultListModel();
		// Create the list and put it in a scroll pane.
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.addListSelectionListener(this);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (2 == e.getClickCount()) {
					final String geomarkLabel = listModel.getElementAt(
							list.locationToIndex(e.getPoint())).toString();
					geoview.getMap().setExtent(geomarksMap.get(geomarkLabel));
				}
			}
		});
		list.setVisibleRowCount(5);
		JScrollPane listScrollPane = new JScrollPane(list);

		JButton hireButton = new JButton(addString);
		hireButton.setIcon(new ImageIcon(getClass().getResource("world_add.png")));
		hireButton.setToolTipText("Press the button to add a geomark!");
		HireListener hireListener = new HireListener(hireButton);
		hireButton.setActionCommand(addString);
		hireButton.addActionListener(hireListener);
		hireButton.setEnabled(false);

		fireButton = new JButton(removeString);
		fireButton.setIcon(new ImageIcon(getClass().getResource("world_delete.png")));
		fireButton.setToolTipText("Press the button to delete a geomark!");
		fireButton.setActionCommand(removeString);
		fireButton.addActionListener(new FireListener());
		
		if (list.getModel().getSize()>0){
			fireButton.setEnabled(true);
		}
		else {
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
			int index = list.getSelectedIndex();
			listModel.remove(index);

			int size = listModel.getSize();

			if (size == 0) { // Nobody's left, disable firing.
				fireButton.setEnabled(false);
			} else { // Select an index.
				if (index == listModel.getSize()) {
					// removed item in last position
					index--;
				}

				list.setSelectedIndex(index);
				list.ensureIndexIsVisible(index);
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
			geomarksMap.put(geomarkName.getText(), geoview.getMap()
					.getAdjustedExtent());
			// If we just wanted to add to the end, we'd do this:
			// listModel.addElement(employeeName.getText());

			// Reset the text field.
			geomarkName.requestFocusInWindow();
			geomarkName.setText("");

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
}