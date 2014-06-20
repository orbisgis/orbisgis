/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.h2gis.utilities.GeometryTypeCodes;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.view.components.renderers.IconCellRendererUtility;
import org.orbisgis.view.components.renderers.TreeLaFRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Toc renderer, try to stick to the Look&Feel but with custom controls.
 */
public class TocRenderer extends TreeLaFRenderer {
        private static Logger UILOGGER = Logger.getLogger("gui."+ TocRenderer.class);
        protected static final I18n I18N = I18nFactory.getI18n(TocRenderer.class);
        private static final int ROW_EMPTY_BORDER_SIZE = 2;
        private Rectangle checkBoxRect;
        private MapContext mapContext;
        private Map<String, Integer> tableGeomType = new HashMap<>();
        private AtomicBoolean fetchingTableIcons = new AtomicBoolean(false);
        private JTree tree;

        /**
         * Builds a TocRenderer using the given JTree.
         * @param tree JTree instance
         */
        public TocRenderer(JTree tree) {
                super(tree);
                this.tree = tree;
        }

        /**
         * Set the MapContext, used to check if a Layer is Active
         * @param mapContext Map context instance
         */
        public void setMapContext(MapContext mapContext) {
            this.mapContext = mapContext;
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
                Component nativeRendererComp = lookAndFeelRenderer.getTreeCellRendererComponent(
                        tree, value, selected, expanded, leaf, row, hasFocus); 
                if(nativeRendererComp instanceof JLabel) {
                        JLabel rendererComponent = (JLabel) nativeRendererComp;
                        try {
                                JPanel panel = new JPanel(new BorderLayout());
                                panel.setOpaque(false);
                                rendererComponent.setBorder(BorderFactory.createEmptyBorder(
                                        ROW_EMPTY_BORDER_SIZE,
                                        ROW_EMPTY_BORDER_SIZE,
                                        ROW_EMPTY_BORDER_SIZE,
                                        ROW_EMPTY_BORDER_SIZE));
                                JCheckBox checkBox = new JCheckBox();
                                IconCellRendererUtility.copyComponentStyle(rendererComponent,checkBox);
                                if (value instanceof TocTreeNodeLayer) {
                                        ILayer layerNode = ((TocTreeNodeLayer) value).getLayer();
                                        ImageIcon nodeIcon = getLayerIcon(layerNode);
                                        if(mapContext!=null && layerNode.equals(mapContext.getActiveLayer())) {
                                            nodeIcon = IconCellRendererUtility.mergeIcons
                                                    (nodeIcon, OrbisGISIcon.getIcon("edition/layer_edit"));
                                        }
                                        rendererComponent.setIcon(nodeIcon);
                                        String nodeLabel = layerNode.getName();
                                        
                                        if(!layerNode.getTableReference().isEmpty() &&
                                                !nodeLabel.equals(layerNode.getTableReference())) {
                                                nodeLabel = I18N.tr("Layer:{0} DataSource :({1})",nodeLabel,layerNode.getTableReference());
                                        }
                                        rendererComponent.setText(nodeLabel);
                                        
                                        
                                        checkBox.setSelected(layerNode.isVisible());
                                } else if(value instanceof TocTreeNodeStyle)  {
                                        Style styleNode = ((TocTreeNodeStyle) value).getStyle();
                                        rendererComponent.setIcon(OrbisGISIcon.getIcon("palette"));
                                        rendererComponent.setText(styleNode.getName());
                                        checkBox.setSelected(styleNode.isVisible());
                                }
                                panel.add(checkBox,BorderLayout.WEST);
                                panel.add(rendererComponent,BorderLayout.CENTER);
                                panel.doLayout();
                                checkBoxRect= new Rectangle(0, 0, checkBox.getPreferredSize().width, checkBox.getPreferredSize().height);
                                rendererComponent.setIcon(
                                        IconCellRendererUtility.mergeComponentAndIcon(
                                                checkBox,rendererComponent.getIcon()));
                        } catch (SQLException | IOException ex) {
                                UILOGGER.error(ex.getLocalizedMessage(), ex);
                        }
                }
                return nativeRendererComp;
	}

	public Rectangle getCheckBoxBounds() {
                if(checkBoxRect!=null) {
                        return checkBoxRect;
                } else {
                        return new Rectangle(0,0);
                }
	}

