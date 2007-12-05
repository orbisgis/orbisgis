package org.urbsat.plugin.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.xml.bind.JAXBException;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.toolsMenuPanel.jaxb.MenuItem;
import org.orbisgis.toolsMenuPanel.jaxb.SqlInstr;

public class UrbSATFunctionsPanel extends JPanel {
	private DescriptionScrollPane descriptionScrollPane;
	private JTree jTree;
	private JTextArea sqlConsoleJTextArea;

	public UrbSATFunctionsPanel(final GeoView2D geoview,
			final DescriptionScrollPane descriptionScrollPane)
			throws JAXBException {
		SQLConsolePanel sqlConsole = (SQLConsolePanel) geoview
				.getView("org.orbisgis.geoview.SQLConsole");
		sqlConsoleJTextArea = sqlConsole.getScrollPanelWest().getJTextArea();

		this.descriptionScrollPane = descriptionScrollPane;

		jTree = new JTree(new UrbSATTreeModel(UrbSATTreeModel.class
				.getResource("urbsat.xml")));

		final UrbSATTreeCellRenderer treeCellRenderer = new UrbSATTreeCellRenderer();
		jTree.setCellRenderer(treeCellRenderer);

		expandAll();

		jTree.setRootVisible(false);
		// jTree.setDragEnabled(true);
		jTree.addMouseListener(new UrbSATMouseAdapter());

		add(jTree);
	}

	private void expandAll() {
		for (int i = 0; i < jTree.getRowCount(); i++) {
			jTree.expandRow(i);
		}
	}

	private class UrbSATMouseAdapter extends MouseAdapter {
		private final String EOL = System.getProperty("line.separator");

		public void mouseClicked(MouseEvent e) {
			final Object selectedNode = jTree.getLastSelectedPathComponent();

			if (selectedNode instanceof MenuItem) {
				final MenuItem menuItem = (MenuItem) selectedNode;

				if (null != menuItem.getClassName()) {
					final String className = menuItem.getClassName()
							.getContent();
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
								// Replace the cursor at end line
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
								// Replace the cursor at end line
								sqlConsoleJTextArea.requestFocus();
							}
						}
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				} else {
					descriptionScrollPane.getJTextArea().setText(
							menuItem.getSqlBlock().getComment().getContent());

					if (e.getClickCount() == 2) {
						final StringBuilder sb = new StringBuilder();
						for (SqlInstr sqlInstr : menuItem.getSqlBlock()
								.getSqlInstr()) {
							sb.append(sqlInstr.getContent()).append(EOL);
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
}