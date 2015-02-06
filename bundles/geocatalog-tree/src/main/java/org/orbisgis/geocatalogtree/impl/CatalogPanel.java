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
package org.orbisgis.geocatalogtree.impl;

import org.jooq.Catalog;
import org.jooq.DSLContext;
import org.jooq.Meta;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.geocatalogtree.icons.GeocatalogIcon;
import org.orbisgis.geocatalogtree.impl.nodes.TreeNodeCatalog;
import org.orbisgis.geocatalogtree.impl.nodes.TreeNodeDataBase;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Nicolas Fortin
 */
@Component(service = DockingPanel.class)
public class CatalogPanel extends JPanel implements DockingPanel {
    private JTree dbTree;
    private DefaultTreeModel defaultTreeModel;
    private DockingPanelParameters dockingParameters = new DockingPanelParameters();
    private static final I18n I18N = I18nFactory.getI18n(CatalogPanel.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogPanel.class);
    private ActionCommands dockingActions = new ActionCommands();
    private DataManager dataManager;

    public CatalogPanel() {
        super(new BorderLayout());
        dbTree = new JTree(new String[0]);
        //Items can be selected freely
        dbTree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        dbTree.setDragEnabled(true);
        //TODO dbTree.setTransferHandler(handler);
        dbTree.setRootVisible(false);
        dbTree.setShowsRootHandles(true);
        dbTree.setEditable(true);
        add(new JScrollPane(dbTree));
        dockingParameters.setName("geocatalog-tree");
        dockingParameters.setTitle(I18N.tr("GeoCatalog"));
        dockingParameters.setTitleIcon(GeocatalogIcon.getIcon("geocatalog"));
        dockingParameters.setCloseable(true);
        // Set the built-in actions to docking frame
        dockingParameters.setDockActions(dockingActions.getActions());
        // Add a listener to put additional actions to this docking
        dockingActions.addPropertyChangeListener(new ActionDockingListener(dockingParameters));
    }

    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    @Activate
    public void activate() {
        // Load catalogs
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            Meta meta = DSL.using(connection).meta();
            List<Catalog> catalogs = meta.getCatalogs();
            if(catalogs.size() > 1) {
                defaultTreeModel = new DefaultTreeModel(new TreeNodeDataBase(), true);
                for (Catalog catalog : catalogs) {
                    defaultTreeModel.insertNodeInto(new TreeNodeCatalog(catalog.getName()), (MutableTreeNode) defaultTreeModel.getRoot(), 0);
                }
            } else {
                TreeNodeCatalog catalog = new TreeNodeCatalog(catalogs.get(0).getName());
                defaultTreeModel = new DefaultTreeModel(catalog, true);
                catalog.loadChildren(connection, defaultTreeModel);
            }
            dbTree.setModel(defaultTreeModel);
            dbTree.expandPath(new TreePath(defaultTreeModel.getRoot()));
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return dockingParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    private void readDatabase() {
        // Detect change between shown components

    }
}
