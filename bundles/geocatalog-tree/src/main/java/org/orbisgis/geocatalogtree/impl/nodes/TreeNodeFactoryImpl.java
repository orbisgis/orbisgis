/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.jooq.Catalog;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Meta;
import org.jooq.QueryPart;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.editorjdbc.TransferableSource;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNodeImpl;
import org.orbisgis.geocatalogtree.api.TreeNodeFactory;
import org.orbisgis.geocatalogtree.icons.GeocatalogIcon;
import org.orbisgis.sif.components.resourceTree.TreeSelectionIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode.*;

/**
 * Create and update nodes
 * @author Nicolas Fortin
 */
public class TreeNodeFactoryImpl implements TreeNodeFactory {
    private static Logger LOGGER = LoggerFactory.getLogger(TreeNodeFactoryImpl.class);
    private static I18n I18N = I18nFactory.getI18n(TreeNodeFactoryImpl.class);
    private DataManager dataManager;
    private boolean isH2 = false;
    private String defaultSchema = "PUBLIC";

    public TreeNodeFactoryImpl(DataManager dataManager) {
        this.dataManager = dataManager;
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            try {
                if (connection.getSchema() != null) {
                    defaultSchema = connection.getSchema();
                }
            } catch (AbstractMethodError | Exception ex) {
                // Driver has been compiled with JAVA 6, or is not implemented
            }
        } catch (SQLException ex) {
            // Ignore
        }
    }

    @Override
    public String[] getParentNodeType() {
        return new String[]{NODE_DATABASE, NODE_CATALOG, NODE_SCHEMA, NODE_TABLE, NODE_COLUMNS, NODE_INDEXES};
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
                    return ((Schema) parentQueryPart).getTable(TableLocation.parse(treeNode.getNodeIdentifier()).getTable());
                case NODE_COLUMNS:
                    return parentQueryPart;
                case NODE_INDEXES:
                    return parentQueryPart;
            }
        }
        return null;
    }

    /**
     * Load nodes. Can be extended by overriding this method.
     * @param parent Parent node to fill or update
     * @param parentQueryPart Parent node JOOQ instance
     * @param connection Active connection
     * @param jTree JTree that will receive items
     * @param allNodes Nodes created
     * @param allNodesQueryPart QueryPart of nodes created
     */
    protected void loadNodes(GeoCatalogTreeNode parent, QueryPart parentQueryPart, Connection connection, JTree
            jTree, List<GeoCatalogTreeNodeImpl> allNodes, List<QueryPart> allNodesQueryPart) throws SQLException {
        DefaultTreeModel treeModel = (DefaultTreeModel)jTree.getModel();
        // Read Meta and create all nodes
        switch (parent.getNodeType()) {
            case NODE_DATABASE:
                loadCatalog(parent, DSL.using(connection).meta(), treeModel,allNodes, allNodesQueryPart);
                break;
            case NODE_CATALOG:
                loadSchema((Catalog)parentQueryPart,allNodes, allNodesQueryPart);
                break;
            case NODE_SCHEMA:
                loadTable((Schema)parentQueryPart,allNodes, allNodesQueryPart, connection);
                break;
            case NODE_TABLE:
                allNodes.add(new GeoCatalogTreeNodeImpl(this, NODE_COLUMNS, I18N.tr("Columns"),
                        GeocatalogIcon.getIcon("column"), GeocatalogIcon.getIcon("column")).setSortChildren(false));
                allNodesQueryPart.add(parentQueryPart);
                allNodes.add(new GeoCatalogTreeNodeImpl(this, NODE_INDEXES, I18N.tr("Index"),
                        GeocatalogIcon.getIcon("index_folder"),GeocatalogIcon.getIcon("index_folder")));
                allNodesQueryPart.add(parentQueryPart);
                break;
            case NODE_COLUMNS:
                loadFields((Table)parentQueryPart, allNodes, allNodesQueryPart, connection);
                break;
            case NODE_INDEXES:
                loadIndexes((Table)parentQueryPart, allNodes, allNodesQueryPart, connection);
                break;
        }
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
        loadNodes(parent, parentQueryPart, connection, jTree, allNodes, allNodesQueryPart);
        Map<String, GeoCatalogTreeNode> oldNodes = parent.getChildrenIdentifier();
        List<MutableTreeNode> nodeToInsert = new ArrayList<>(allNodes.size());
        if(parent.isChildrenSorted()) {
            // Check if the node already exist in the tree or if old nodes has been removed from db
            for (int nodeId = 0; nodeId < allNodes.size(); nodeId++) {
                GeoCatalogTreeNodeImpl node = allNodes.get(nodeId);
                QueryPart nodeQueryPart = allNodesQueryPart.get(nodeId);
                GeoCatalogTreeNode existingNode = oldNodes.get(node.getNodeIdentifier());
                if (existingNode != null) {
                    // Node already exists
                    oldNodes.remove(node.getNodeIdentifier());
                    // Update the node if it contains at least one child
                    TreePath existingNodePath = new TreePath(((DefaultTreeModel) jTree.getModel()).getPathToRoot(existingNode));
                    if (existingNode.getChildCount() > 0 || jTree.isExpanded(existingNodePath)) {
                        TreeNodeFactory treeNodeFactory = existingNode.getFactory();
                        if (treeNodeFactory instanceof TreeNodeFactoryImpl) {
                            TreeNodeFactoryImpl childFactory = (TreeNodeFactoryImpl) treeNodeFactory;
                            childFactory.updateChildren(existingNode, nodeQueryPart, connection, jTree);
                        } else if (treeNodeFactory != null) {
                            treeNodeFactory.updateChildren(node, connection, jTree);
                        }
                    }
                } else {
                    // New node, add it in the tree
                    nodeToInsert.add(node);
                }
            }
        } else {
            // In order to keep the parsed order we have to remove all old node (loosing tree states also)
            nodeToInsert.addAll(allNodes);
        }
        // Add remove nodes on model using Swing event thread
        List<MutableTreeNode> nodesToRemove = new ArrayList<>(oldNodes.isEmpty() ? 1 : oldNodes.size());
        if(!oldNodes.isEmpty()) {
            // Removed nodes
            for(GeoCatalogTreeNode node : oldNodes.values()) {
                if(isNodeMadeByThis(node)) {
                    nodesToRemove.add(node);
                }
            }
        }
        SwingUtilities.invokeLater(new TreeModelOperation(treeModel, nodeToInsert,nodesToRemove, parent));
    }

    protected boolean isNodeMadeByThis(GeoCatalogTreeNode node) {
        return node.getFactory() == this;
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
                nodes.add(new GeoCatalogTreeNodeImpl(this, NODE_SCHEMA, schema.getName(),
                        GeocatalogIcon.getIcon("schema"), GeocatalogIcon.getIcon("schema")));
                nodesQueryPart.add(schema);
            }
        }
    }

    private void loadTable(Schema schema, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart, Connection connection) throws SQLException {
        if(schema != null) {
            for (Table table : schema.getTables()) {
                // Check if the table is a geo table
                TableLocation identifier = new TableLocation(null, schema
                        .getName(), table.getName());
                boolean hasGeoField = !SFSUtilities.getGeometryFields(connection, identifier).isEmpty();
                if (hasGeoField) {
                    nodes.add(new GeoCatalogTreeNodeImpl(this, NODE_TABLE, identifier.toString(isH2), GeocatalogIcon
                            .getIcon("geofile"), GeocatalogIcon.getIcon("geofile")).setLabel(table.getName()).set
                            (GeoCatalogTreeNode.PROP_SPATIAL_TABLE, true));
                } else {
                    nodes.add(new GeoCatalogTreeNodeImpl(this, NODE_TABLE, identifier.toString(isH2), GeocatalogIcon
                            .getIcon("flatfile"), GeocatalogIcon.getIcon("flatfile")).setLabel(table.getName()).set
                            (GeoCatalogTreeNode.PROP_SPATIAL_TABLE, false));
                }
                nodesQueryPart.add(table);
            }
        }
    }
    private void loadFields(Table table, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart, Connection connection) throws SQLException {
        if(table != null) {
            // Fetch PK for icon
            Set<String> pkFieldNames = new HashSet<>();
            UniqueKey pk = table.getPrimaryKey();
            if(pk != null) {
                List<TableField> fields = pk.getFields();
                for(TableField field : fields) {
                    pkFieldNames.add(field.getName());
                }
            }
            // Fetch geometry fields
            Set<String> spatialFields = new HashSet<>(SFSUtilities.getGeometryFields(connection, new TableLocation
                    (table.getSchema().getName(), table.getName())));
            for(Field field : table.fields()) {
                GeoCatalogTreeNodeImpl fieldNode;
                if(pkFieldNames.contains(field.getName())) {
                    fieldNode = new GeoCatalogTreeNodeImpl(this, NODE_COLUMN, field.getName(), GeocatalogIcon.getIcon("key"));
                } else {
                    ImageIcon icon;
                    DataType dataType = field.getDataType();
                    if(dataType.isNumeric()) {
                        icon = GeocatalogIcon.getIcon("field_num");
                    } else if(dataType.getSQLType() == Types.BOOLEAN) {
                        icon = GeocatalogIcon.getIcon("field_bool");
                    } else if(dataType.isString()) {
                        icon = GeocatalogIcon.getIcon("field_text");
                    } else if(dataType.isDateTime()) {
                        icon = GeocatalogIcon.getIcon("field_date");
                    } else if(spatialFields.contains(field.getName())) {
                        icon = GeocatalogIcon.getIcon("field_geom");
                    } else{
                        icon = GeocatalogIcon.getIcon("column");
                    }
                    fieldNode = new GeoCatalogTreeNodeImpl(this, NODE_COLUMN, field.getName(), icon);
                }
                fieldNode.set(GeoCatalogTreeNode.PROP_COLUMN_SPATIAL, spatialFields.contains(field.getName()));
                fieldNode.set(GeoCatalogTreeNode.PROP_COLUMN_TYPE_NAME, field.getDataType().getTypeName());
                fieldNode.setAllowsChildren(false);
                nodes.add(fieldNode);
                nodesQueryPart.add(field);
            }
        }
    }

    private void loadIndexes(Table table, List<GeoCatalogTreeNodeImpl> nodes, List<QueryPart> nodesQueryPart,
                             Connection connection) throws SQLException {
        if (table != null) {
            // Fetch all index
            TableLocation tableLocation = new TableLocation(table.getSchema().getName(), table.getName());
            List<String> spatialFields = SFSUtilities.getGeometryFields(connection, tableLocation);
            // Fetch
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try(ResultSet rs = databaseMetaData.getIndexInfo(tableLocation.getCatalog(), tableLocation.getSchema(), tableLocation.getTable(), false, true)) {
                while(rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    String columnName = rs.getString("COLUMN_NAME");
                    if(indexName != null) {
                        StringBuilder label = new StringBuilder(indexName);
                        if(columnName != null) {
                            label.append(" (");
                            label.append(columnName);
                            label.append(")");
                        }
                        ImageIcon leafIcon;
                        if(spatialFields.contains(columnName)) {
                            leafIcon = GeocatalogIcon.getIcon("index_geo");
                        } else {
                            leafIcon = GeocatalogIcon.getIcon("index_alpha");
                        }
                        nodes.add(new GeoCatalogTreeNodeImpl(this, GeoCatalogTreeNode
                                .NODE_INDEX, new TableLocation(tableLocation.getSchema(), indexName).toString(isH2), leafIcon).setLabel(label.toString()));
                        nodesQueryPart.add(table);
                    }
                }
            }
        }
    }

    @Override
    public void nodeValueVetoableChange(GeoCatalogTreeNode node, String newValue) throws PropertyVetoException {

    }

    @Override
    public void nodeValueChange(GeoCatalogTreeNode node, String oldValue, String newValue) {

    }

    private String getSimplifiedTableIdentifier(String tableIdentifier) {
        TableLocation columnTableIdentifier = TableLocation.parse(tableIdentifier);
        return new TableLocation(columnTableIdentifier.getCatalog(), columnTableIdentifier.getSchema()
                .equalsIgnoreCase(defaultSchema) ? "" : columnTableIdentifier.getSchema(), columnTableIdentifier
                .getTable()).toString(isH2);
    }

    @Override
    public Transferable createTransferable(JTree dbTree) {
        List<String> sources = new ArrayList<>(dbTree.getSelectionCount());
        List<String> columns = new ArrayList<>(dbTree.getSelectionCount());
        String columnTable = "";
        Transferable transferable = null;
        for(GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            switch (treeNode.getNodeType()) {
                case GeoCatalogTreeNode.NODE_TABLE:
                    sources.add(getSimplifiedTableIdentifier(treeNode.getNodeIdentifier()));
                    break;
                case GeoCatalogTreeNode.NODE_COLUMN:
                    if(columnTable.isEmpty()) {
                        columnTable = treeNode.getParent().getParent().getNodeIdentifier();
                    }
                    if(columnTable.equals(treeNode.getParent().getParent().getNodeIdentifier())) {
                        columns.add(treeNode.getNodeIdentifier());
                    }
                    break;
            }
        }
        if(!sources.isEmpty()) {
            transferable = new TransferableSource(sources.toArray(new String[sources.size()]), dataManager);
        } else if(!columns.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ");
            for(int i=0; i<columns.size();i++) {
                if(i > 0) {
                    sb.append(", ");
                }
                sb.append(columns.get(i));
            }
            sb.append(" FROM ");
            sb.append(getSimplifiedTableIdentifier(columnTable));
            sb.append(";");
            transferable = new StringSelection(sb.toString());
        }
        return transferable;
    }


    /**
     * Insertion/Deletion of nodes into the model. Has to be done in the Swing Event thread to avoid gui problems.
     */
    private static class TreeModelOperation implements Runnable {
        private DefaultTreeModel defaultTreeModel;
        private List<MutableTreeNode> newChildren;
        private List<MutableTreeNode> nodesToRemove;
        private GeoCatalogTreeNode parent;

        public TreeModelOperation(DefaultTreeModel defaultTreeModel, List<MutableTreeNode> newChildren
                ,List<MutableTreeNode> nodesToRemove, GeoCatalogTreeNode parent) {
            this.defaultTreeModel = defaultTreeModel;
            this.newChildren = newChildren;
            this.nodesToRemove = nodesToRemove;
            this.parent = parent;
        }

        @Override
        public void run() {
            // First remove deprecated nodes
            for(MutableTreeNode node : nodesToRemove) {
                defaultTreeModel.removeNodeFromParent(node);
            }
            if(parent.isChildrenSorted()) {
                // Insert alphabetically
                List<String> modelNodes = new ArrayList<>(parent.getChildCount() + newChildren.size());
                for (int nodeId = 0; nodeId < parent.getChildCount(); nodeId++) {
                    modelNodes.add(parent.getChildAt(nodeId).toString());
                }
                while (!newChildren.isEmpty()) {
                    MutableTreeNode nodeToInsert = newChildren.remove(0);
                    int index = Collections.binarySearch(modelNodes, nodeToInsert.toString());
                    index = index >= 0 ? index : (-(index) - 1);
                    defaultTreeModel.insertNodeInto(nodeToInsert, parent, index);
                    modelNodes.add(index, nodeToInsert.toString());
                }
            } else {
                for(MutableTreeNode nodeToInsert : newChildren) {
                    defaultTreeModel.insertNodeInto(nodeToInsert, parent, parent.getChildCount());
                }
            }
        }
    }
}

