package org.orbisgis.ui.listManager;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ListManager extends JPanel {

	private static final String MODIFY = "Modify";
	private static final String REMOVE = "Remove";
	private static final String ADD = "Add";
	private ListManagerListener listener;
	private JTable table;
	private JButton btnModify;
	private JButton btnRemove;
	private JButton btnAdd;
	private TableModel model;

	public ListManager(ListManagerListener listener, TableModel tableModel) {
		JPanel pnlButtons = new JPanel();
		pnlButtons.setLayout(new CRFlowLayout());
		btnAdd = getButton(ADD);
		pnlButtons.add(btnAdd);
		pnlButtons.add(new CarriageReturn());
		btnRemove = getButton(REMOVE);
		pnlButtons.add(btnRemove);
		pnlButtons.add(new CarriageReturn());
		btnModify = getButton(MODIFY);
		pnlButtons.add(btnModify);
		pnlButtons.add(new CarriageReturn());
		this.setLayout(new BorderLayout());
		table = new JTable();
		this.model = tableModel;
		table.setModel(model);
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		this.add(pnlButtons, BorderLayout.EAST);

		RefreshListener refreshListener = new RefreshListener();
		table.addKeyListener(refreshListener);
		table.addMouseListener(refreshListener);

		this.listener = listener;

		updateButtons();
	}

	private JButton getButton(String text) {
		JButton ret = new JButton(text);
		ret.setActionCommand(text);
		ret.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals(ADD)) {
					listener.addNewElement();
				} else if (e.getActionCommand().equals(REMOVE)) {
					listener.removeElement(table.getSelectedRow());
				} else if (e.getActionCommand().equals(MODIFY)) {
					listener.modifyElement(table.getSelectedRow());
				}
				updateButtons();
			}
		});

		return ret;
	}

	private void updateButtons() {
		btnRemove.setEnabled(table.getSelectedRow() != -1);
		btnModify.setEnabled(table.getSelectedRow() != -1);
	}

	private class RefreshListener implements KeyListener, MouseListener {

		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
			updateButtons();
		}

		public void keyTyped(KeyEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			updateButtons();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
			updateButtons();
		}

	}

}
