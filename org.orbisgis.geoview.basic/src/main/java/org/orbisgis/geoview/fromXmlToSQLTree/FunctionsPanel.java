package org.orbisgis.geoview.fromXmlToSQLTree;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geoview.Register;
import org.orbisgis.geoview.basic.persistence.ClassName;
import org.orbisgis.geoview.basic.persistence.Menu;
import org.orbisgis.geoview.basic.persistence.MenuItem;
import org.orbisgis.geoview.basic.persistence.SqlInstr;

public class FunctionsPanel extends ResourceTree {
	private final String EOL = System.getProperty("line.separator");

	private DescriptionScrollPane descriptionScrollPane;
	private Menu rootMenu;

	public FunctionsPanel(final DescriptionScrollPane descriptionScrollPane)
			throws JAXBException {
		this.descriptionScrollPane = descriptionScrollPane;

		rootMenu = Register.getMenu();
		setModel(new ToolsMenuPanelTreeModel(rootMenu, getTree()));
		setTreeCellRenderer(new ToolsMenuPanelTreeCellRenderer());

		getTree().setRootVisible(false);
		getTree().setDragEnabled(true);
		getTree().addMouseListener(new FunctionPanelMouseAdapter());
	}

	public void expandAll() {
		for (int i = 0; i < getTree().getRowCount(); i++) {
			getTree().expandRow(i);
		}
	}

	public void refresh() {
		((ToolsMenuPanelTreeModel) getTree().getModel()).refresh();
	}

	private String[] fromClassNameToDescriptionAndSqlOrder(
			final ClassName className) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		final Object newInstance = Class.forName(className.getValue().trim())
				.newInstance();
		if (newInstance instanceof Function) {
			return new String[] { ((Function) newInstance).getDescription(),
					((Function) newInstance).getSqlOrder() + EOL };
		} else if (newInstance instanceof CustomQuery) {
			return new String[] { ((CustomQuery) newInstance).getDescription(),
					((CustomQuery) newInstance).getSqlOrder() + EOL };
		}
		return null;
	}

	private String[] fromMenuItemToDescriptionAndSqlOrder(
			final MenuItem menuItem) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		final ClassName className = menuItem.getClassName();

		if (null == className) {
			final List<SqlInstr> sqlInstrList = menuItem.getSqlBlock()
					.getSqlInstr();
			final StringBuilder sb = new StringBuilder();
			for (SqlInstr sqlInstr : sqlInstrList) {
				sb.append(sqlInstr.getValue()).append(EOL);
			}
			return new String[] {
					menuItem.getSqlBlock().getComment().getValue(),
					sb.toString() };
		} else {
			return fromClassNameToDescriptionAndSqlOrder(className);
		}
	}

	private class FunctionPanelMouseAdapter extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			final Object selectedNode = getTree()
					.getLastSelectedPathComponent();

			if (selectedNode instanceof MenuItem) {
				final MenuItem menuItem = (MenuItem) selectedNode;
				String[] tmp;
				try {
					tmp = fromMenuItemToDescriptionAndSqlOrder(menuItem);
					descriptionScrollPane.getJTextArea().setText(tmp[0]);
				} catch (InstantiationException ex) {
					ex.printStackTrace();
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				}
			} else {
				descriptionScrollPane.getJTextArea().setText(null);
			}
		}
	}

	@Override
	protected boolean doDrop(Transferable trans, Object node) {
		// TODO Auto-generated method stub
		return false;
	}

	private MenuItem[] getSelectedResources() {
		TreePath[] paths = getTree().getSelectionPaths();
		if (paths == null) {
			return new MenuItem[0];
		} else {
			final List<MenuItem> listOfMenuItems = new ArrayList<MenuItem>();
			for (TreePath menuEntryPath : paths) {
				final Object menuEntry = menuEntryPath.getLastPathComponent();
				if (menuEntry instanceof MenuItem) {
					listOfMenuItems.add((MenuItem) menuEntry);
				}
			}
			return listOfMenuItems.toArray(new MenuItem[0]);
		}
	}

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		final MenuItem[] resources = getSelectedResources();
		if (resources.length > 0) {
			final StringBuilder sb = new StringBuilder();
			for (MenuItem menuItem : resources) {
				try {
					final String[] tmp = fromMenuItemToDescriptionAndSqlOrder(menuItem);
					sb.append(tmp[1]);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			return new StringSelection(sb.toString());
		}
		return null;
	}

	@Override
	public JPopupMenu getPopup() {
		// TODO Auto-generated method stub
		return null;
	}
}