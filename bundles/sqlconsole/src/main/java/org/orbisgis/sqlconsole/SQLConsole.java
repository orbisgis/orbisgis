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

import org.fife.rsta.ac.LanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sqlconsole.api.SQLConsoleEditor;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.sqlconsole.icons.SQLConsoleIcon;
import org.orbisgis.sqlconsole.ui.SQLConsolePanel;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.util.Map;

/**
 * Docking Panel implementation.
 * @author Nicolas Fortin
 */
@Component(service = EditorDockable.class, immediate = true)
public class SQLConsole implements EditorDockable, SQLConsoleEditor {
        private DockingPanelParameters dockingPanelParameters = new DockingPanelParameters();
        private SQLConsolePanel sqlPanel;
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsole.class);
        private DataSource dataSource;
        private ScriptSplitterFactory splitterFactory;
        private LanguageSupport sqlLanguageSupport;

        @Activate
        public void init() {
                sqlPanel = new SQLConsolePanel(dataSource);
                sqlPanel.setSplitterFactory(splitterFactory);
                dockingPanelParameters.setTitle(I18N.tr("SQL Console"));
                dockingPanelParameters.setTitleIcon(SQLConsoleIcon.getIcon("sql_code"));
                dockingPanelParameters.setDockActions(sqlPanel.getActions().getActions());
                // Tools that will be created later will also be set in the docking panel
                // thanks to this listener
                sqlPanel.getActions().addPropertyChangeListener(
                        new ActionDockingListener(dockingPanelParameters));
                LanguageSupport languageSupport = sqlLanguageSupport;
                if(languageSupport != null) {
                        languageSupport.install(sqlPanel.getScriptPanel());
                }
        }

        /**
         * @param dataSource JDBC DataSource
         */
        @Reference
        public void setDataSource(DataSource dataSource) {
                this.dataSource = dataSource;
        }

        /**
         * @param dataSource JDBC DataSource
         */
        public void unsetDataSource(DataSource dataSource) {
                this.dataSource = dataSource;
        }

        /**
         * @param splitterFactory The component used to split sql script into single query
         */
        @Reference
        public void setSplitterFactory(ScriptSplitterFactory splitterFactory) {
                this.splitterFactory = splitterFactory;
        }
        /**
         * @param splitterFactory The component used to split sql script into single query
         */
        public void unsetSplitterFactory(ScriptSplitterFactory splitterFactory) {
                this.splitterFactory = null;
                sqlPanel.setSplitterFactory(null);
        }

        @Reference(cardinality = ReferenceCardinality.OPTIONAL, target = "(language=sql)", policy = ReferencePolicy.DYNAMIC)
        public void setLanguageSupport(LanguageSupport languageSupport) {
                this.sqlLanguageSupport = languageSupport;
                if(sqlPanel != null) {
                        languageSupport.install(getScriptPanel());
                }
        }

        public void unsetLanguageSupport(LanguageSupport languageSupport) {
                languageSupport.uninstall(getScriptPanel());
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
