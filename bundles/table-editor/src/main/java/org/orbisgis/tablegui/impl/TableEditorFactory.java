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

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.Editor;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.sif.edition.EditorFactory;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.concurrent.ExecutorService;

/**
 *  This factory receive the {@link TableEditableElement} and open a new editor.
 */
@Component(immediate = true)
public class TableEditorFactory implements EditorFactory {
        public static final String FACTORY_ID = "TableEditorFactory";
        private static final Logger LOGGER = LoggerFactory.getLogger("gui." + TableEditorFactory.class);
        protected final static I18n I18N = I18nFactory.getI18n(TableEditorFactory.class);
        private DataManager dataManager;
        private EditorManager editorManager;
        private ExecutorService executorService;
        private ToolboxWpsClient wpsClient = null;

        @Override
        public DockingPanelLayout makeEditableLayout(EditableElement editable) {
            if(editable instanceof TableEditableElement) {
                TableEditableElement editableTable = (TableEditableElement)editable;
                if(isEditableAlreadyOpened(editableTable)) { //Panel already created
                    LOGGER.info(I18N.tr("This data source ({0}) is already shown in an editor.",editableTable.getTableReference()));
                    return null;
                }
                return new TablePanelLayout(editableTable);
            } else {
                return null;
            }
        }

        @Reference
        public void setExecutorService(ExecutorService executorService) {
            this.executorService = executorService;
        }

        public void unsetExecutorService(ExecutorService executorService) {
            this.executorService = null;
        }

    /**
         * Set editor manager instance in order to check if a table editor is already opened
         * @param editorManager
         */
        @Reference
        public void setEditorManager(EditorManager editorManager) {
            this.editorManager = editorManager;
        }

        public void unsetEditorManager(EditorManager editorManager) {
            this.editorManager = null;
        }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    public void setInternalWpsClient(ToolboxWpsClient wpsClient) {
        this.wpsClient = wpsClient;
    }

    public void unsetInternalWpsClient(ToolboxWpsClient wpsClient) {
        this.wpsClient = null;
    }

        /**
         * @param dataManager JDBC DataManager factory
         */
        @Reference
        public void setDataManager(DataManager dataManager) {
            this.dataManager = dataManager;
        }
        /**
         * @param dataManager JDBC DataManager factory
        */
        public void unsetDataManager(DataManager dataManager) {
            this.dataManager = dataManager;
        }

        
        private boolean isEditableAlreadyOpened(EditableElement editable) {
                for(Editor editor : editorManager.getEditors()) {
                        if(editor instanceof TableEditor && editable.equals(editor.getEditableElement())) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public DockingPanelLayout makeEmptyLayout() {
                return new TablePanelLayout(dataManager);
        }

        @Override
        public boolean match(DockingPanelLayout layout) {
                return layout instanceof TablePanelLayout;
        }

        @Override
        public EditorDockable create(DockingPanelLayout layout) {
                TableEditableElement editableTable = ((TablePanelLayout)layout).getTableEditableElement();
                //Check the DataSource state
                return new TableEditor(editableTable, dataManager, editorManager, executorService, wpsClient);
        }

        @Override
        public String getId() {
                return FACTORY_ID;
        }

        @Override
        public void dispose() {
                //
        }        
}
