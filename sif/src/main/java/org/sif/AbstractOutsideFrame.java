package org.sif;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;

public abstract class AbstractOutsideFrame extends JDialog implements
		OutsideFrame, ContainerListener, KeyListener, MouseListener {

	private boolean accepted = false;

	public AbstractOutsideFrame(Window owner) {
		super(owner);
	}

	protected void listen(Component c) {
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

	protected void unlisten(Component c) {
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
		getPanel().validateInput();
	}

	protected abstract SimplePanel getPanel();

	public void keyTyped(KeyEvent e) {
	}

	void exit(boolean ok) {
		if (ok) {
			saveInput();
		}
		setVisible(false);
		dispose();
		accepted = ok;
	}

	protected abstract void saveInput();

	public void mouseClicked(MouseEvent e) {
		getPanel().validateInput();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public boolean isAccepted() {
		return accepted;
	}

}
