package org.sif;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SIFWizard extends AbstractOutsideFrame {

	private JPanel wizardButtons;
	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnFinish;
	private JButton btnCancel;
	private JPanel mainPanel;

	private boolean test;

	private SimplePanel[] panels;
	private int index = 0;

	private CardLayout layout = new CardLayout();

	public SIFWizard(Window owner) {
		super(owner);
		init();
	}

	private void init() {
		this.setLayout(new BorderLayout());

		this.add(getWizardButtons(), BorderLayout.SOUTH);

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (test) {
					exit(true);
				} else {
					if (btnFinish.isEnabled()) {
						btnFinish.requestFocus();
					} else {
						btnCancel.requestFocus();
					}
				}
			}

		});

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private JPanel getWizardButtons() {
		if (wizardButtons == null) {
			wizardButtons = new JPanel();
			wizardButtons.add(getBtnPrevious());
			wizardButtons.add(getBtnNext());
			wizardButtons.add(getBtnFinish());
			wizardButtons.add(getBtnCancel());
		}

		return wizardButtons;
	}

	private void buildMainPanel(SimplePanel[] panels) {
		mainPanel = new JPanel();
		mainPanel.setLayout(layout);

		for (int i = 0; i < panels.length; i++) {
			mainPanel.add(panels[i], Integer.toString(i));
		}
	}

	public JButton getBtnPrevious() {
		if (btnPrevious == null) {
			btnPrevious = new JButton("Previous");
			btnPrevious.setEnabled(false);
			btnPrevious.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					index--;
					layout.previous(mainPanel);
				}

			});
		}

		return btnPrevious;
	}

	public JButton getBtnNext() {
		if (btnNext == null) {
			btnNext = new JButton("Next");
			btnNext.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (getPanel().postProcess()) {
						index++;
						layout.next(mainPanel);
						getPanel().initialize();
					} else {
						return;
					}
				}

			});
		}

		return btnNext;
	}

	public JButton getBtnFinish() {
		if (btnFinish == null) {
			btnFinish = new JButton("Finish");
			btnFinish.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					exit(true);
				}

			});
		}

		return btnFinish;
	}

	public JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					exit(false);
				}

			});
		}

		return btnCancel;
	}

	public void setComponent(SimplePanel[] panels, HashMap<String, String> inputs) {
		this.panels = panels;
		this.index = 0;
		panels[0].validateInput();
		buildMainPanel(panels);
		this.add(mainPanel, BorderLayout.CENTER);
		listen(this);
		loadInput(inputs);
		getPanel().initialize();
		this.setIconImage(getPanel().getIconImage());
	}

	public void canContinue() {
		enableByPosition();
		visualizeByPosition();

		btnNext.setEnabled(true);
		btnFinish.setEnabled(true);
	}

	private void visualizeByPosition() {
		if (panels != null) {
			if (index == panels.length - 1) {
				btnFinish.setVisible(true);
				btnNext.setVisible(false);
			} else {
				btnFinish.setVisible(false);
				btnNext.setVisible(true);
			}
		}
	}

	private void enableByPosition() {
		if (panels != null) {
			if (index == 0) {
				btnPrevious.setEnabled(false);
			} else {
				btnPrevious.setEnabled(true);
			}

			if (index < panels.length - 1) {
				btnNext.setEnabled(true);
				btnFinish.setEnabled(false);
			} else {
				btnNext.setEnabled(false);
				btnFinish.setEnabled(true);
			}
		}
	}

	public void cannotContinue() {
		enableByPosition();
		visualizeByPosition();

		btnNext.setEnabled(false);
		btnFinish.setEnabled(false);
	}

	@Override
	protected SimplePanel getPanel() {
		return panels[index];
	}

	protected void loadInput(HashMap<String, String> inputs) {
		test = true;
		for (SimplePanel panel : panels) {
			if (!panel.loadInput(inputs)) {
				test = false;
			}
		}
	}

	@Override
	protected void saveInput() {
		for (SimplePanel panel : panels) {
			panel.saveInput();
		}
	}

}
