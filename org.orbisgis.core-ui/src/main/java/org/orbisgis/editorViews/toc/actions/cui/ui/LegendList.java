package org.orbisgis.editorViews.toc.actions.cui.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.orbisgis.editorViews.toc.actions.cui.gui.ILegendPanelUI;
import org.orbisgis.editorViews.toc.actions.cui.gui.widgets.JPanelComboLegendPicker;
import org.orbisgis.images.IconLoader;
import org.orbisgis.ui.sif.AskValue;
import org.sif.UIFactory;

public class LegendList extends JPanel {
	private JToolBar toolBar;
	private JButton jButtonMenuAdd;
	private JButton jButtonMenuDel;
	private JButton jButtonMenuDown;
	private JButton jButtonMenuRename;
	private JButton jButtonMenuUp;

	private JList lst;
	private LegendsPanel legendsPanel;
	private LegendModel model;

	public LegendList(LegendsPanel legendsPanel) {
		this.legendsPanel = legendsPanel;
		toolBar = new JToolBar();
		toolBar.setFloatable(false);

		jButtonMenuUp = new JButton();
		jButtonMenuUp.setIcon(IconLoader.getIcon("go-up.png"));
		jButtonMenuUp.setToolTipText("Up");
		jButtonMenuUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuUpActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuUp);

		jButtonMenuDown = new JButton();
		jButtonMenuDown.setIcon(IconLoader.getIcon("go-down.png"));
		jButtonMenuDown.setToolTipText("Down");
		jButtonMenuDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuDownActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuDown);

		jButtonMenuAdd = new JButton();
		jButtonMenuAdd.setIcon(IconLoader.getIcon("picture_add.png"));
		jButtonMenuAdd.setToolTipText("Add");
		jButtonMenuAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuAddActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuAdd);

		jButtonMenuDel = new JButton();
		jButtonMenuDel.setIcon(IconLoader.getIcon("picture_delete.png"));
		jButtonMenuDel.setToolTipText("Delete");
		jButtonMenuDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuDelActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuDel);

		jButtonMenuRename = new JButton();
		jButtonMenuRename.setIcon(IconLoader.getIcon("picture_edit.png"));
		jButtonMenuRename.setToolTipText("Rename");
		jButtonMenuRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				jButtonMenuRenameActionPerformed(evt);
			}
		});
		toolBar.add(jButtonMenuRename);

		lst = new JList();
		lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lst.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				jList1ValueChanged(evt);
				refresh();
			}
		});

		this.setLayout(new BorderLayout());
		this.add(toolBar, BorderLayout.PAGE_START);
		this.add(new JScrollPane(lst), BorderLayout.CENTER);

		model = new LegendModel();
		lst.setModel(model);

		refresh();
	}

	/**
	 * Refresh the state of the menu buttons
	 */
	public void refresh() {
		model.refresh();

		int idx = lst.getSelectedIndex();
		int maximo = lst.getModel().getSize() - 1;
		int minimo = 0;

		if (idx == -1) {
			jButtonMenuUp.setEnabled(false);
			jButtonMenuDown.setEnabled(false);
			jButtonMenuDel.setEnabled(false);
			jButtonMenuRename.setEnabled(false);
		} else {
			jButtonMenuDel.setEnabled(true);
			jButtonMenuRename.setEnabled(true);
			if (idx == minimo) {
				if (idx == maximo)
					jButtonMenuDown.setEnabled(false);
				else
					jButtonMenuDown.setEnabled(true);
				jButtonMenuUp.setEnabled(false);
			} else {
				if (idx == maximo) {
					jButtonMenuUp.setEnabled(true);
					jButtonMenuDown.setEnabled(false);
				} else {
					jButtonMenuUp.setEnabled(true);
					jButtonMenuDown.setEnabled(true);
				}
			}
		}

	}

	/**
	 * rename of a selected value in the list.
	 *
	 * @param evt
	 */
	private void jButtonMenuRenameActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonMenuRenameActionPerformed
		String legendName = (String) lst.getSelectedValue();
		int idx = lst.getSelectedIndex();

		AskValue ask = new AskValue("Insert the new name", "txt is not null",
				"A name must be specified", legendName);
		String newName = "";
		if (UIFactory.showDialog(ask)) {
			newName = ask.getValue();
			legendsPanel.legendRenamed(idx, newName);
		}
	}

	/**
	 * remove a selected values
	 *
	 * @param evt
	 */
	private void jButtonMenuDelActionPerformed(ActionEvent evt) {
		int idx = lst.getSelectedIndex();
		int size = lst.getModel().getSize();
		if (lst.getSelectedIndex() >= size - 1) {
			lst.setSelectedIndex(Math.min(lst.getSelectedIndex() - 1, 0));
		}
		legendsPanel.legendRemoved(idx);
	}

	/**
	 * adds a new legend
	 *
	 * @param evt
	 */
	private void jButtonMenuAddActionPerformed(ActionEvent evt) {// GEN-FIRST:event_jButtonMenuAddActionPerformed
		ArrayList<String> paneNames = new ArrayList<String>();
		ArrayList<ILegendPanelUI> ids = new ArrayList<ILegendPanelUI>();
		ILegendPanelUI[] legends = legendsPanel.getAvailableLegends();
		for (int i = 0; i < legends.length; i++) {
			ILegendPanelUI legendPanelUI = legends[i];
			if (legendPanelUI.acceptsGeometryType(legendsPanel
					.getGeometryType())) {
				paneNames.add(legendPanelUI.getLegendTypeName());
				ids.add(legendPanelUI);
			}
		}
		JPanelComboLegendPicker legendPicker = new JPanelComboLegendPicker(
				paneNames.toArray(new String[0]), ids
						.toArray(new ILegendPanelUI[0]));

		if (UIFactory.showDialog(legendPicker)) {
			ILegendPanelUI panel = (ILegendPanelUI) legendPicker.getSelected();

			legendsPanel.legendAdded(panel);
		}
	}

	/**
	 * move down the selected legend
	 *
	 * @param evt
	 */
	private void jButtonMenuDownActionPerformed(ActionEvent evt) {
		int idx = lst.getSelectedIndex();
		lst.setSelectedIndex(idx + 1);
		legendsPanel.legendMovedDown(idx);
	}

	/**
	 * moves up the selected legend
	 *
	 * @param evt
	 */
	private void jButtonMenuUpActionPerformed(ActionEvent evt) {
		int idx = lst.getSelectedIndex();
		lst.setSelectedIndex(idx - 1);
		legendsPanel.legendMovedUp(idx);
	}

	/**
	 * selection of a new legend in the list. it will fire this event and will
	 * open the appropriate panel for these legend.
	 *
	 * @param evt
	 */
	private void jList1ValueChanged(ListSelectionEvent evt) {
		legendsPanel.legendSelected(lst.getSelectedIndex());
	}

	public int getSelectedIndex() {
		return lst.getSelectedIndex();
	}

	private class LegendModel extends AbstractListModel implements ListModel {

		public Object getElementAt(int index) {
			return legendsPanel.getLegendsNames()[index];
		}

		public void refresh() {
			fireContentsChanged(this, 0, getSize() + 1);
		}

		public int getSize() {
			return legendsPanel.getLegendsNames().length;
		}

	}
}