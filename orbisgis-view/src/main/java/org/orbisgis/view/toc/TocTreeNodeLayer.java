/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.toc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.utils.CollectionUtils;

/**
 * The decorator for a tree node layer
 */
public class TocTreeNodeLayer implements TreeNode {
        private ILayer layer;
        

        public TocTreeNodeLayer(ILayer layer) {
                this.layer = layer;
        }

     

        @Override
        public boolean equals(Object obj) {
                if (obj == null) {
                        return false;
                }
                if (getClass() != obj.getClass()) {
                        return false;
                }
                final TocTreeNodeLayer other = (TocTreeNodeLayer) obj;
                if (this.layer != other.layer && (this.layer == null || !this.layer.equals(other.layer))) {
                        return false;
                }
                return true;
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
                        return new TocTreeNodeStyle(layer.getStyle(i));
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
                        return layer.indexOf(((TocTreeNodeStyle)tn).getStyle());
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
        public Enumeration children() {
                List<TreeNode> nodes = new ArrayList<TreeNode>();
                for(int i=0;i<getChildCount();i++) {
                        nodes.add(getChildAt(i));
                }
                return new NodeEnumeration(nodes.iterator());
        }
        /**
         * The interface need an enumeration,
         * this class is provide to convert an iterator to an enumeration
         * If such class exists already in java please replace it
         */
        private class NodeEnumeration implements Enumeration {
                private Iterator it;

                public NodeEnumeration(Iterator it) {
                        this.it = it;
                }
                
                @Override
                public boolean hasMoreElements() {
                        return it.hasNext();
                }

                @Override
                public Object nextElement() {
                        return it.next();
                }
                
        }
}
