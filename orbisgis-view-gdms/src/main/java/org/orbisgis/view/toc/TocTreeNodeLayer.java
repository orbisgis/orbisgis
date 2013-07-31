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
package org.orbisgis.view.toc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Logger;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.utils.CollectionUtils;
import org.orbisgis.view.components.resourceTree.EnumIterator;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The decorator for a tree node layer
 */
public class TocTreeNodeLayer implements MutableTreeNode {
        private ILayer layer;
        protected final static I18n I18N = I18nFactory.getI18n(TocTreeNodeLayer.class);
        private static final Logger LOGGER = Logger.getLogger(TocTreeNodeLayer.class);

        public TocTreeNodeLayer(ILayer layer) {
                if(layer==null) {
                    throw new IllegalArgumentException("TreeNode instanced with null ILayer");
                }
                this.layer = layer;
        }

     
        @Override
        public boolean equals(Object obj) {
                if (obj instanceof TocTreeNodeLayer) {
                        final TocTreeNodeLayer other = (TocTreeNodeLayer) obj;
                        return this.layer != null && this.layer.equals(other.layer);
                } else {
                        return false;
                }
        }

        @Override
        public int hashCode() {
                return layer.hashCode();
        }
        
        /**
         * The core layer model
         * @return 
         */
        public ILayer getLayer() {
                return layer;
        }     
                
        @Override
        public TreeNode getChildAt(int i) {
                if(layer.acceptsChilds()) {
                        return new TocTreeNodeLayer(layer.getLayer(i));
                } else {
                        //We don't want the style drawn on the top of the map to
                        //be displayed as the last style in the tree...
                        int s = layer.getStyles().size();
                        return new TocTreeNodeStyle(layer.getStyle(s-i-1));
                }
        }

        @Override
        public int getChildCount() {
                if (layer.acceptsChilds()) {
                        return layer.getLayerCount();
                } else {
                        return layer.getStyles().size();
                }
        }

        @Override
        public TreeNode getParent() {
                if(layer.getParent()!=null) {
                        return new TocTreeNodeLayer(layer.getParent());
                } else {
                        return null;
                }
        }

        @Override
        public int getIndex(TreeNode tn) {
                if(tn instanceof TocTreeNodeLayer) {
                        return CollectionUtils.indexOf(layer.getChildren(), ((TocTreeNodeLayer)tn).getLayer());
                } else {
                        //We don't want the style drawn on the top of the map to
                        //be displayed as the last style in the tree...
                        List<Style> s = layer.getStyles();
                        int ind = s.size();
                        return ind - 1 - s.indexOf(((TocTreeNodeStyle)tn).getStyle());
                }
        }

        @Override
        public boolean getAllowsChildren() {
                return true;
        }

        @Override
        public boolean isLeaf() {
                return false;
        }

        @Override
        public Enumeration<TreeNode> children() {
                List<TreeNode> nodes = new ArrayList<TreeNode>();
                for(int i=0;i<getChildCount();i++) {
                        nodes.add(getChildAt(i));
                }
                return new EnumIterator<TreeNode>(nodes.iterator());
        }

        @Override
        public void insert(MutableTreeNode mtn, int i) {
        }

        @Override
        public void remove(int i) {
        }

        @Override
        public void remove(MutableTreeNode mtn) {
        }

        @Override
        public void setUserObject(Object o) {
                try {
                        String label = o.toString();
                        //User change the layer label
                        if(label.isEmpty()) {
                                layer.setName(layer.getDataSource().getName());
                        } else {
                                layer.setName(label);
                        }
                } catch (LayerException ex) {
                        LOGGER.error(I18N.tr("Cannot change the layer name"), ex);
                }
        }

        @Override
        public void removeFromParent() {
        }

        @Override
        public void setParent(MutableTreeNode mtn) {
        }        
}
