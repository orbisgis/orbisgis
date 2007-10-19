package org.sif;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SIFDialog extends AbstractOutsideFrame {

	private JButton btnOk;

	private JButton btnCancel;

	private SimplePanel simplePanel;

	public SIFDialog(Window owner) {
		super(owner);
		init();
	}

	private void init() {
		this.setLayout(new BorderLayout());

		btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				exit(true);
			}

		});
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				exit(false);
			}

		});
		JPanel pnlButtons = new JPanel();
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);

		this.add(pnlButtons, BorderLayout.SOUTH);

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (btnOk.isEnabled()) {
					btnOk.requestFocus();
				} else {
					btnCancel.requestFocus();
				}
			}

		});

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	public void setComponent(SimplePanel simplePanel) {
		this.simplePanel = simplePanel;
		this.add(simplePanel, BorderLayout.CENTER);
		listen(this);
	}

	public void canContinue() {
		btnOk.setEnabled(true);
	}

	public void cannotContinue() {
		btnOk.setEnabled(false);
	}

	@Override
	protected SimplePanel getPanel() {
		return simplePanel;
	}

}
