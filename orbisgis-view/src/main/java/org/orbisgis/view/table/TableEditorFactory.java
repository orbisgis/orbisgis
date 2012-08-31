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
package org.orbisgis.view.table;

import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.view.docking.DockingPanelLayout;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.edition.MultipleEditorFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *  This factory receive the {@link TableEditableElement} and open a new editor.
 */
public class TableEditorFactory implements MultipleEditorFactory {
        public static final String FACTORY_ID = "TableEditorFactory";
        private static final Logger LOGGER = Logger.getLogger("gui."+TableEditorFactory.class);
        protected final static I18n I18N = I18nFactory.getI18n(TableEditorFactory.class);
        
        @Override
        public DockingPanelLayout makeEditableLayout(EditableElement editable) {
                if(editable instanceof TableEditableElement) {
                        TableEditableElement editableTable = (TableEditableElement)editable;
                        if(isEditableAlreadyOpened(editableTable)) { //Panel already created
                                LOGGER.info(I18N.tr("This data source ({0}) is already shown in an editor.",editableTable.getSourceName()));
                                return null;
                        }
			if(dataSourceCheck(editableTable.getSourceName())) {
                                return new TablePanelLayout(editableTable);
                        }else {
                                LOGGER.info(I18N.tr("In a consequence of an unreachable data source {0},the associated data editor could not be recovered."));
                                return null;
                        }
                } else {
                        return null;
                }
        }
        
        private boolean dataSourceCheck(String sourceName) {
                DataManager dataManager = Services.getService(DataManager.class);
                if(!dataManager.getSourceManager().exists(sourceName)) {
                        return false;
                }
                try {
                        DataSource source = dataManager.getDataSourceFactory().getDataSource(sourceName);
                        if(!source.isOpen()) {
                                source.open();
                                source.close();
                        }
                } catch (NoSuchTableException ex) {
                        return false;
                } catch (DataSourceCreationException ex) {
                        return false;
                } catch (DriverException ex) {
                        return false;
                }
                return true;
        }
        
        private boolean isEditableAlreadyOpened(EditableElement editable) {
                EditorManager em = Services.getService(EditorManager.class);
                for(EditorDockable editor : em.getEditors()) {
                        if(editor instanceof TableEditor && editable.equals(editor.getEditableElement())) {
                                return true;
                        }
                }
                return false;
        }

        @Override
        public DockingPanelLayout makeEmptyLayout() {
                return new TablePanelLayout();
        }

        @Override
        public boolean match(DockingPanelLayout layout) {
                return layout instanceof TablePanelLayout;
        }

        @Override
        public EditorDockable create(DockingPanelLayout layout) {
                TableEditableElement editableTable = ((TablePanelLayout)layout).getTableEditableElement();
                //Check the DataSource state                
                if(!dataSourceCheck(editableTable.getSourceName())) {
                        LOGGER.info(I18N.tr("In a consequence of an unreachable data source {0},the associated data editor could not be recovered.",editableTable.getSourceName()));
                        return null;
                } else {
                        return new TableEditor(editableTable);
                }
        }

        @Override
        public DockingPanelLayout getLayout(EditorDockable panel) {
                return new TablePanelLayout(((TableEditor)panel).getTableEditableElement());
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
