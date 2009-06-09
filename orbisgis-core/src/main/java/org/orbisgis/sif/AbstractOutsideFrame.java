/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.sif;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.JDialog;

public abstract class AbstractOutsideFrame extends JDialog implements
		OutsideFrame, ContainerListener, KeyListener, MouseListener, ActionListener, FocusListener {

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
		c.removeFocusListener(this);
		if (c instanceof JComboBox) {
			((JComboBox)c).removeActionListener(this);
		}
		// Add KeyListener to the Component passed as an argument
		c.addKeyListener(this);
		c.addMouseListener(this);
		c.addFocusListener(this);
		if (c instanceof JComboBox) {
			((JComboBox)c).addActionListener(this);
		}

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
		getPanel().validateInput();
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
			if (getPanel().postProcess()) {
				saveInput();
			} else {
				return;
			}
		}
		accepted = ok;
		setVisible(false);
		dispose();
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

	public void actionPerformed(ActionEvent e) {
		getPanel().validateInput();
	}

	@Override
	public void focusGained(FocusEvent e) {
		getPanel().validateInput();
	}
	
	@Override
	public void focusLost(FocusEvent e) {
	}
	
}
