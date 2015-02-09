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

import org.jooq.Meta;
import org.jooq.impl.DSL;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNodeImpl;
import org.orbisgis.geocatalogtree.api.TreeNodeFactory;
import org.orbisgis.geocatalogtree.icons.GeocatalogIcon;
import org.orbisgis.geocatalogtree.impl.nodes.TreeNodeFactoryImpl;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Nicolas Fortin
 */
@Component(service = DockingPanel.class)
public class CatalogPanel extends JPanel implements DockingPanel, TreeWillExpandListener {
    private JTree dbTree;
    private DefaultTreeModel defaultTreeModel;
    private DockingPanelParameters dockingParameters = new DockingPanelParameters();
    private static final I18n I18N = I18nFactory.getI18n(CatalogPanel.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogPanel.class);
    private ActionCommands dockingActions = new ActionCommands();
    private DataManager dataManager;
    private Map<String, Set<TreeNodeFactory>> treeNodeFactories = new HashMap<>();
    private TreeNodeFactoryImpl defaultTreeNodeFactory;

    public CatalogPanel() {
        super(new BorderLayout());
        defaultTreeNodeFactory = new TreeNodeFactoryImpl();
        addTreeNodeFactory(defaultTreeNodeFactory);
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

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
            ReferencePolicyOption.GREEDY)
    public void addTreeNodeFactory(TreeNodeFactory treeNodeFactory) {
        for(String nodeType : treeNodeFactory.getParentNodeType()) {
            Set<TreeNodeFactory> factorySet = treeNodeFactories.get(nodeType);
            if(factorySet == null) {
                factorySet = new HashSet<>();
                treeNodeFactories.put(nodeType, factorySet);
            }
            factorySet.add(treeNodeFactory);
        }
    }

    public void removeTreeNodeFactory(TreeNodeFactory treeNodeFactory) {
        GeoCatalogTreeNode current = (GeoCatalogTreeNode)defaultTreeModel.getRoot();
        while(current != null) {
            if(treeNodeFactory.equals(current.getFactory())) {
                GeoCatalogTreeNode removed = current;
                current = current.getParent();
                defaultTreeModel.removeNodeFromParent(removed);
            }
        }
        Set<Map.Entry<String, Set<TreeNodeFactory>>> factorySet = treeNodeFactories.entrySet();
        for(Map.Entry<String, Set<TreeNodeFactory>> entry : factorySet) {
            if(treeNodeFactory.equals(entry.getValue())) {
                factorySet.remove(entry);
            }
        }
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
        new InitTree(this).execute();
    }

    private void initTree() {
        // Load catalogs
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            defaultTreeModel = new DefaultTreeModel(new GeoCatalogTreeNodeImpl(null, "", ""), true);
            defaultTreeNodeFactory.loadDatabase(DSL.using(connection).meta(), defaultTreeModel);
            dbTree.setModel(defaultTreeModel);
            dbTree.expandPath(new TreePath(defaultTreeModel.getRoot()));
            updateNode((GeoCatalogTreeNode)defaultTreeModel.getRoot());
            dbTree.addTreeWillExpandListener(this);
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        // Update expanding node
        Object lastPathComp = event.getPath().getLastPathComponent();
        if(lastPathComp instanceof GeoCatalogTreeNode) {
            GeoCatalogTreeNode node = (GeoCatalogTreeNode)lastPathComp;
            updateNode(node);
        }
    }

    /**
     * Load children of this node
     * @param node Parent node
     */
    public void updateNode(GeoCatalogTreeNode node) {
        Set<TreeNodeFactory> factorySet = treeNodeFactories.get(node.getNodeType());
        if(factorySet != null) {
            try(Connection connection = dataManager.getDataSource().getConnection()) {
                for (TreeNodeFactory factory : factorySet) {
                    factory.updateChildren(node, connection, dbTree);
                }
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
        }
    }

    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // Nothing to do
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

    private static class InitTree extends SwingWorker {
        private CatalogPanel catalogPanel;

        public InitTree(CatalogPanel catalogPanel) {
            this.catalogPanel = catalogPanel;
        }

        @Override
        protected Object doInBackground() throws Exception {
            catalogPanel.initTree();
            return null;
        }
    }
}
