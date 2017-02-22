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
package org.orbisgis.tablegui.impl;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeAction;
import org.orbisgis.geocatalogtree.api.GeoCatalogTreeNode;
import org.orbisgis.geocatalogtree.api.PopupMenu;
import org.orbisgis.geocatalogtree.api.PopupTarget;
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Action;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.Arrays;
import java.util.List;
import org.orbisgis.tableeditorapi.TableEditableElementImpl;

/**
 * DBTree menu "Open table"
 * @author Nicolas Fortin
 */
@Component
public class DBTreeOpenTable implements PopupMenu {
    public static final String MENU_OPENTABLE = "MENU_OPENTABLE";
    private static final I18n I18N = I18nFactory.getI18n(DBTreeOpenTable.class);
    private DataManager dataManager;
    private EditorManager editorManager;

    @Override
    public List<Action> createActions(PopupTarget target) {
        GeoCatalogTreeAction openTable = new GeoCatalogTreeAction(MENU_OPENTABLE, I18N.tr("Open the attributes"),
                TableEditorIcon.getIcon("table"), EventHandler.create(ActionListener.class, this, "onOpenEditor", ""),
                target.getTree());
        openTable.addNodeTypeFilter(GeoCatalogTreeNode.NODE_TABLE);
        return Arrays.asList((Action)openTable);
    }

    @Override
    public void disposeActions(PopupTarget target, List<Action> actions) {

    }

    public void onOpenEditor(ActionEvent event) {
        Object source = event.getSource();
        if(source instanceof JMenuItem) {
            GeoCatalogTreeAction action = (GeoCatalogTreeAction)((JMenuItem) event.getSource()).getAction();
            for(GeoCatalogTreeNode node : action.getSelectedTreeNodes()) {
                if(GeoCatalogTreeNode.NODE_TABLE.equals(node.getNodeType())) {
                    TableLocation tableLocation = TableLocation.parse(node.getNodeIdentifier());
                    String tableName;
                    if(tableLocation.getCatalog().isEmpty() && tableLocation.getSchema().equalsIgnoreCase("public")){
                        tableName = tableLocation.getTable();
                    }
                    else{
                        tableName = tableLocation.toString();
                    }
                    editorManager.openEditable(new TableEditableElementImpl(tableName, dataManager));
                }
            }
        }
    }

    /**
     * @param dataManager DataManager to open Editable content
     */
    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * @param dataManager DataManager to open Editable content
     */
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    /**
     * @param editorManager Editor windows manager
     */
    @Reference
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }
    /**
     * @param editorManager Editor windows manager
     */
    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

}
