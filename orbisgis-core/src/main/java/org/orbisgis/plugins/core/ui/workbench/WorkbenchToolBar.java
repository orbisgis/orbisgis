package org.orbisgis.plugins.core.ui.workbench;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;
import org.orbisgis.plugins.core.ui.PlugIn;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.map.MapControl;
import org.orbisgis.plugins.core.ui.editors.map.tool.Automaton;
import org.orbisgis.plugins.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.images.IconLoader;

public class WorkbenchToolBar extends EnableableToolBar implements Observer {

	private WorkbenchContext context;
	private HashMap<String, WorkbenchToolBar> toolbars = new HashMap<String, WorkbenchToolBar>();

	public WorkbenchToolBar(WorkbenchContext workbenchContext) {
		this.context = workbenchContext;
	}

	public HashMap<String, WorkbenchToolBar> getToolbars() {
		return toolbars;
	}

	public boolean haveAnOtherToolBar() {
		return toolbars.size() > 0 ? true : false;
	}

	public WorkbenchToolBar(WorkbenchContext workbenchContext, String name) {
		super(name);
		this.context = workbenchContext;
	}

	public void addPlugIn(final PlugIn plugIn, Component c) {
		context.addObserver((Observer) plugIn);
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
				// when he switches to table editor : All Map tools are not
				// enabled in "Map Edition toolbar"
				// But This toolbar is always displayed.
				((WorkbenchToolBar) comp).setFloatable(false);
				toolbars.put(comp.getName(), (WorkbenchToolBar) comp);
				context.addObserver((WorkbenchToolBar) comp);
			} else if (comp instanceof JPanel) {
				Component actionComponent = (Component) ((AbstractPlugIn) constraints)
						.getActionComponent();
				String typeListener = (String) ((AbstractPlugIn) constraints)
						.getTypeListener();
				if (actionComponent != null) {
					if (typeListener.equals("item"))
						((JComboBox) actionComponent)
								.addItemListener(AbstractPlugIn.toItemListener(
										(AbstractPlugIn) constraints, context));
					else if (typeListener.equals("action"))
						((JButton) actionComponent)
								.addActionListener(AbstractPlugIn
										.toActionListener(
												(AbstractPlugIn) constraints,
												context));
				}
			} else {
				((JButton) comp).addActionListener(AbstractPlugIn
						.toActionListener((PlugIn) constraints, context));
			}
		}
		super.addImpl(comp, constraints, index);
	}

	// TOOLBAR Automaton
	public JToggleButton addAutomaton(final Automaton automaton, String icon) {
		JToggleButton jtoggle = new JToggleButton() {
			public String getToolTipText(MouseEvent event) {
				return automaton.getName();
			}
		};
		jtoggle.setIcon(IconLoader.getIcon(icon));
		return addCursorAutomaton(automaton.getName(), automaton, jtoggle);
	}

	private JToggleButton addCursorAutomaton(String tooltip,
			final Automaton automaton, JToggleButton button) {
		add(button, tooltip, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EditorManager em = (EditorManager) Services
						.getService(EditorManager.class);
				IEditor editor = em.getActiveEditor();
				MapEditorPlugIn mapEditor = (MapEditorPlugIn) editor;
				if (mapEditor.getComponent() != null) {
					try {
						automaton.setMouseCursor(automaton.getMouseCursor());
						((MapControl) mapEditor.getComponent())
								.setTool(automaton);
						WorkbenchContext wbContext = Services
								.getService(WorkbenchContext.class);
						wbContext.setLastAction("Set Tool");
					} catch (TransitionException e1) {
						Services.getErrorManager().error(
								"cannot add Automaton", e1);
					}
				}
			}
		}, automaton);
		context.addObserver((Observer) automaton);
		automaton.setButton(button);
		return button;
	}

	public void update(Observable o, Object arg) {
		// System.out.println("update toolbar " + this.getName());
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i).isEnabled() && getComponent(i).isVisible()) {
				this.setVisible(true);
				break;
			}
			this.setVisible(false);
		}
	}
}
