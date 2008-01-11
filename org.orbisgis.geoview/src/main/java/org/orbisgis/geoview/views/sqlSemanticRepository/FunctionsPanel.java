/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 licence. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlSemanticRepository;

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
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.ClassName;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.MenuItem;
import org.orbisgis.geoview.views.sqlSemanticRepository.persistence.SqlInstr;

public class FunctionsPanel extends ResourceTree {
	private final static String EOL = System.getProperty("line.separator");
	private DescriptionScrollPane descriptionScrollPane;

	public FunctionsPanel(final DescriptionScrollPane descriptionScrollPane)
			throws JAXBException {
		this.descriptionScrollPane = descriptionScrollPane;

		setModel(new ToolsMenuPanelTreeModel(
				EPSQLSemanticRepositoryHelper.install(), getTree()));
		setTreeCellRenderer(new ToolsMenuPanelTreeCellRenderer());
		getTree().setEditable(false);
		getTree().setRootVisible(false);
		getTree().setDragEnabled(true);
		getTree().addMouseListener(new FunctionPanelMouseAdapter());
	}

	public void expandAll() {
		for (int i = 0; i < getTree().getRowCount(); i++) {
			getTree().expandRow(i);
		}
	}

	public static String[] fromClassNameToDescriptionAndSqlOrder(
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

	public static String[] fromMenuItemToDescriptionAndSqlOrder(
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