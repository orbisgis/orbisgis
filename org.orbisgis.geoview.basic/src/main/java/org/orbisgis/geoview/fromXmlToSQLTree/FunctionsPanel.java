package org.orbisgis.geoview.fromXmlToSQLTree;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.xml.bind.JAXBException;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.core.resourceTree.ResourceTree;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.Register;
import org.orbisgis.geoview.basic.persistence.Menu;
import org.orbisgis.geoview.basic.persistence.MenuItem;
import org.orbisgis.geoview.basic.persistence.SqlInstr;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;

public class FunctionsPanel extends ResourceTree {
	private DescriptionScrollPane descriptionScrollPane;
	private JTextArea sqlConsoleJTextArea;
	private Menu rootMenu;

	public FunctionsPanel(final GeoView2D geoview,
			final DescriptionScrollPane descriptionScrollPane)
			throws JAXBException {
		SQLConsolePanel sqlConsole = (SQLConsolePanel) geoview
				.getView("org.orbisgis.geoview.SQLConsole");
		sqlConsoleJTextArea = sqlConsole.getScrollPanelWest().getJTextArea();

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

	private class FunctionPanelMouseAdapter extends MouseAdapter {
		private final String EOL = System.getProperty("line.separator");

		public void mouseClicked(MouseEvent e) {
			final Object selectedNode = getTree()
					.getLastSelectedPathComponent();

			if (selectedNode instanceof MenuItem) {
				final MenuItem menuItem = (MenuItem) selectedNode;

				if (null != menuItem.getClassName()) {
					final String className = menuItem.getClassName().getValue()
							.trim();
					try {
						final Object newInstance = Class.forName(className)
								.newInstance();
						if (newInstance instanceof Function) {
							descriptionScrollPane.getJTextArea().setText(
									((Function) newInstance).getDescription());
							if (e.getClickCount() == 2) {
								final String query = ((Function) newInstance)
										.getSqlOrder();
								final int position = sqlConsoleJTextArea
										.getCaretPosition();
								sqlConsoleJTextArea.insert(query, position);
								// Replace the cursor at end of line
								sqlConsoleJTextArea.requestFocus();
							}
						} else {
							descriptionScrollPane.getJTextArea().setText(
									((CustomQuery) newInstance)
											.getDescription());
							if (e.getClickCount() == 2) {
								final String query = ((CustomQuery) newInstance)
										.getSqlOrder();
								final int position = sqlConsoleJTextArea
										.getCaretPosition();
								sqlConsoleJTextArea.insert(query, position);
								// Replace the cursor at end of line
								sqlConsoleJTextArea.requestFocus();
							}
						}
					} catch (InstantiationException exception) {
						exception.printStackTrace();
					} catch (IllegalAccessException exception) {
						exception.printStackTrace();
					} catch (ClassNotFoundException exception) {
						exception.printStackTrace();
					}
				} else {
					descriptionScrollPane.getJTextArea().setText(
							menuItem.getSqlBlock().getComment().getValue());

					if (e.getClickCount() == 2) {
						final StringBuilder sb = new StringBuilder();
						for (SqlInstr sqlInstr : menuItem.getSqlBlock()
								.getSqlInstr()) {
							sb.append(sqlInstr.getValue()).append(EOL);
						}
						final int position = sqlConsoleJTextArea
								.getCaretPosition();
						sqlConsoleJTextArea.insert(sb.toString(), position);
						// Replace the cursor at end line
						sqlConsoleJTextArea.requestFocus();
					}
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

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JPopupMenu getPopup() {
		// TODO Auto-generated method stub
		return null;
	}
}