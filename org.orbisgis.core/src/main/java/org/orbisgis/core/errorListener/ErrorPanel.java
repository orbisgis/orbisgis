package org.orbisgis.core.errorListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class ErrorPanel extends JPanel {

	private static final String SHOW = "Show";
	private static final String DELETE = "Delete";
	private static final String DETAILS = "Show Details >>";
	private JPanel normalPanel;
	private JTextArea txt;
	private JButton btnDetails;
	private JPanel extendedPanel;
	private JPanel controlButtonsPanel;
	private JButton btnDelete;
	private JButton btnShow;
	private JTable tbl;
	private ErrorsTableModel errorsModel;
	private JTextArea txtException;
	private MyListener myListener = new MyListener();
	private JFrame frame;

	public ErrorPanel(JFrame frame) {
		this.frame = frame;
		this.setLayout(new BorderLayout());
		this.add(getNormalPanel(), BorderLayout.NORTH);
		this.add(getExtendedPanel(), BorderLayout.CENTER);
		enableButtons();
	}

	private JPanel getExtendedPanel() {
		if (extendedPanel == null) {
			extendedPanel = new JPanel();
			BorderLayout borderLayout = new BorderLayout();
			borderLayout.setHgap(30);
			extendedPanel.setLayout(borderLayout);
			extendedPanel.add(new JSeparator(), BorderLayout.NORTH);
			errorsModel = new ErrorsTableModel();
			tbl = new JTable(errorsModel);
			tbl.setPreferredSize(new Dimension(100, 100));
			tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tbl.getSelectionModel().addListSelectionListener(myListener);
			tbl.addMouseListener(myListener);
			tbl.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					AutofitTableColumns.autoResizeTable(tbl, true);
				}

			});
			final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			split.setLeftComponent(new JScrollPane(tbl));
			txtException = new JTextArea(4, 4);
			txtException.setPreferredSize(new Dimension(200, 200));
			split.setRightComponent(txtException);
			split.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					split.setDividerLocation(0.5);
				}

			});
			extendedPanel.add(split, BorderLayout.CENTER);
			extendedPanel.add(getControlButtonsPanel(), BorderLayout.EAST);
			extendedPanel.setVisible(false);
		}

		return extendedPanel;
	}

	private Component getControlButtonsPanel() {
		if (controlButtonsPanel == null) {
			controlButtonsPanel = new JPanel();
			controlButtonsPanel.setLayout(new CRFlowLayout());
			btnDelete = createButton(DELETE);
			controlButtonsPanel.add(btnDelete);
			controlButtonsPanel.add(new CarriageReturn());
			btnShow = createButton(SHOW);
			controlButtonsPanel.add(btnShow);
			controlButtonsPanel.add(new CarriageReturn());
		}

		return controlButtonsPanel;
	}

	private JButton createButton(String text) {
		JButton btn = new JButton(text);
		btn.setActionCommand(text);
		btn.addActionListener(myListener);
		return btn;
	}

	private JPanel getNormalPanel() {
		if (normalPanel == null) {
			normalPanel = new JPanel();
			normalPanel.setLayout(new BorderLayout());
			txt = new JTextArea(5, 60);
			txt.setBorder(null);
			txt.setEditable(false);
			normalPanel.add(txt, BorderLayout.NORTH);
			JPanel buttons = new JPanel();
			btnDetails = createButton(DETAILS);
			buttons.add(btnDetails);
			normalPanel.add(buttons, BorderLayout.SOUTH);
		}

		return normalPanel;
	}

	private class MyListener extends MouseAdapter implements ActionListener,
			ListSelectionListener, MouseListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(DETAILS)) {
				if (extendedPanel.isVisible()) {
					extendedPanel.setVisible(false);
					btnDetails.setText(DETAILS);
					frame.pack();
				} else {
					extendedPanel.setVisible(true);
					btnDetails.setText("<< Hide Details");
					frame.pack();
				}
			} else if (e.getActionCommand().equals(DELETE)) {
				if (tbl.getSelectedRow() != -1) {
					errorsModel.removeError(tbl.getSelectedRow());
				}
			} else if (e.getActionCommand().equals(SHOW)) {
				if (tbl.getSelectedRow() != -1) {
					txtException.setText(errorsModel.getTrace(tbl
							.getSelectedRow()));
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			enableButtons();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				txtException
						.setText(errorsModel.getTrace(tbl.getSelectedRow()));
			}
		}

	}

	private void enableButtons() {
		if (tbl.getSelectedRow() != -1) {
			btnDelete.setEnabled(true);
			btnShow.setEnabled(true);
		} else {
			btnDelete.setEnabled(false);
			btnShow.setEnabled(false);
		}
	}

	public void addError(ErrorMessage errorMessage) {
		errorsModel.addError(errorMessage);
	}
}
