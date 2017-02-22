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
package org.orbisgis.geocatalogtree.impl;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.h2gis.api.DriverFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.h2gis.utilities.TableLocation;
import org.jooq.impl.DSL;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DatabaseProgressionListener;
import org.orbisgis.corejdbc.StateEvent;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.dbjobs.jobs.DropTable;
import org.orbisgis.dbjobs.jobs.ExportInFileOperation;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeAction;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNodeImpl;
import org.orbisgis.geocatalogtree.api.PopupMenu;
import org.orbisgis.geocatalogtree.api.PopupTarget;
import org.orbisgis.geocatalogtree.api.TreeNodeFactory;
import org.orbisgis.geocatalogtree.icons.GeocatalogIcon;
import org.orbisgis.geocatalogtree.impl.jobs.CreateIndex;
import org.orbisgis.geocatalogtree.impl.jobs.CreateSpatialIndex;
import org.orbisgis.geocatalogtree.impl.jobs.DropColumn;
import org.orbisgis.geocatalogtree.impl.jobs.DropIndex;
import org.orbisgis.geocatalogtree.impl.nodes.TableAndField;
import org.orbisgis.geocatalogtree.impl.nodes.TreeNodeFactoryImpl;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.fstree.CustomTreeCellRenderer;
import org.orbisgis.sif.components.fstree.TreeNodeBusy;
import org.orbisgis.sif.components.resourceTree.AbstractTreeModel;
import org.orbisgis.sif.components.resourceTree.TreeSelectionIterable;
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

/**
 * @author Nicolas Fortin
 * @author Erwan Bocher
 */
@Component(service = DockingPanel.class)
public class CatalogPanel extends JPanel implements DockingPanel, TreeWillExpandListener, DatabaseView, DatabaseProgressionListener, PopupTarget {
    private final JTree dbTree = new JTree(new String[0]);
    private DefaultTreeModel defaultTreeModel;
    private DockingPanelParameters dockingParameters = new DockingPanelParameters();
    private static final I18n I18N = I18nFactory.getI18n(CatalogPanel.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogPanel.class);
    private ActionCommands dockingActions = new ActionCommands();
    private ActionCommands popupActions = new ActionCommands();
    private DataManager dataManager;
    private Map<String, Set<TreeNodeFactory>> treeNodeFactories = new HashMap<>();
    private TreeNodeFactoryImpl defaultTreeNodeFactory;
    private AtomicBoolean loadingNodeChildren = new AtomicBoolean(false);
    private ExecutorService executorService;
    private DriverFunctionContainer driverFunctionContainer;
    private Boolean isH2;

    public CatalogPanel() {
        super(new BorderLayout());
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC,
            policyOption = ReferencePolicyOption.GREEDY)
    public void addPopupMenu(PopupMenu popupMenu) {
        popupActions.addActionFactory(popupMenu, this);
        popupActions.setAccelerators(this);
    }

    public void removePopupMenu(PopupMenu popupMenu) {
        popupActions.removeActionFactory(popupMenu);
        popupActions.setAccelerators(this);
    }

