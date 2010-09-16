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
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.orbisgis.core.images.OrbisGISIcon;
import org.orbisgis.core.renderer.se.AreaSymbolizer;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.LineSymbolizer;
import org.orbisgis.core.renderer.se.PanelableNode;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.RasterSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.core.renderer.se.TextSymbolizer;

/**
 *
 * Is used to show rule symbolizers
 *
 * @author maxence
 */
public final class EditSymbolizerListPanel extends JPanel implements TreeSelectionListener {

	private EditFeatureTypeStylePanel ftsPanel;
	private JTree sTree;
	private Rule rule;
	private JButton sUp;
	private JButton sDown;
	private JButton sAdd;
	private JButton sRm;
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;

	private class CellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf,
				int row, boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

			Object symb = ((DefaultMutableTreeNode) value).getUserObject();

			if (symb instanceof AreaSymbolizer) {
				setIcon(OrbisGISIcon.LAYER_POLYGON);
			} else if (symb instanceof LineSymbolizer) {
				setIcon(OrbisGISIcon.LAYER_LINE);
			} else if (symb instanceof PointSymbolizer) {
				setIcon(OrbisGISIcon.LAYER_POINT);
			} else if (symb instanceof TextSymbolizer) {
				setIcon(OrbisGISIcon.PENCIL);
			} else if (symb instanceof RasterSymbolizer) {
				setIcon(OrbisGISIcon.LAYER_RGB);
			}

			return this;
		}
	}

	public EditSymbolizerListPanel(EditFeatureTypeStylePanel parent, Rule r) {
		super(new BorderLayout());
		this.ftsPanel = parent;
		this.rule = r;

		if (rule != null) {
			root = new DefaultMutableTreeNode("Symbolizers");
			DefaultMutableTreeNode sNode;
			for (Symbolizer s : rule.getCompositeSymbolizer().getSymbolizerList()) {
				sNode = new DefaultMutableTreeNode(s);
				root.add(sNode);
			}

			treeModel = new DefaultTreeModel(root);

			sTree = new JTree(treeModel);

			sTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			sTree.addTreeSelectionListener(this);
			sTree.setRootVisible(false);
			sTree.setCellRenderer(new CellRenderer());


			this.add(sTree, BorderLayout.NORTH);

			JPanel toolbar = new JPanel();

			sUp = new JButton(OrbisGISIcon.GO_UP);
			sDown = new JButton(OrbisGISIcon.GO_DOWN);
			sAdd = new JButton(OrbisGISIcon.ADD);
			sRm = new JButton(OrbisGISIcon.REMOVE);

			sUp.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();
					if (node.getUserObject() instanceof Symbolizer && node.getPreviousSibling() != null) {
						int currentIndex = node.getParent().getIndex(node);
						MutableTreeNode p = (MutableTreeNode) node.getParent();
						treeModel.removeNodeFromParent(node);
						treeModel.insertNodeInto(node, p, currentIndex - 1);

						Symbolizer s = (Symbolizer) node.getUserObject();
						CompositeSymbolizer parent = (CompositeSymbolizer) s.getParent();
						parent.moveSymbolizerUp(s);
					}
				}
			});



			sDown.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();
					if (node.getUserObject() instanceof Symbolizer && node.getNextSibling() != null) {
						int currentIndex = node.getParent().getIndex(node);
						MutableTreeNode p = (MutableTreeNode) node.getParent();
						treeModel.removeNodeFromParent(node);
						treeModel.insertNodeInto(node, p, currentIndex + 1);

						Symbolizer s = (Symbolizer) node.getUserObject();
						CompositeSymbolizer parent = (CompositeSymbolizer) s.getParent();
						parent.moveSymbolizerDown(s);

					}
				}
			});

			sRm.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();
					if (node.getUserObject() instanceof Symbolizer) {

						Symbolizer s = (Symbolizer) node.getUserObject();

						// Remove from tree
						treeModel.removeNodeFromParent(node);
						// Remove from rule
						CompositeSymbolizer parent = (CompositeSymbolizer) s.getParent();
						parent.removeSymbolizer(s);
						// Show the rule editor
						ftsPanel.editNode(rule);
					}
				}
			});


			sAdd.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object[] possibilities = ftsPanel.getAvailableSymbolizerList();
					String defaultChoice = ftsPanel.getDefaultType();

					String s = (String) JOptionPane.showInputDialog(null,
							"Choose a new type", "Choose a new type",
							JOptionPane.PLAIN_MESSAGE, null,
							possibilities, defaultChoice);

					Symbolizer newSymb;

					if (s != null) {
						if (s.equals("Area Symbolizer")) {
							newSymb = new AreaSymbolizer();
						} else if (s.equals("Line Symbolizer")) {
							newSymb = new LineSymbolizer();
						} else if (s.equals("Point Symbolizer")) {
							newSymb = new PointSymbolizer();
						} else if (s.equals("Text Symbolizer")) {
							newSymb = new TextSymbolizer();
						} else {
							newSymb = null;
						}
					} else {
						newSymb = null;
					}

					if (newSymb != null) {
						rule.getCompositeSymbolizer().addSymbolizer(newSymb);
						addItem(newSymb, null);
					}
				}
			});

			sUp.setEnabled(false);
			sDown.setEnabled(false);
			sRm.setEnabled(false);

			toolbar.add(sUp);
			toolbar.add(sAdd);
			toolbar.add(sRm);
			toolbar.add(sDown);

			this.add(toolbar, BorderLayout.SOUTH);
		} else {
			this.add(new JLabel("Please select a rule"));
		}
	}

	private DefaultMutableTreeNode getNode(PanelableNode node) {
		for (Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements();) {
			DefaultMutableTreeNode current = (DefaultMutableTreeNode) e.nextElement();
			if (node.equals(current.getUserObject())) {
				return current;
			}
		}
		return null;
	}

	public void addItem(PanelableNode node, PanelableNode parent) {
		DefaultMutableTreeNode item = new DefaultMutableTreeNode(node);
		if (parent == null) {
			// means that node will be added at top level
			treeModel.insertNodeInto(item, root, root.getChildCount());
			sTree.setSelectionRow(sTree.getRowCount() - 1);
		} else {
			// means node is a child of parent, so first look for parent within the tree
			DefaultMutableTreeNode parentNode = getNode(parent);

			if (parentNode != null) {
				treeModel.insertNodeInto(item, parentNode, parentNode.getChildCount());
			} else {
				System.out.println("Could not attach new child to its parent !!!");
			}
		}
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();

		sUp.setEnabled(false);
		sDown.setEnabled(false);
		sRm.setEnabled(false);

		if (node == null) {
			System.out.println("Selection is empty !");
		} else {
			System.out.println("Selected node is: " + node + "(" + node.getClass() + ")");
			Object obj = node.getUserObject();
			System.out.println(" --> node is A: " + obj.getClass());

			if (obj instanceof Symbolizer) {
				if (this.rule.getCompositeSymbolizer().getSymbolizerList().size() > 1) {
					sRm.setEnabled(true);
				}

				if (node.getNextSibling() != null) {
					sDown.setEnabled(true);
				}

				if (node.getPreviousSibling() != null) {
					sUp.setEnabled(true);
				}
			}

			if (obj instanceof PanelableNode) {
				this.ftsPanel.editNode((PanelableNode) obj);
			}
		}
	}
}
