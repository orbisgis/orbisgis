/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.pluginSystem.workbench;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.components.button.DropDownButton;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;

public class WorkbenchToolBar extends EnableableToolBar implements Observer {

	private WorkbenchContext context;
	private Map<String, WorkbenchToolBar> toolbars = new HashMap<String, WorkbenchToolBar>();
	private DropDownButton dropDownButton;
	private JPopupMenu popup = new JPopupMenu();
	private boolean dropDown;

	private List<Observer> toolsPlugInObservers = new ArrayList<Observer>();

	public DropDownButton getDropDownButton() {
		return dropDownButton;
	}

	public WorkbenchToolBar(WorkbenchContext workbenchContext) {
		this.context = workbenchContext;

	}

	public Map<String, WorkbenchToolBar> getToolbars() {
		return toolbars;
	}

	public boolean haveAnOtherToolBar() {
		return toolbars.size() > 0 ? true : false;
	}

	public WorkbenchToolBar(WorkbenchContext workbenchContext, String name) {
		super(name);
		this.context = workbenchContext;
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}

	public WorkbenchToolBar(WorkbenchContext workbenchContext, String name,
			ImageIcon iconFile, boolean dropDown) {
		super(name);
		this.context = workbenchContext;
		this.dropDown = dropDown;
		dropDownButton = new DropDownButton(iconFile) {

			protected JPopupMenu getPopupMenu() {
				return popup;
			}
		};
		dropDownButton.addToToolBar(this);
		dropDownButton.setVisible(false);
		dropDownButton.setEnabled(false);
		dropDownButton.setBorderPainted(false);
		setOpaque(false);
	}

	public void addPanelPlugIn(final PlugIn plugIn, Component c,
			PlugInContext plugInContext) {
		((AbstractPlugIn) plugIn).setPlugInContext(plugInContext);
		toolsPlugInObservers.add(plugIn);
		add(c, plugIn);
	}

	public void addPlugIn(final PlugIn plugIn, Component c,
			PlugInContext plugInContext) {
		((AbstractPlugIn) plugIn).setPlugInContext(plugInContext);
		toolsPlugInObservers.add(plugIn);
		((JButton) c).setBorderPainted(false);
		((JButton) c).addActionListener(AbstractPlugIn.toActionListener(plugIn,
				context));
		add(c, plugIn);
	}

	protected void addImpl(Component comp, final Object constraints, int index) {
		if (constraints instanceof Automaton) {

		} else {
			if (comp instanceof JComboBox) {
				((JComboBox) comp).addItemListener(AbstractPlugIn
						.toItemListener((PlugIn) constraints, context));
			} else if (comp instanceof JToolBar) {
				// TODO : For the moment tool bar is not floatable. This resolve
				// a problem, but not the solution
				// Maybe we'll extend JToolbar parent to modify toolbar
				// comportment.
				// We 'll working on at UI review moment.
				// PROBLEM :Consider toolbars : "Map Edition tools",
				// "Table Edition tools".
				// User is on map Editor and this toolbar is out the frame (Map
				// Edition toolbar).
				// when he switches to table editor : All MaJToggleButtonp tools
				// are not
				// enabled in "Map Edition toolbar"
				// But This toolbar is always displayed.
				((WorkbenchToolBar) comp).setFloatable(false);
				toolbars.put(comp.getName(), (WorkbenchToolBar) comp);
				toolsPlugInObservers.add((WorkbenchToolBar) comp);
			} else if (comp instanceof JPanel) {
				Component actionComponent = (Component) ((AbstractPlugIn) constraints)
						.getActionComponent();
				String typeListener = (String) ((AbstractPlugIn) constraints)
						.getTypeListener();
				if (actionComponent != null) {
					if (typeListener.equals("item")) {
						((JComboBox) actionComponent)
								.addItemListener(AbstractPlugIn.toItemListener(
										(AbstractPlugIn) constraints, context));
					} else if (typeListener.equals("action")) {
						if (actionComponent instanceof JButton) {
							((JButton) actionComponent)
									.addActionListener(AbstractPlugIn
											.toActionListener(
													(AbstractPlugIn) constraints,
													context));
						} else if (actionComponent instanceof JTextField) {
							((JTextField) actionComponent)
									.addActionListener(AbstractPlugIn
											.toActionListener(
													(AbstractPlugIn) constraints,
													context));
						}
					} else if (typeListener.equals("check"))
						((JCheckBox) actionComponent)
								.addActionListener(AbstractPlugIn
										.toActionListener(
												(AbstractPlugIn) constraints,
												context));
				}
			}
		}
		super.addImpl(comp, constraints, index);
	}

	// TOOLBAR Automaton
	public AbstractButton addAutomaton(final Automaton automaton, ImageIcon icon) {
		AbstractButton c = null;
		if (dropDown) {
			c = new JMenuItem() {
				public String getToolTipText(MouseEvent event) {
					return automaton.getName();
				}
			};
			popup.add(c);
		} else {
			c = new JToggleButton() {
				public String getToolTipText(MouseEvent event) {
					return automaton.getName();
				}
			};
		}
		c.setIcon(icon);
		c.setBorderPainted(false);

		return addCursorAutomaton(automaton.getName(), automaton, c, icon);
	}

	private AbstractButton addCursorAutomaton(String tooltip,
			final Automaton automaton, final AbstractButton button,
			final ImageIcon icon) {
		toolsPlugInObservers.add(automaton);
		button.setBorderPainted(false);
		add(button, dropDown, tooltip, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditorManager em = (EditorManager) Services
						.getService(EditorManager.class);
				IEditor editor = em.getActiveEditor();
				MapEditorPlugIn mapEditor = (MapEditorPlugIn) editor;
				if (mapEditor != null && mapEditor.getMapControl() != null) {
					try {
						mapEditor.getMapControl().setTool(automaton);
						WorkbenchContext wbContext = Services
								.getService(WorkbenchContext.class);
						wbContext.getWorkbench().getFrame()
								.getWorkbenchToolBar().clearSelection();
						if (dropDown) {
							dropDownButton.setSelected(true);
							dropDownButton.setIconFile(icon);
						}
						wbContext.setLastAction("Set Tool");
					} catch (TransitionException e1) {
						Services.getErrorManager().error(
								"cannot add Automaton", e1);
					}
				}
			}
		}, automaton);
		automaton.setButton(button);
		return button;
	}

	protected void clearSelection() {
		for (int i = 0; i < getComponentCount(); i++) {
			if (((WorkbenchToolBar) getComponent(i)).getDropDownButton() != null) {
				((WorkbenchToolBar) getComponent(i)).getDropDownButton()
						.setSelected(false);
			}
		}
	}

	public void update(Observable arg0, Object arg1) {
		this.setVisible(false);
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i).isEnabled()
					&& !(getComponent(i) instanceof Separator)) {
				this.setVisible(true);
			}
		}
		if (dropDownButton != null) {
			dropDownButton.setEnabled(false);
			for (int j = 0; j < popup.getComponentCount(); j++) {
				if (popup.getComponent(j).isEnabled()
						&& popup.getComponent(j).isVisible()
						&& !(popup.getComponent(j) instanceof Separator)) {
					this.setVisible(true);
					dropDownButton.setVisible(true);
					dropDownButton.setEnabled(true);
				}
			}
		}
	}

	public List<Observer> getToolsPlugInObservers() {
		return toolsPlugInObservers;
	}
}
