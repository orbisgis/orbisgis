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
import org.jooq.Meta;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNodeImpl;
import org.orbisgis.geocatalogtree.api.TreeNodeFactory;

import javax.swing.tree.DefaultTreeModel;
import java.sql.Connection;
import java.util.List;

import static org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode.*;

/**
 * Create and update nodes
 * @author Nicolas Fortin
 */
public class TreeNodeFactoryImpl implements TreeNodeFactory {

    @Override
    public String[] getParentNodeType() {
        return new String[]{NODE_DATABASE, GeoCatalogTreeNode.NODE_CATALOG, GeoCatalogTreeNode.NODE_TABLE};
    }

    @Override
    public void updateChildren(GeoCatalogTreeNode parent, Connection connection, DefaultTreeModel treeModel) {
        updateChildren(parent, DSL.using(connection).meta(),treeModel);
    }

    public void updateChildren(GeoCatalogTreeNode parent, Meta meta, DefaultTreeModel treeModel) {
        if(parent == null) {
            loadDatabase(meta, treeModel);
            return;
        }
        switch (parent.getNodeType()) {
            case NODE_DATABASE:
                loadCatalog(parent, meta, treeModel);
                break;
            case NODE_CATALOG:
                loadSchema(parent, getCatalog(meta, parent), treeModel);
                break;
            case NODE_SCHEMA:
                loadTable(parent, getSchema(meta, parent), treeModel);
                break;
        }
    }

    /**
     * @param meta JOOQ meta
     * @param treeNode TreeNode database
     * @return Catalog or null
     */
    public Catalog getCatalog(Meta meta, GeoCatalogTreeNode treeNode) {
        for(Catalog catalog : meta.getCatalogs()) {
            if(treeNode.getNodeIdentifier().equals(catalog.getName())) {
                return catalog;
            }
        }
        return null;
    }

    /**
     * @param meta JOOQ meta
     * @param treeNode Catalog
     * @return Schema or null
     */
    public Schema getSchema(Meta meta, GeoCatalogTreeNode treeNode) {
        Catalog catalog = getCatalog(meta, treeNode.getParent());
        Schema schema = catalog.getSchema(treeNode.getNodeIdentifier());
        return schema != null ? schema : null;
    }

    private void loadDatabase(Meta meta, DefaultTreeModel treeModel) {
        List<Catalog> catalogs = meta.getCatalogs();
        if(catalogs.size() > 1) {
            treeModel.setRoot(new GeoCatalogTreeNodeImpl(this, NODE_DATABASE, "root"));
        } else {
            loadCatalog(null, meta, treeModel);
        }
    }

    private void loadCatalog(GeoCatalogTreeNode parent, Meta meta, DefaultTreeModel treeModel) {
        for(Catalog catalog : meta.getCatalogs()) {
            GeoCatalogTreeNodeImpl catalogNode = new GeoCatalogTreeNodeImpl(this, NODE_CATALOG, catalog.getName());
            if(parent == null) {
                treeModel.setRoot(catalogNode);
                break;
            } else {
                treeModel.insertNodeInto(catalogNode, parent, parent.getChildCount());
            }
        }
    }

    private void loadSchema(GeoCatalogTreeNode parent, Catalog catalog, DefaultTreeModel treeModel) {
        if(catalog != null) {
            for (Schema schema : catalog.getSchemas()) {
                treeModel.insertNodeInto(new GeoCatalogTreeNodeImpl(this, NODE_SCHEMA, schema.getName()),
                        parent, parent.getChildCount());
            }
        }
    }

    private void loadTable(GeoCatalogTreeNode parent, Schema schema, DefaultTreeModel treeModel) {
        if(schema != null) {
            for (Table table : schema.getTables()) {
                treeModel.insertNodeInto(new GeoCatalogTreeNodeImpl(this, NODE_TABLE, table.getName()),
                        parent, parent.getChildCount());
            }
        }
    }
}
