/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * An UI Element, which edit and manage UI for one SE element
 *
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIComponent extends LegendUIAbstractPanel {

	protected float weight; // TODO en faire qqch...
	// The direct parent
	protected LegendUIComponent parent;
	// this.parent is null if this component has to be rendered into its own panel
	// In this case, the initial parent is set to parent before setting it to null
	private LegendUIComponent initialParent;
	// Children list
	private ArrayList<LegendUIComponent> children;
	private ArrayList<LegendUIComponentListener> listeners;
	private String name;
	// Is the (this) UI represents a NULL element (such as no fill) ?
	protected boolean isNullComponent;

	// Panel in which UI element will be mounted
	protected LegendUIAbstractPanel editor;
	// Panel reserved for special
	protected LegendUIAbstractPanel toolbar;

	private JButton nullifier;

	/**
	 * This constructor shall be called by each subclasses constructors !
	 * The constructor and the subclasses constructor shouln't add anything to the JPanel
	 *
	 * @see LegendUIComponent.mountChildren()
	 *
	 * @param name name of the component (as listed in the TOC)
	 * @param controller
	 * @param parent new component will be a child of parent
	 * @param weight unused
	 * @param nullable can the edited element be null ?
	 */
	public LegendUIComponent(String name, LegendUIController controller, LegendUIComponent parent, float weight, boolean nullable) {
		super(controller);

		this.weight = weight;
		this.parent = parent;

		this.name = name;

		if (nullable) {
			this.nullifier = new JButton(OrbisGISIcon.getIcon("delete"));
			nullifier.setMargin(new Insets(0, 0, 0, 0));
			this.nullifier.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					isNullComponent = true;
					turnOff();

				}
			});
		} else {
			this.nullifier = null;
		}

		listeners = new ArrayList<LegendUIComponentListener>();

		this.children = new ArrayList<LegendUIComponent>();

		if (this.parent != null) {
			this.parent.registerChild(this);
		}

		//this.setBorder(BorderFactory.createTitledBorder(name));
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		super.setName(name);
		this.name = name;
	}

	@Override
	public final String toString() {
		if (this.getName() != null) {
			return this.getName();
		} else {
			return this.getClass().getSimpleName().replaceAll("LegendUIMeta", "").replaceAll("Panel$", "");
		}
	}

	public Iterator<LegendUIComponent> getChildrenIterator() {
		return children.iterator();
	}

	/**
	 * Does this component starts a new panel ?
	 * If true, then this component will be accessible through LegendUI TOC
	 * @return true if this has to be laid out in its own panel
	 */
	public boolean isNested() {
		return (initialParent != null);
	}

	/**
	 *  Does this component define anything or not ?
	 */
	public boolean isNull() {
		return isNullComponent;
	}

	public boolean isInlinedAndNotNull() {
		return !(isNested() || isNull());
	}

	/**
	 * @return true if this component has no parent
	 */
	public boolean isTopElement() {
		return parent == null && initialParent == null;
	}

	/**
	 * Return the top-most UIComponent in the current panel
	 */
	public LegendUIComponent getScopeParent() {
		LegendUIComponent current = this;
		while (current != null && !current.isNested() && !current.isTopElement()) {
			current = current.parent;
		}

		return current;
	}

	/**
	 * @return Component parent
	 */
	public LegendUIComponent getParentComponent() {
		if (this.parent != null) {
			return this.parent;
		} else {
			return this.initialParent;
		}
	}

	/**
	 *
	 * @return Return the symbolizer UI component
	 */
	public LegendUIComponent getTopParent() {
		LegendUIComponent current = this;

		while (current instanceof LegendUISymbolizerPanel == false) {
			current = current.getParentComponent();
		}

		return current;
	}

	public void makeOrphan() {
		LegendUIComponent p = getParentComponent();

		if (p != null) {
			p.removeChildInternal(this);
		}

		this.parent = null;
		this.initialParent = null;
	}

	private void removeChildInternal(LegendUIComponent child) {
		if (children.contains(child)) {
			children.remove(child);
		}
	}

	protected void removeChild(LegendUIComponent child) {
		if (children.contains(child)) {
			child.makeOrphan();
		}
	}

	/**
	 * @param child
	 */
	private void registerChild(LegendUIComponent child) {
		if (!children.contains(child)) {
			this.children.add(child);
		}
	}

	public void addChild(LegendUIComponent child) {
		this.registerChild(child);
		child.parent = this;
	}

	/**
	 * Bottom to top way to extract a component into a new sub-pane
	 *
	 */
	public void extractFromParent() {
		this.detachFromParent();
		//initialParent.markChildAsExternal(this);
	}

	/**
	 * Top to bottom way to make separate panels
	 * @param child
	 */
	public void extractChild(LegendUIComponent child) {
		//markChildAsExternal(child);
		if (children.contains(child)) {
			child.detachFromParent();
		}
	}

	/**
	 * mark the break between this and its parent
	 */
	private void detachFromParent() {
		if (parent != null && initialParent == null) {
			this.initialParent = this.parent;
			this.parent = null;
		}
	}

	public void unnest() {
		if (parent == null && initialParent != null) {
			this.parent = this.initialParent;
			this.initialParent = null;
		}

		if (this.parent == null) {
		}
	}


	/*
	 * Inline all children (recursivly)
	 */
	public void unnestChildren() {
		for (LegendUIComponent child : this.children) {
			if (child.isNested()) {
				child.unnest();
			}
			child.unnestChildren();
		}
	}

	/**
	 *  remove all children (recursivly)
	 */
	public void clear() {
		this.removeAll();
		this.clearListener();
		for (LegendUIComponent child : children) {
			child.clear();
			child.parent = null;
			child.initialParent = null;
		}
		children.clear();
	}

	public void clearListener() {
		listeners.clear();
	}

	public void register(LegendUIComponentListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	protected void fireNameChanged() {
		for (LegendUIComponentListener l : listeners) {
			l.nameChanged();
		}
	}

	/**
	 * Deep-build the interface from this to button
	 */
	protected void mountComponentForChildren() {
		this.removeAll();

		/*   _______________
		 *  |               |
		 *  |    toolbar    |
		 *  |_______________|
		 *  |               |
		 *  |    editor     |
		 *  |_______________|
		 */

		toolbar = new LegendUIAbstractPanel(controller);
		//toolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));

		editor = new LegendUIAbstractPanel(controller);

		this.add(toolbar, BorderLayout.NORTH);
		this.add(editor, BorderLayout.SOUTH);

		// List of null parameters (will be listed (by name) in the toolbar)
		ArrayList<LegendUIComponent> nullList = new ArrayList<LegendUIComponent>();

		Iterator<LegendUIComponent> it = this.getChildrenIterator();
		while (it.hasNext()) {
			LegendUIComponent child = it.next();
			// Child is not null => create its interface
			if (!child.isNull()) {
				child.mountComponentForChildren();
				child.mountComponent();
			} else {
				// Child is null => in the list of unused parameters
				nullList.add(child);
			}
		}

		if (nullList.size() > 0) {
			/*
			 * For each null parameter, create at SetUpLabel, and puch it in the toolbar
			 */
			Iterator<LegendUIComponent> nullIt = nullList.iterator();
			while (nullIt.hasNext()) {
				LegendUIComponent next = nullIt.next();
				toolbar.add(new SetUpParam(next));
			}

		}

		if (nullifier != null) {
			// Add a button to nullifiy this component (so it will be listed in its parent toolbar)
			toolbar.add(nullifier);
		}

		this.mountComponent();
	}

	private class SetUpParam extends JLabel {

		LegendUIComponent comp;

		public SetUpParam(final LegendUIComponent comp) {
			super("<html><u><i>" + comp.toString() + "</i></u></html>");
			this.comp = comp;
			setForeground(new Color(160, 160, 160));
			this.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					comp.turnOn();
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}
			});
		}
	};


	/**
	 * The icon describing this component
	 * @return the icon
	 */
	public abstract Icon getIcon();

	/**
	 * This method is called by the controller. It's the method that actually add
	 * sub-components in the editor LegendUIAbstractPanel (i.e. the JPanel).
	 *
	 * It's the only method that add and update the LegendUIAbstractPanel content!
	 *
	 * Implementators shall only add content to the editor LegendUIAbstractPanel
	 *
	 * @see
	 *
	 */
	protected abstract void mountComponent();

	/**
	 * This method is called when the user want to disactivate this component
	 */
	protected abstract void turnOff();

	/**
	 * This method is called when the user want to (re-)activate this component
	 */
	protected abstract void turnOn();

	/**
	 * Return the edited SE Component Class
	 * This is used by LegendUIAbstractMetaPanel().
	 *
	 * @return
	 */
	public abstract Class getEditedClass();

}
