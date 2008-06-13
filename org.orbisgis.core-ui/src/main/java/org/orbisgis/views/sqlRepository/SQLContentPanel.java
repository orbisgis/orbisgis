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
package org.orbisgis.views.sqlRepository;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.function.Function;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SQLProcessor;
import org.orbisgis.DataManager;
import org.orbisgis.Services;
import org.orbisgis.ui.resourceTree.ResourceTree;
import org.orbisgis.views.sqlRepository.persistence.Category;
import org.orbisgis.views.sqlRepository.persistence.SqlInstruction;
import org.orbisgis.views.sqlRepository.persistence.SqlScript;

public class SQLContentPanel extends ResourceTree {
	private DescriptionScrollPane descriptionScrollPane;

	public SQLContentPanel(final DescriptionScrollPane descriptionScrollPane,
			Category repositoryRoot) {
		this.descriptionScrollPane = descriptionScrollPane;

		setModel(new SQLRepositoryTreeModel(repositoryRoot, getTree()));
		setTreeCellRenderer(new SQLRepositoryTreeCellRenderer());
		getTree().setEditable(false);
		getTree().setRootVisible(false);
		getTree().setDragEnabled(true);
		getTree().addMouseListener(new FunctionPanelMouseAdapter());
		getTree().getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	public void expandAll() {
		for (int i = 0; i < getTree().getRowCount(); i++) {
			getTree().expandRow(i);
		}
	}

	private String getComment(final Object sqlContent)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, ParseException {
		if (sqlContent instanceof SqlScript) {
			SqlScript script = (SqlScript) sqlContent;
			String scriptContent = getResourceContent(script.getResource());
			SQLProcessor pr = new SQLProcessor(((DataManager) Services
					.getService("org.orbisgis.DataManager")).getDSF());
			String comment = pr.getScriptComment(scriptContent);
			if (comment != null) {
				return comment;
			} else {
				return "No description for this script";
			}
		} else if (sqlContent instanceof SqlInstruction) {
			SqlInstruction instruction = (SqlInstruction) sqlContent;
			final Object newInstance = Class.forName(instruction.getClazz())
					.newInstance();
			if (newInstance instanceof Function) {
				return ((Function) newInstance).getDescription();
			} else if (newInstance instanceof CustomQuery) {
				return ((CustomQuery) newInstance).getDescription();
			} else {
				throw new RuntimeException("bug");
			}
		} else {
			throw new RuntimeException("bug!");
		}
	}

	private String getContent(final Object sqlContent)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException, ParseException {
		if (sqlContent instanceof SqlScript) {
			SqlScript script = (SqlScript) sqlContent;
			String scriptContent = getResourceContent(script.getResource());
			SQLProcessor pr = new SQLProcessor(((DataManager) Services
					.getService("org.orbisgis.DataManager")).getDSF());
			return pr.getScriptBody(scriptContent);
		} else if (sqlContent instanceof SqlInstruction) {
			SqlInstruction instruction = (SqlInstruction) sqlContent;
			final Object newInstance = Class.forName(instruction.getClazz())
					.newInstance();
			if (newInstance instanceof Function) {
				return ((Function) newInstance).getSqlOrder();
			} else if (newInstance instanceof CustomQuery) {
				return ((CustomQuery) newInstance).getSqlOrder();
			} else {
				throw new RuntimeException("bug");
			}
		} else {
			throw new RuntimeException("bug!");
		}
	}

	private String getResourceContent(String resource) throws IOException {
		InputStream is = SQLContentPanel.class.getResource(resource)
				.openStream();
		DataInputStream dis = new DataInputStream(is);
		byte[] content = new byte[dis.available()];
		dis.readFully(content);
		dis.close();
		return new String(content);
	}

	private class FunctionPanelMouseAdapter extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			final Object selectedNode = getTree()
					.getLastSelectedPathComponent();

			if ((selectedNode != null) && (!(selectedNode instanceof Category))) {
				try {
					descriptionScrollPane.getJTextArea().setText(
							getComment(selectedNode));
				} catch (InstantiationException ex) {
					Services.getErrorManager().warning(
							"Cannot read the script description", ex);
				} catch (IllegalAccessException ex) {
					Services.getErrorManager().warning(
							"Cannot read the script description", ex);
				} catch (ClassNotFoundException ex) {
					Services.getErrorManager().warning(
							"Cannot read the script description", ex);
				} catch (IOException ex) {
					Services.getErrorManager().warning(
							"Cannot read the script description", ex);
				} catch (ParseException ex) {
					Services.getErrorManager().warning(
							"Cannot parse the script "
									+ ((SqlScript) selectedNode).getResource(),
							ex);
					descriptionScrollPane.getJTextArea().setText(
							"Cannot parse script");
				}
			} else {
				descriptionScrollPane.getJTextArea().setText(null);
			}
		}
	}

	@Override
	protected boolean doDrop(Transferable trans, Object node) {
		return false;
	}

	private Object getSelectedResources() {
		TreePath path = getTree().getSelectionPath();
		if (path == null) {
			return null;
		} else {
			return path.getLastPathComponent();
		}
	}

	@Override
	protected Transferable getDragData(DragGestureEvent dge) {
		Object resource = getSelectedResources();
		if (resource != null) {
			if (resource instanceof Category) {
				return null;
			} else {
				try {
					return new StringSelection(getContent(resource));
				} catch (InstantiationException e) {
					Services.getErrorManager().warning(
							"Cannot read the script description", e);
				} catch (IllegalAccessException e) {
					Services.getErrorManager().warning(
							"Cannot read the script description", e);
				} catch (ClassNotFoundException e) {
					Services.getErrorManager().warning(
							"Cannot read the script description", e);
				} catch (IOException e) {
					Services.getErrorManager().warning(
							"Cannot read the script description", e);
				} catch (ParseException ex) {
					Services.getErrorManager().warning(
							"Cannot parse the script "
									+ ((SqlScript) resource).getResource(), ex);
				}
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public JPopupMenu getPopup() {
		return null;
	}
}