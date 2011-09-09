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
package org.orbisgis.core.ui.editorViews.toc;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.gdms.driver.DriverException;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeModel;

public class TocTreeModel extends AbstractTreeModel {

	private final ILayer root;

	public TocTreeModel(ILayer root, JTree tree) {
		super(tree);
		this.root = root;
	}

	public void refresh() {
		fireEvent(new TreePath(root));
	}

	@Override
	public Object getChild(Object parent, int index) {
		ILayer l = (ILayer) parent;
		if (l.acceptsChilds()) {
			return l.getChildren()[index];
		} else {
			return new RuleNode(l, index);
		}
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ILayer) {
			ILayer layer = (ILayer) parent;
			if (layer.acceptsChilds()) {
				return layer.getChildren().length;
			} else {
				return layer.getFeatureTypeStyle().getRules().size();
			}
		} else {
			return 0;
		}
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof ILayer) {
			if (child instanceof RuleNode) {
				return ((RuleNode) child).getRuleIndex();
			} else {
				return ((ILayer) parent).getIndex((ILayer) child);
			}
		} else {
			return 0;
		}
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		if (node instanceof ILayer) {
			ILayer layer = (ILayer) node;
			return layer.acceptsChilds() && (layer.getChildren().length == 0);
		} else {
			return true;
		}
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	class RuleNode {

		private ILayer layer;
		private int ruleIndex;
		private Rule rule;

		public RuleNode(ILayer layer, int ruleIndex) {
			this.layer = layer;
			this.ruleIndex = ruleIndex;
			try {
				rule = layer.getRenderingRule().get(ruleIndex);
			} catch (DriverException ex) {
				Logger.getLogger(TocTreeModel.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		public ILayer getLayer() {
			return layer;
		}

		public int getRuleIndex() {
			return ruleIndex;
		}

		public Rule getRule(){
			return rule;
		}
	}
}
