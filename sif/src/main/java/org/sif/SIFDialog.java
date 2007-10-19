package org.sif;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
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

public class SIFDialog extends JDialog implements OutsideFrame,
		ContainerListener, KeyListener, MouseListener {

	private JButton btnOk;

	private JButton btnCancel;

	private boolean accepted = false;

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

	public boolean isAccepted() {
		return accepted;
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
		simplePanel.validateInput();
	}

	public void keyTyped(KeyEvent e) {
	}

	private void exit(boolean ok) {
		SIFDialog.this.setVisible(false);
		SIFDialog.this.dispose();
		SIFDialog.this.accepted = ok;
	}

	public void mouseClicked(MouseEvent e) {
		simplePanel.validateInput();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
