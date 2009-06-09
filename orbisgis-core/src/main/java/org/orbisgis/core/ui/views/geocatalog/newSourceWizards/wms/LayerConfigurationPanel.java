package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.wms;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.images.IconLoader;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIPanel;

public class LayerConfigurationPanel extends JPanel implements UIPanel {

	private static final String ALL_LEFT = "ALL_LEFT";
	private static final String CURRENT_LEFT = "CURRENT_LEFT";
	private static final String CURRENT_RIGHT = "CURRENT_RIGHT";
	private static final String DOWN = "DOWN";
	private static final String UP = "UP";
	private TreeModel optionTreeModel;
	private WMSConnectionPanel connectionPanel;
	private JButton btnCurrentRight;
	private JButton btnCurrentLeft;
	private JButton btnAllLeft;
	private JTree treeOption;
	private JList lstSelection;
	private ActionListener actionListener;
	private DefaultListModel listModel;
	private JButton btnUp;
	private JButton btnDown;

	public LayerConfigurationPanel(WMSConnectionPanel connectionPanel) {
		this.connectionPanel = connectionPanel;

		actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedListIndex = lstSelection.getSelectedIndex();
				if ((UP.equals(e.getActionCommand()))
						|| (DOWN.equals(e.getActionCommand()))) {
					int inc = 1;
					if (UP.equals(e.getActionCommand())) {
						inc = -1;
					}
					Object element = listModel.elementAt(selectedListIndex);
					listModel.removeElementAt(selectedListIndex);
					listModel.add(selectedListIndex + inc, element);
					lstSelection.setSelectedIndex(listModel.indexOf(element));
				} else if (CURRENT_RIGHT.equals(e.getActionCommand())) {
					TreePath[] selectedPaths = treeOption.getSelectionPaths();
					for (TreePath treePath : selectedPaths) {
						listModel.addElement(treePath.getLastPathComponent());
					}
				} else if (CURRENT_LEFT.equals(e.getActionCommand())) {
					listModel.remove(selectedListIndex);
				} else if (ALL_LEFT.equals(e.getActionCommand())) {
					listModel.clear();
				}
			}
		};
	}

	private Component getOrderButtons() {
		JPanel ret = new JPanel();
		ret.setLayout(new CRFlowLayout());
		btnUp = createButton("go-up.png", UP);
		btnDown = createButton("go-down.png", DOWN);
		ret.add(btnUp);
		ret.add(new CarriageReturn());
		ret.add(btnDown);
		return ret;
	}

	private JButton createButton(String iconName, String actionCommand) {
		JButton button = new JButton(IconLoader.getIcon(iconName));
		button.setActionCommand(actionCommand);
		button.addActionListener(actionListener);
		return button;
	}

	private Component getSelectionList() {
		lstSelection = new JList();
		lstSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listModel = new DefaultListModel();
		lstSelection.setModel(listModel);
		lstSelection.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						enableDisableButtons();
					}
				});
		JScrollPane ret = new JScrollPane(lstSelection);
		ret.setPreferredSize(new Dimension(1, 1));
		return ret;
	}

	private Component getAddRemoveButtons() {
		JPanel ret = new JPanel();
		ret.setLayout(new CRFlowLayout());
		btnCurrentRight = createButton("current_right.png", CURRENT_RIGHT);
		btnCurrentLeft = createButton("current_left.png", CURRENT_LEFT);
		btnAllLeft = createButton("all_left.png", ALL_LEFT);
		ret.add(btnCurrentRight);
		ret.add(new CarriageReturn());
		ret.add(btnCurrentLeft);
		ret.add(new CarriageReturn());
		ret.add(btnAllLeft);
		return ret;
	}

	private Component getOptionTree() {
		treeOption = new JTree(optionTreeModel);
		treeOption.getSelectionModel().addTreeSelectionListener(
				new TreeSelectionListener() {

					@Override
					public void valueChanged(TreeSelectionEvent e) {
						enableDisableButtons();
					}
				});
		JScrollPane ret = new JScrollPane(treeOption);
		ret.setPreferredSize(new Dimension(1, 1));
		return ret;
	}

	private void enableDisableButtons() {
		btnCurrentRight.setEnabled(treeOption.getSelectionCount() > 0);
		int selectedListIndex = lstSelection.getSelectedIndex();
		btnCurrentLeft.setEnabled(selectedListIndex != -1);
		btnUp.setEnabled(selectedListIndex > 0);
		btnDown.setEnabled((selectedListIndex != -1)
				&& (selectedListIndex < listModel.size() - 1));
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		return null;
	}

	@Override
	public String getInfoText() {
		return "Select server layers";
	}

	@Override
	public String getTitle() {
		return "Layer configuration";
	}

	@Override
	public String initialize() {
		optionTreeModel = new WMSLayerTreeModel(connectionPanel.getWMSClient());

		GridBagLayout gl = new GridBagLayout();
		this.setLayout(gl);

		// Option tree
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = 1;
		this.add(getOptionTree(), c);

		// add/remove buttons
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.weightx = 0.1;
		this.add(getAddRemoveButtons(), c);

		// selection list
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.weightx = 1;
		this.add(getSelectionList(), c);

		// sort buttons
		c.fill = GridBagConstraints.NONE;
		c.gridx = 3;
		c.weightx = 0.1;
		this.add(getOrderButtons(), c);

		enableDisableButtons();

		return null;
	}

	@Override
	public String postProcess() {
		return null;
	}

	@Override
	public String validateInput() {
		if (getSelectedLayers().length == 0) {
			return "At least a layer must be selected";
		}

		return null;
	}

	public Object[] getSelectedLayers() {
		Object[] ret = new Object[listModel.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = listModel.elementAt(i);
		}
		return ret;
	}
}