    protected void updateLayerNodes(Set<String> tableReferences) {
        if(tree.getModel() instanceof DefaultTreeModel) {
            DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
            ILayer[] layers = mapContext.getLayers();
            for (ILayer layer : layers) {
                if (tableReferences.contains(layer.getTableReference())) {
                    TocTreeNodeLayer node = new TocTreeNodeLayer(layer);
                    treeModel.nodeChanged(node);
                }
            }
        }
    }
    /**
     * Remove node icon cache for the provided layer.
     */
    public void clearTableIconCache() {
        clearTableIconCache(null);
    }

    /**
     * Remove node icon cache for the provided layer.
     * @param tableReference Table identifier or null to clear all table's icon.
     */
    public void clearTableIconCache(String tableReference) {
        if(tableReference == null) {
            tableGeomType = new HashMap<>(tableGeomType.size());
        } else {
            tableGeomType.remove(tableReference);
        }
    }

    public ImageIcon getLayerIcon(ILayer layer) throws SQLException,
            IOException {
        try {
            if (layer.acceptsChilds()) {
                return OrbisGISIcon.getIcon("layers");
            } else {
                if (layer.isStream()) {
                    return OrbisGISIcon.getIcon("server_connect");
                } else {
                    if(!tableGeomType.containsKey(layer.getTableReference())) {
                        tableGeomType.put(layer.getTableReference(), -2);
                        if(!fetchingTableIcons.getAndSet(true)) {
                            TableTypeFetcher tableTypeFetcher = new TableTypeFetcher(tableGeomType,
                                    mapContext.getDataManager().getDataSource(), this, fetchingTableIcons);
                            tableTypeFetcher.execute();
                        }
                    }
                    // Create a legend for each spatial field
                    int type = tableGeomType.get(layer.getTableReference());
                    if (type >= 0) {
                        switch (type) {
                            case GeometryTypeCodes.GEOMETRY:
                            case GeometryTypeCodes.GEOMCOLLECTION:
                                return OrbisGISIcon.getIcon("layermixe");
                            case GeometryTypeCodes.POINT:
                            case GeometryTypeCodes.MULTIPOINT:
                                return OrbisGISIcon.getIcon("layerpoint");
                            case GeometryTypeCodes.LINESTRING:
                            case GeometryTypeCodes.MULTILINESTRING:
                                return OrbisGISIcon.getIcon("layerline");
                            case GeometryTypeCodes.POLYGON:
                            case GeometryTypeCodes.MULTIPOLYGON:
                                return OrbisGISIcon.getIcon("layerpolygon");
                            default:
                                throw new RuntimeException(I18N.tr("Unable to find appropriate icon for typeCode {0}", type));
                        }
                    } else if(type == -2) {
                        return OrbisGISIcon.getIcon("information_geo");
                    } else {
                        return OrbisGISIcon.getIcon("remove");
                        // TODO Raster
                            /*
                            if (layer.getRaster().getType() == ImagePlus.COLOR_RGB) {
                                return OrbisGISIcon.getIcon("layerrgb");
                            } else {
                                return OrbisGISIcon.getIcon("raster");
                            }
                            */
                    }
                }
            }
        } catch (Exception ex) {
            // Error while reading datasource, may be a thread race condition or the table does not exists
            UILOGGER.trace(I18N.tr("Error while drawing the Toc tree"));
            return OrbisGISIcon.getIcon("remove");
        }
    }
    /**
     * Connect to JDBC in background in order to fetch table type icons
     */
    private class TableTypeFetcher extends SwingWorker {

        private Map<String, Integer> tableGeomType;
        private DataSource dataSource;
        private Set<String> fetchedTableIcons = new HashSet<>();
        private TocRenderer tocRenderer;
        AtomicBoolean fetchingTableIcons;

        private TableTypeFetcher(Map<String, Integer> tableGeomType, DataSource dataSource, TocRenderer tocRenderer, AtomicBoolean fetchingTableIcons) {
            this.tableGeomType = tableGeomType;
            this.dataSource = dataSource;
            this.tocRenderer = tocRenderer;
            this.fetchingTableIcons = fetchingTableIcons;
        }

        @Override
        protected void done() {
            tocRenderer.updateLayerNodes(fetchedTableIcons);
        }

        @Override
        protected Object doInBackground() throws Exception {
            try (Connection connection = dataSource.getConnection()) {
                fetchingTableIcons.set(false);
                for (String tableName : tableGeomType.keySet()) {
                    // Fetch icons where type is -1
                    int currentType = tableGeomType.get(tableName);
                    if(currentType < 0) {
                        int newType =  SFSUtilities.getGeometryType(connection, TableLocation.parse(tableName), "");
                        if(newType != currentType) {
                            tableGeomType.put(tableName, newType);
                            fetchedTableIcons.add(tableName);
                        }
                    }
                }
            }
            return null;
        }
    }
}
