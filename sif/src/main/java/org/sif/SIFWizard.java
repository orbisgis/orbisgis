package org.sif;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SIFWizard extends JDialog implements OutsideFrame,
		ContainerListener, KeyListener, MouseListener {

	private JPanel wizardButtons;
	private JButton btnPrevious;
	private JButton btnNext;
	private JButton btnFinish;
	private JButton btnCancel;
	private JPanel mainPanel;

	private SimplePanel[] panels;
	private int index = 0;

	private CardLayout layout = new CardLayout();
	private boolean accepted = false;

	public SIFWizard(Frame owner) {
		super(owner);
		init();
	}

	public SIFWizard(JDialog owner) {
		super(owner);
		init();
	}

	private void init() {
		this.setLayout(new BorderLayout());

		this.add(getWizardButtons(), BorderLayout.SOUTH);

		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentShown(ComponentEvent e) {
				if (btnFinish.isEnabled()) {
					btnFinish.requestFocus();
				} else {
					btnCancel.requestFocus();
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
					index++;
					layout.next(mainPanel);
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

	public void setComponent(SimplePanel[] panels) {
		this.panels = panels;
		this.index = 0;
		panels[0].validateInput();
		buildMainPanel(panels);
		this.add(mainPanel, BorderLayout.CENTER);
		listen(this);
	}

	private void listen(Component c) {
		// To be on the safe side, try to remove KeyListener first just in case
		// it has been added before.
		// If not, it won't do any harm
		c.removeKeyListener(this);
		c.removeMouseListener(this);
		// Add KeyListener to the Component passed as an argument
		c.addKeyListener(this);
		c.addMouseListener(this);

		if (c instanceof Container) {

			// Component c is a Container. The following cast is safe.
			Container cont = (Container) c;

			// To be on the safe side, try to remove ContainerListener first
			// just in case it has been added before.
			// If not, it won't do any harm
			cont.removeContainerListener(this);
			// Add ContainerListener to the Container.
			cont.addContainerListener(this);

			// Get the Container's array of children Components.
			Component[] children = cont.getComponents();

			// For every child repeat the above operation.
			for (int i = 0; i < children.length; i++) {
				listen(children[i]);
			}
		}
	}

	private void unlisten(Component c) {
		c.removeKeyListener(this);
		c.removeMouseListener(this);

		if (c instanceof Container) {

			Container cont = (Container) c;

			cont.removeContainerListener(this);

			Component[] children = cont.getComponents();

			for (int i = 0; i < children.length; i++) {
				unlisten(children[i]);
			}
		}
	}

	public void componentAdded(ContainerEvent e) {
		listen(e.getChild());
	}

	public void componentRemoved(ContainerEvent e) {
		unlisten(e.getChild());
	}

	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			// Key pressed is the ESCAPE key. Hide this Dialog.
			exit(false);
		}
	}

	public void keyReleased(KeyEvent e) {
		panels[index].validateInput();
	}

	public void keyTyped(KeyEvent e) {
	}

	private void exit(boolean ok) {
		setVisible(false);
		dispose();
		accepted = ok;
	}

	public void mouseClicked(MouseEvent e) {
		panels[index].validateInput();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	protected boolean isAccepted() {
		return accepted;
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

}