    @Override
    public List<String> getSelectedSources() {
        List<String> sources = new ArrayList<>(dbTree.getSelectionCount());
        for (GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode
                .class)) {
            if (GeoCatalogTreeNode.NODE_TABLE.equals(treeNode.getNodeType())) {
                sources.add(new TableLocation(treeNode.getParent().getNodeIdentifier(), treeNode.getNodeIdentifier())
                        .toString(isH2()));
            }
        }
        return sources;
    }

    /**
     * Check if the database is an H2 database engine.
     * @return 
     */
    private boolean isH2() {
        if(isH2 == null) {
            try(Connection connection = dataManager.getDataSource().getConnection()) {
                isH2 = JDBCUtilities.isH2DataBase(connection.getMetaData());
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
                return false;
            }
        }
        return isH2;
    }

    @Override
    public JTree getTree() {
        return dbTree;
    }

    @Reference
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }

    public void init() {
        defaultTreeNodeFactory = new TreeNodeFactoryImpl(dataManager);
        addTreeNodeFactory(defaultTreeNodeFactory);
        dbTree.addMouseListener(EventHandler.create(MouseListener.class, this,
                "onMouseActionOnSourceList", "")); //This method ask the event data as argument
        //Items can be selected freely
        dbTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        dbTree.setRootVisible(false);
        dbTree.setShowsRootHandles(true);
        dbTree.setEditable(true);
        dbTree.addTreeSelectionListener(new IsEditableHandler(dbTree));
        add(new JScrollPane(dbTree));
        dockingParameters.setName("geocatalog-tree");
        dockingParameters.setTitle(I18N.tr("DB Tree"));
        dockingParameters.setTitleIcon(GeocatalogIcon.getIcon("db_tree"));
        dockingParameters.setCloseable(true);
        // Set the built-in actions to docking frame
        dockingParameters.setDockActions(dockingActions.getActions());
        // Add a listener to put additional actions to this docking
        dockingActions.addPropertyChangeListener(new ActionDockingListener(dockingParameters));
        addActions();
    }

    @Reference
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = driverFunctionContainer;
    }

    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = null;
    }

    @Override
    public void onDatabaseUpdate(String entity, String... identifier) {
        Set<String> identifiers = new HashSet<>(Arrays.asList(identifier));
        try {
            switch (DB_ENTITY.valueOf(entity)) {
                case TABLE:
                    Enumeration<TreePath> paths = dbTree.getExpandedDescendants(new TreePath(defaultTreeModel.getRoot()));
                    List<GeoCatalogTreeNode> nodeToUpdate = new ArrayList<>(dbTree.getRowCount());
                    while(paths != null && paths.hasMoreElements()) {
                        GeoCatalogTreeNode node = (GeoCatalogTreeNode)paths.nextElement().getLastPathComponent();
                        if(node != null) {
                            if(identifiers.contains(node.getNodeIdentifier())) {
                                nodeToUpdate.add(node);
                            }
                        }
                    }
                    if(!nodeToUpdate.isEmpty() && loadingNodeChildren.compareAndSet(false, true)) {
                        execute(new ReadDB(this, nodeToUpdate, loadingNodeChildren));
                    }
                    break;
            }
        } catch (IllegalArgumentException ex) {
            // Db entity not managed by CatalogPanel
        }
    }

    private void addActions() {
        boolean isEmbeddedDataBase = true;
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            isEmbeddedDataBase = JDBCUtilities.isH2DataBase(meta) && !meta.getURL().startsWith("jdbc:h2:tcp:");
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        //Popup:Add
        if(isEmbeddedDataBase) {
            GeoCatalogTreeAction addGroup = new GeoCatalogTreeAction(PopupMenu.M_ADD,I18N.tr("Add"), dbTree);
            addGroup.addNodeTypeFilter(GeoCatalogTreeNode.NODE_SCHEMA);
            popupActions.addAction(addGroup.setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_ADD));
            //Popup:Add:File
            GeoCatalogTreeAction addFile = new GeoCatalogTreeAction(PopupMenu.M_ADD_FILE, I18N.tr("File"), I18N.tr
                    ("Add a file from hard drive."), GeocatalogIcon.getIcon("page_white_add"), EventHandler.create
                    (ActionListener.class, this, "onMenuAddLinkedFile"), KeyStroke.getKeyStroke(KeyEvent.VK_O,
                    InputEvent.CTRL_DOWN_MASK), dbTree);
            addFile.addNodeTypeFilter(GeoCatalogTreeNode.NODE_SCHEMA);
            popupActions.addAction(addFile.addStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK))
                    .setParent(PopupMenu.M_ADD));
            //Popup:Add:Folder
            GeoCatalogTreeAction addFolder = new GeoCatalogTreeAction(PopupMenu.M_ADD_FOLDER,I18N.tr("Folder"),
                    I18N.tr("Add a set of file from an hard drive folder."),
                    GeocatalogIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuAddFilesFromFolder"),KeyStroke.getKeyStroke("ctrl alt O"), dbTree);
            addFolder.addNodeTypeFilter(GeoCatalogTreeNode.NODE_SCHEMA);
            popupActions.addAction(addFolder.setParent(PopupMenu.M_ADD));

            //Popup:Add:DataBase
            //popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_DB,I18N.tr("DataBase"),
            //        I18N.tr("Add one or more tables from a DataBase"),
            //        OrbisGISIcon.getIcon("database_add"),EventHandler.create(ActionListener.class,
            //        this,"onMenuAddFromDataBase"),null).setParent(PopupMenu.M_ADD));
        }
        //Popup:Import
        GeoCatalogTreeAction importGroup = new GeoCatalogTreeAction(PopupMenu.M_IMPORT,I18N.tr("Import"),dbTree);
        popupActions.addAction(importGroup.addNodeTypeFilter(GeoCatalogTreeNode.NODE_SCHEMA).setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_IMPORT));
        //Popup:Import:File
        GeoCatalogTreeAction importFile = new GeoCatalogTreeAction(PopupMenu.M_IMPORT_FILE,I18N.tr("File"),
                I18N.tr("Copy the content of a file from hard drive."),
                GeocatalogIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                this,"onMenuImportFile"),KeyStroke.getKeyStroke("ctrl I"), dbTree);
        popupActions.addAction(importFile.addNodeTypeFilter(GeoCatalogTreeNode.NODE_SCHEMA).setParent(PopupMenu
                .M_IMPORT));
        GeoCatalogTreeAction importFolder = new GeoCatalogTreeAction(PopupMenu.M_IMPORT_FOLDER,I18N.tr("Folder"),
                I18N.tr("Add a set of file from an hard drive folder."),
                GeocatalogIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                this,"onMenuImportFilesFromFolder"),KeyStroke.getKeyStroke("ctrl alt I"),dbTree);
        popupActions.addAction(importFolder.addNodeTypeFilter(GeoCatalogTreeNode.NODE_SCHEMA).setParent(PopupMenu
                .M_IMPORT));

        //Popup:Save
        GeoCatalogTreeAction saveGroup = new GeoCatalogTreeAction(PopupMenu.M_SAVE,I18N.tr("Save"), dbTree);
        popupActions.addAction(saveGroup.addNodeTypeFilter(GeoCatalogTreeNode.NODE_TABLE).setMenuGroup(true)
                .setLogicalGroup(PopupMenu.GROUP_ADD));
        //Popup:Save:File
        GeoCatalogTreeAction saveFile = new GeoCatalogTreeAction(PopupMenu.M_SAVE_FILE, I18N.tr("File"), I18N.tr
                ("Save selected sources in files"), GeocatalogIcon.getIcon("page_white_save"), EventHandler.create
                (ActionListener.class, this, "onMenuSaveInfile"), KeyStroke.getKeyStroke("ctrl S"), dbTree);
        popupActions.addAction(saveFile.addNodeTypeFilter(GeoCatalogTreeNode.NODE_TABLE).setParent(PopupMenu.M_SAVE));
        //Popup:Save:Db
        //TODO Add linked table then transfer data
        //popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_DB,I18N.tr("Database"),
        //        I18N.tr("Save selected sources in a data base"),OrbisGISIcon.getIcon("database_save"),
        //        EventHandler.create(ActionListener.class,this,"onMenuSaveInDB"),getListSelectionModel()).setParent(PopupMenu.M_SAVE));
        //Popup:Remove sources
        GeoCatalogTreeAction dropTable = new GeoCatalogTreeAction(PopupMenu.M_REMOVE, I18N.tr("Remove the source"),
                I18N.tr("Remove from this list the selected sources."), GeocatalogIcon.getIcon("remove"),
                EventHandler.create(ActionListener.class, this, "onMenuRemoveSource"), KeyStroke.getKeyStroke
                (KeyEvent.VK_DELETE, 0), dbTree);
        popupActions.addAction(dropTable.addNodeTypeFilter(GeoCatalogTreeNode.NODE_TABLE).setLogicalGroup(PopupMenu
                .GROUP_CLOSE));
        // Popup:Remove index
        GeoCatalogTreeAction dropIndex = new GeoCatalogTreeAction(PopupMenu.M_REMOVE_INDEX, I18N.tr("Drop the index"),
                I18N.tr("Remove this index"), GeocatalogIcon.getIcon("remove"),
                EventHandler.create(ActionListener.class, this, "onMenuRemoveIndex"), KeyStroke.getKeyStroke
                (KeyEvent.VK_DELETE, 0), dbTree);
        popupActions.addAction(dropIndex.addNodeTypeFilter(GeoCatalogTreeNode.NODE_INDEX).setLogicalGroup(PopupMenu
                .GROUP_CLOSE));
        //Popup:Refresh
        GeoCatalogTreeAction refresh = new GeoCatalogTreeAction(PopupMenu.M_REFRESH,I18N.tr("Refresh"),
                I18N.tr("Read the content of the database"),
                GeocatalogIcon.getIcon("refresh"),EventHandler.create(ActionListener.class,
                        this,"refreshSourceList"),KeyStroke.getKeyStroke("ctrl R"), dbTree);
        popupActions.addAction(refresh.setLogicalGroup(PopupMenu.GROUP_OPEN));
        GeoCatalogTreeAction createIndex = new GeoCatalogTreeAction(PopupMenu.M_CREATE_INDEX,
                I18N.tr("Create index"), GeocatalogIcon.getIcon("index_alpha"),
                EventHandler.create(ActionListener.class, this, "onMenuCreateIndex"), dbTree);
        createIndex.addNodeTypeFilter(GeoCatalogTreeNode.NODE_COLUMN);
        popupActions.addAction(createIndex);
        GeoCatalogTreeAction createSpatialIndex = new GeoCatalogTreeAction(PopupMenu.M_CREATE_SPATIAL_INDEX,
                I18N.tr("Create spatial index"), GeocatalogIcon.getIcon("index_geo"),
                EventHandler.create(ActionListener.class, this, "onMenuCreateSpatialIndex"), dbTree);
        createSpatialIndex.addNodeTypeFilter(GeoCatalogTreeNode.NODE_COLUMN);
        createSpatialIndex.check(GeoCatalogTreeNode.PROP_COLUMN_SPATIAL, true);
        popupActions.addAction(createSpatialIndex);
        GeoCatalogTreeAction dropColumn = new GeoCatalogTreeAction(PopupMenu.M_DROP_COLUMN,
                I18N.tr("Drop column"), GeocatalogIcon.getIcon("remove"),
                EventHandler.create(ActionListener.class, this, "onMenuDropColumn"), dbTree);
        popupActions.addAction(dropColumn.addNodeTypeFilter(GeoCatalogTreeNode.NODE_COLUMN).setLogicalGroup(PopupMenu
                .GROUP_CLOSE));
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

    /**
     * Add linked file to selected schema
     */
    public void onMenuAddLinkedFile() {
        driverFunctionContainer.importFile(this, DriverFunction.IMPORT_DRIVER_TYPE.LINK);
    }
    
    /**
     * User click on create index
     */
    public void onMenuCreateIndex() {
        List<TableAndField> fields = new ArrayList<TableAndField>(dbTree.getSelectionCount());
        for (GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            if (GeoCatalogTreeNode.NODE_COLUMN.equals(treeNode.getNodeType())) {
                GeoCatalogTreeNode fieldNode = (GeoCatalogTreeNode) treeNode;
                TableLocation table = TableLocation.parse(fieldNode.getParent().getParent()
                        .getNodeIdentifier());
                if (!checkIndexExists(fieldNode, table)) {
                    fields.add(new TableAndField(TableLocation.parse(treeNode.getParent().getParent().getNodeIdentifier()).toString(isH2()), treeNode.getNodeIdentifier()));
                }
            }
        }
        try {
            CreateIndex job = CreateIndex.onMenuCreateIndex(dataManager.getDataSource(), fields, this, this, isH2());
            if (job != null) {
                execute(job);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * User click on create spatial index
     */
    public void onMenuCreateSpatialIndex() {        
        List<TableAndField> fields = new ArrayList<TableAndField>(dbTree.getSelectionCount());
        for (GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            if (GeoCatalogTreeNode.NODE_COLUMN.equals(treeNode.getNodeType())) {
                GeoCatalogTreeNode fieldNode = (GeoCatalogTreeNode) treeNode;
                TableLocation table = TableLocation.parse(fieldNode.getParent().getParent()
                        .getNodeIdentifier());
                if (!checkIndexExists(fieldNode, table)) {
                    fields.add(new TableAndField(TableLocation.parse(treeNode.getParent().getParent().getNodeIdentifier()).toString(isH2()), treeNode.getNodeIdentifier()));
                }
            }
        }
        try {
            CreateSpatialIndex job = CreateSpatialIndex.onMenuCreateSpatialIndex(dataManager.getDataSource(), fields, this, this, isH2());
            if (job != null) {
                execute(job);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }
    
    /**
     * User click on dropColumn
     */
    public void onMenuDropColumn() {
        List<TableAndField> fields = new ArrayList<TableAndField>(dbTree.getSelectionCount());
        for (GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            if (GeoCatalogTreeNode.NODE_COLUMN.equals(treeNode.getNodeType())) {
                fields.add(new TableAndField( TableLocation.parse(treeNode.getParent().getParent().getNodeIdentifier()).toString(isH2()), treeNode.getNodeIdentifier()));
            }
        }
        try {
            DropColumn job = DropColumn.onMenuDropColumn(dataManager.getDataSource(), fields, this, this);
            if (job != null) {
                execute(job);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }
    
    /**
     * Check if the field is already indexed
     * @param fieldNode
     * @param table 
     */
    private boolean checkIndexExists(GeoCatalogTreeNode fieldNode, TableLocation table) {
        try (Connection connection = dataManager.getDataSource().getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet rs = databaseMetaData.getIndexInfo(table.getCatalog(), table.getSchema(), table.getTable(), false, true)) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if (fieldNode.getNodeIdentifier().equals(columnName)) {
                        // Index already exists
                        LOGGER.error(I18N.tr("This field is already indexed"));
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
        return false;
    }

    /**
     * Add all files from selected folder recursively
     */
    public void onMenuAddFilesFromFolder() {
        driverFunctionContainer.addFilesFromFolder(this, DriverFunction.IMPORT_DRIVER_TYPE.LINK);
    }

    private String getSelectedSchema() {
        // Get selected schema
        String schema = null;
        Object nodeObj = dbTree.getLastSelectedPathComponent();
        if(nodeObj instanceof GeoCatalogTreeNode
                && GeoCatalogTreeNode.NODE_SCHEMA.equals(((GeoCatalogTreeNode) nodeObj).getNodeType())) {
            schema = ((GeoCatalogTreeNode) nodeObj).getNodeIdentifier();
        }
        return schema;
    }
    /**
     * Copy file content into a table
     */
    public void onMenuImportFile() {
        driverFunctionContainer.importFile(this, DriverFunction.IMPORT_DRIVER_TYPE.COPY, getSelectedSchema());
    }

    /**
     * Copy all files in a folder to tables
     */
    public void onMenuImportFilesFromFolder() {
        driverFunctionContainer.addFilesFromFolder(this, DriverFunction.IMPORT_DRIVER_TYPE.COPY, getSelectedSchema());
    }

    /**
     * Export a table into a file.
     */
    public void onMenuSaveInfile() {
        List<String> sources = new ArrayList<>(dbTree.getSelectionCount());
        for(GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            if(GeoCatalogTreeNode.NODE_TABLE.equals(treeNode.getNodeType())) {
                sources.add(treeNode.getNodeIdentifier());
            }
        }
        ExportInFileOperation exportJob = ExportInFileOperation.saveInfile(dataManager.getDataSource(), sources, driverFunctionContainer);
        if(exportJob != null) {
            execute(exportJob);
        }
    }

    public void onMenuRemoveIndex() {
        List<String> indexes = new ArrayList<>(dbTree.getSelectionCount());
        for(GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            if(GeoCatalogTreeNode.NODE_INDEX.equals(treeNode.getNodeType())) {
                indexes.add(treeNode.getNodeIdentifier());
            }
        }
        try {
            DropIndex job = DropIndex.onMenuDropIndex(dataManager.getDataSource(), indexes, this, this);
            if (job != null) {
                execute(job);
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * Drop selected tables
     */
    public void onMenuRemoveSource() {
        List<String> sources = new ArrayList<>(dbTree.getSelectionCount());
        for(GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
            if(GeoCatalogTreeNode.NODE_TABLE.equals(treeNode.getNodeType())) {
                sources.add(treeNode.getNodeIdentifier());
            }
        }
        DropTable job = DropTable.onMenuRemoveSource(dataManager.getDataSource(), sources, this, this);
        if(job != null) {
            execute(job);
        }
    }

    private void execute(SwingWorker swingWorker) {
        if(executorService != null) {
            executorService.execute(swingWorker);
        } else {
            swingWorker.execute();
        }
    }

    @Override
    public void progressionUpdate(StateEvent state) {
        // Refresh root node
        if(state.isUpdateDatabaseStructure() && loadingNodeChildren.compareAndSet(false, true)) {
            execute(new ReadDB(this, (GeoCatalogTreeNode) defaultTreeModel.getRoot(), loadingNodeChildren));
        }
    }

    @Override
    public void refreshSourceList() {
        if(loadingNodeChildren.compareAndSet(false, true)) {
            if (!dbTree.isSelectionEmpty()) {
                List<GeoCatalogTreeNode> nodeToRefresh = new ArrayList<>(dbTree.getSelectionCount());
                for (GeoCatalogTreeNode treeNode : new TreeSelectionIterable<>(dbTree.getSelectionPaths(), GeoCatalogTreeNode.class)) {
                    nodeToRefresh.add(treeNode);
                }
                if(nodeToRefresh.isEmpty()) {
                    loadingNodeChildren.set(false);
                } else {
                    execute(new ReadDB(this, nodeToRefresh, loadingNodeChildren));
                }
            } else {
                // Refresh root node
                execute(new ReadDB(this, (GeoCatalogTreeNode) defaultTreeModel.getRoot(), loadingNodeChildren));
            }
        }
    }

    public void onMouseActionOnSourceList(MouseEvent e) {
        //Manage selection of items before popping up the menu
        if (e.isPopupTrigger()) { //Right mouse button under linux and windows
            //Update selection
            TreePath path = dbTree.getPathForLocation(e.getX(), e.getY());
            TreePath[] selectionPaths = dbTree.getSelectionPaths();
            if (selectionPaths != null && path != null){
                if (!AbstractTreeModel.contains(selectionPaths, path)) {
                    if (e.isControlDown()) {
                        dbTree.addSelectionPath(path);
                    } else {
                        dbTree.setSelectionPath(path);
                    }
                }
            } else {
                dbTree.setSelectionPath(path);
            }
            JPopupMenu popup = new JPopupMenu();
            popupActions.copyEnabledActions(popup);
            if (popup.getComponentCount()>0) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
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
        dataManager.removeDatabaseProgressionListener(this);
        this.dataManager = null;
    }

    @Activate
    public void activate() {
        init();
        new InitTree(this).execute();
    }

    private void initTree() {
        // Load catalogs
        try(Connection connection = dataManager.getDataSource().getConnection()) {
            defaultTreeModel = new GeoCatalogTreeModel(new GeoCatalogTreeNodeImpl(null, "", ""), true);
            defaultTreeNodeFactory.loadDatabase(DSL.using(connection).meta(), defaultTreeModel);
            dbTree.setModel(defaultTreeModel);
            dbTree.setCellRenderer(new CustomTreeCellRenderer(dbTree));
            dbTree.expandPath(new TreePath(defaultTreeModel.getRoot()));
            updateNode((GeoCatalogTreeNode) defaultTreeModel.getRoot());
            dbTree.addTreeWillExpandListener(this);
            dataManager.addDatabaseProgressionListener(this, StateEvent.DB_STATES.STATE_STATEMENT_END);
            popupActions.setAccelerators(this);
            dbTree.setDragEnabled(true);
            dbTree.setTransferHandler(new DBTreeTranferHandler(dbTree));
        } catch (SQLException ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
        }
    }

    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        // Update expanding node
        Object lastPathComp = event.getPath().getLastPathComponent();
        if(lastPathComp instanceof GeoCatalogTreeNode && ((GeoCatalogTreeNode) lastPathComp).getChildCount() == 0) {
            GeoCatalogTreeNode node = (GeoCatalogTreeNode)lastPathComp;
            loadingNodeChildren.set(true);
            new ReadDB(this, node, loadingNodeChildren).execute();
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

    public JTree getDbTree() {
        return dbTree;
    }

    private static class ReadDB extends  SwingWorker {
        private CatalogPanel catalogPanel;
        private List<GeoCatalogTreeNode> nodes;
        private AtomicBoolean loadingNodeChildren;

        public ReadDB(CatalogPanel catalogPanel, List<GeoCatalogTreeNode> nodes, AtomicBoolean loadingNodeChildren) {
            this.catalogPanel = catalogPanel;
            this.nodes = nodes;
            this.loadingNodeChildren = loadingNodeChildren;
        }

        public ReadDB(CatalogPanel catalogPanel, GeoCatalogTreeNode node, AtomicBoolean loadingNodeChildren) {
            this.catalogPanel = catalogPanel;
            this.nodes = Arrays.asList(node);
            this.loadingNodeChildren = loadingNodeChildren;
        }

        @Override
        protected Object doInBackground() throws Exception {
            try {
                for (GeoCatalogTreeNode node : nodes) {
                    TreeNodeBusy nodeBusy = null;
                    try {
                        if (node.getAllowsChildren() && node.getChildCount() == 0) {
                            nodeBusy = new TreeNodeBusy();
                            DefaultTreeModel treeModel = (DefaultTreeModel) catalogPanel.getDbTree().getModel();
                            nodeBusy.setModel(treeModel);
                            // Model change should be done on swing event thread
                            SwingUtilities.invokeAndWait(new InsertBusyNode(treeModel, nodeBusy, node));
                            nodeBusy.setDoAnimation(true);
                        }
                        catalogPanel.updateNode(node);
                    } finally {
                        if (nodeBusy != null) {
                            nodeBusy.setDoAnimation(false);
                        }
                    }
                }
            } finally {
                loadingNodeChildren.set(false);
            }
            return null;
        }
    }

    private static class InsertBusyNode implements Runnable {
        private DefaultTreeModel treeModel;
        private TreeNodeBusy nodeBusy;
        private GeoCatalogTreeNode node;

        public InsertBusyNode(DefaultTreeModel treeModel, TreeNodeBusy nodeBusy, GeoCatalogTreeNode node) {
            this.treeModel = treeModel;
            this.nodeBusy = nodeBusy;
            this.node = node;
        }

        @Override
        public void run() {
            treeModel.insertNodeInto(nodeBusy, node, 0);
        }
    }

    /**
     * Change tree editable state using selected component editable state.
     */
    private static class IsEditableHandler implements TreeSelectionListener {
        private JTree tree;

        public IsEditableHandler(JTree tree) {
            this.tree = tree;
        }

        @Override
        public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
            TreePath path = treeSelectionEvent.getPath();
            Object lastPathComponent = path.getLastPathComponent();
            if(lastPathComponent instanceof GeoCatalogTreeNode) {
                tree.setEditable(((GeoCatalogTreeNode) lastPathComponent).isEditable());
            }
        }
    }
}
