/*
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
package org.orbisgis.geocatalogtree.impl.nodes;

import org.jooq.Catalog;
import org.jooq.Field;
import org.jooq.Meta;
import org.jooq.QueryPart;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNodeImpl;
import org.orbisgis.geocatalogtree.api.TreeNodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode.*;

/**
 * Create and update nodes
 * @author Nicolas Fortin
 */
public class TreeNodeFactoryImpl implements TreeNodeFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(TreeNodeFactoryImpl.class);
    private static I18n I18N = I18nFactory.getI18n(TreeNodeFactoryImpl.class);

    @Override
    public String[] getParentNodeType() {
        return new String[]{NODE_DATABASE, NODE_CATALOG, NODE_SCHEMA, NODE_TABLE, NODE_COLUMNS};
    }

    @Override
    public void updateChildren(GeoCatalogTreeNode parent, Connection connection, JTree jTree) throws SQLException {
        updateChildren(parent, getJooqQueryPart(connection,null, parent),connection, jTree);
    }


    public QueryPart getJooqQueryPart(Connection connection,QueryPart parentQueryPart, GeoCatalogTreeNode treeNode) {
        if(treeNode == null) {
            LOGGER.error("Cant find root catalog");
            return null;
        }
        if(parentQueryPart == null) {
            if(treeNode.getNodeType().equals(NODE_CATALOG)) {
                return getCatalog(DSL.using(connection).meta(), treeNode.getNodeIdentifier());
            } else {
                parentQueryPart = getJooqQueryPart(connection, null, treeNode.getParent());
            }
        }
        if(parentQueryPart != null) {
            switch (treeNode.getNodeType()) {
                case NODE_SCHEMA:
                    return ((Catalog) parentQueryPart).getSchema(treeNode.getNodeIdentifier());
                case NODE_TABLE:
                    return ((Schema) parentQueryPart).getTable(treeNode.getNodeIdentifier());
                case NODE_COLUMNS:
                    return parentQueryPart;
            }
        }
        return null;
    }

    /**
     * Load sub-nodes
     * @param parent Parent node to fill or update
     * @param parentQueryPart Parent node JOOQ instance
     * @param connection Active connection
     * @param jTree JTree that will receive items
     * @throws SQLException
     */
    public void updateChildren(GeoCatalogTreeNode parent, QueryPart parentQueryPart,Connection connection, JTree jTree) throws SQLException {
        DefaultTreeModel treeModel = (DefaultTreeModel)jTree.getModel();
        if(parent.getNodeType().isEmpty()) {
            loadDatabase(DSL.using(connection).meta(), treeModel);
            return;
        }
        List<GeoCatalogTreeNodeImpl> allNodes = new ArrayList<>(parent.getChildCount());
        List<QueryPart> allNodesQueryPart = new ArrayList<>(parent.getChildCount());
        // Read Meta and create all nodes
        switch (parent.getNodeType()) {
            case NODE_DATABASE:
                loadCatalog(parent, DSL.using(connection).meta(), treeModel,allNodes, allNodesQueryPart);
                break;
            case NODE_CATALOG:
                loadSchema((Catalog)parentQueryPart,allNodes, allNodesQueryPart);
                break;
            case NODE_SCHEMA:
                loadTable((Schema)parentQueryPart,allNodes, allNodesQueryPart);
                break;
            case NODE_TABLE:
                allNodes.add(new GeoCatalogTreeNodeImpl(this, NODE_COLUMNS, I18N.tr("Columns")));
                allNodesQueryPart.add(parentQueryPart);
                break;
            case NODE_COLUMNS:
                loadFields((Table)parentQueryPart, allNodes, allNodesQueryPart);
                break;
        }
        Map<String, GeoCatalogTreeNode> oldNodes = parent.getChildrenIdentifier();
        // Check if the node already exist in the tree or if old nodes has been removed from db
        for(int nodeId=0; nodeId < allNodes.size(); nodeId++) {
            GeoCatalogTreeNodeImpl node=allNodes.get(nodeId);
            QueryPart nodeQueryPart = allNodesQueryPart.get(nodeId);
            GeoCatalogTreeNode existingNode = oldNodes.get(node.getNodeIdentifier());
            if(existingNode != null) {
                // Node already exists
                oldNodes.remove(node.getNodeIdentifier());
                // Update the node if it contains at least one child
                if(existingNode.getChildCount() > 0) {
                    TreeNodeFactory treeNodeFactory = existingNode.getFactory();
                    if(treeNodeFactory instanceof TreeNodeFactoryImpl) {
                        TreeNodeFactoryImpl childFactory = (TreeNodeFactoryImpl)treeNodeFactory;
                        childFactory.updateChildren(node, nodeQueryPart, connection, jTree);
                    } else if(treeNodeFactory != null) {
                        treeNodeFactory.updateChildren(node, connection, jTree);
                    }
                }
            } else {
                // New node, add it in the tree
                treeModel.insertNodeInto(node, parent, parent.getChildCount());
            }
        }
        if(!oldNodes.isEmpty()) {
            // Removed nodes
            for(GeoCatalogTreeNode node : oldNodes.values()) {
                treeModel.removeNodeFromParent(node);
            }
        }

    }

    /**
     * @param meta JOOQ meta
     * @param catalogName Catalog name
     * @return Catalog or null
     */
    public Catalog getCatalog(Meta meta, String catalogName) {
        for(Catalog catalog : meta.getCatalogs()) {
            if(catalogName.equals(catalog.getName())) {
                return catalog;
            }
        }
        return null;
    }

    /**
     * Load root node or catalog as root if only one catalog in db
     * @param meta JOOQ meta
     * @param treeModel JTree model
     */
    public void loadDatabase(Meta meta, DefaultTreeModel treeModel) {
        List<Catalog> catalogs = meta.getCatalogs();
        if(catalogs.size() == 1) {
            loadCatalog(null, meta, treeModel, new ArrayList<GeoCatalogTreeNodeImpl>(), new ArrayList<QueryPart>());
        } else {
            treeModel.setRoot(new GeoCatalogTreeNodeImpl(this, NODE_DATABASE, "root"));
        }
    }

    private void loadCatalog(GeoCatalogTreeNode parent, Meta meta, DefaultTreeModel treeModel, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart) {
        if(parent == null) {
            treeModel.setRoot(new GeoCatalogTreeNodeImpl(this, NODE_CATALOG, meta.getCatalogs().get(0).getName()));
        } else {
            for(Catalog catalog : meta.getCatalogs()) {
                nodes.add(new GeoCatalogTreeNodeImpl(this, NODE_CATALOG, catalog.getName()));
                nodesQueryPart.add(catalog);
            }
        }
    }

    private void loadSchema(Catalog catalog, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart) {
        if(catalog != null) {
            for (Schema schema : catalog.getSchemas()) {
                nodes.add(new GeoCatalogTreeNodeImpl(this, NODE_SCHEMA, schema.getName()));
                nodesQueryPart.add(schema);
            }
        }
    }

    private void loadTable(Schema schema, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart) {
        if(schema != null) {
            for (Table table : schema.getTables()) {
                nodes.add(new GeoCatalogTreeNodeImpl(this, NODE_TABLE, table.getName()));
                nodesQueryPart.add(table);
            }
        }
    }
    private void loadFields(Table table, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart) {
        if(table != null) {
            for(Field field : table.fields()) {
                GeoCatalogTreeNodeImpl fieldNode = new GeoCatalogTreeNodeImpl(this, NODE_COLUMN, field.getName());
                fieldNode.setAllowsChildren(false);
                nodes.add(fieldNode);
                nodesQueryPart.add(field);
            }
        }
    }
}

