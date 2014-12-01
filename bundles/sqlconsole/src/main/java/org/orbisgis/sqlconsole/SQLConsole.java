/*
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
package org.orbisgis.sqlconsole;

import javax.sql.DataSource;
import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionDockingListener;
import org.orbisgis.sqlconsole.icons.SQLConsoleIcon;
import org.orbisgis.viewapi.docking.DockingPanelParameters;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.viewapi.edition.EditorDockable;
import org.orbisgis.sqlconsole.ui.SQLConsolePanel;
import org.orbisgis.viewapi.sqlconsole.ui.ext.SQLConsoleEditor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Docking Panel implementation.
 * @author Nicolas Fortin
 */
public class SQLConsole implements EditorDockable,SQLConsoleEditor {
        private DockingPanelParameters dockingPanelParameters = new DockingPanelParameters();
        private SQLConsolePanel sqlPanel;
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsole.class);
        
        public SQLConsole(DataSource dataSource) {
                sqlPanel = new SQLConsolePanel(dataSource);
                dockingPanelParameters.setTitle(I18N.tr("SQL Console"));
                dockingPanelParameters.setTitleIcon(SQLConsoleIcon.getIcon("sql_code"));
                dockingPanelParameters.setDockActions(sqlPanel.getActions().getActions());
                // Tools that will be created later will also be set in the docking panel
                // thanks to this listener
                sqlPanel.getActions().addPropertyChangeListener(
                        new ActionDockingListener(dockingPanelParameters));
        }

        /**
         * @param splitterFactory The component used to split sql script into single query
         */
        public void setSplitterFactory(ScriptSplitterFactory splitterFactory) {
            sqlPanel.setSplitterFactory(splitterFactory);
        }

        /**
         * Get the ActionCommands instance use by SQLConsole.
         * @return ActionCommands instance
         */
        public ActionCommands getActions() {
            return sqlPanel.getActions();
        }
        /**
         * Free sql console resources
         */
        public void dispose() {
                sqlPanel.freeResources();
        }
        
        @Override
        public DockingPanelParameters getDockingParameters() {
                return dockingPanelParameters;
        }

        @Override
        public JComponent getComponent() {
                return sqlPanel;
        }

        @Override
        public boolean match(EditableElement editableElement) {
                return editableElement instanceof MapElement;
        }

        @Override
        public EditableElement getEditableElement() {
                return null;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
        }

        @Override
        public JTextArea getTextArea() {
            return sqlPanel.getScriptPanel();
        }

        public RSyntaxTextArea getScriptPanel() {
            return sqlPanel.getScriptPanel();
        }
}
