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
package org.orbisgis.sqlconsole;

import javax.sql.DataSource;
import javax.swing.*;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sqlconsole.actions.LoadScript;
import org.orbisgis.sqlconsole.api.SQLAction;
import org.orbisgis.sqlconsole.api.SQLConsoleEditor;
import org.orbisgis.sqlconsole.api.SQLElement;
import org.orbisgis.sqlparserapi.ScriptSplitterFactory;
import org.orbisgis.sqlconsole.icons.SQLConsoleIcon;
import org.orbisgis.sqlconsole.ui.SQLConsolePanel;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Docking Panel implementation.
 * @author Nicolas Fortin
 */
@Component(service = SQLConsoleEditor.class, factory = SQLConsole.SERVICE_FACTORY_ID, properties = {SQLElement.PROP_DOCUMENT_PATH+"="})
public class SQLConsole implements EditorDockable, SQLConsoleEditor {
        public static final String SERVICE_FACTORY_ID = "org.orbisgis.sqlconsole.SQLConsole";
        private DockingPanelParameters dockingPanelParameters = new DockingPanelParameters();
        private SQLConsolePanel sqlPanel;
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsole.class);
        private DataSource dataSource;
        private ScriptSplitterFactory splitterFactory;
        private LanguageSupport sqlLanguageSupport;
        private ExecutorService executorService;
        private List<SQLAction> sqlActionList = new ArrayList<>();
        private SQLElement sqlElement = new SQLElement();

        @Activate
        public void init(Map<String, Object> attributes) {
                sqlPanel = new SQLConsolePanel(dataSource, sqlElement);
                sqlPanel.setSplitterFactory(splitterFactory);
                sqlPanel.setExecutorService(executorService);
                dockingPanelParameters.setName("sqlconsole");
                dockingPanelParameters.setTitle(I18N.tr("SQL Console"));
                dockingPanelParameters.setTitleIcon(SQLConsoleIcon.getIcon("sql_code"));
                dockingPanelParameters.setDockActions(sqlPanel.getActions().getActions());
                dockingPanelParameters.setDefaultDockingLocation(new DockingLocation(DockingLocation.Location.STACKED_ON
                        ,SQLConsoleFactory.class.getSimpleName()));
                // Tools that will be created later will also be set in the docking panel
                // thanks to this listener
                sqlPanel.getActions().addPropertyChangeListener(
                        new ActionDockingListener(dockingPanelParameters));
                for(SQLAction sqlAction : sqlActionList) {
                        sqlPanel.addActionFactory(sqlAction, this);
                }
                LanguageSupport languageSupport = sqlLanguageSupport;
                if(languageSupport != null) {
                        languageSupport.install(sqlPanel.getScriptPanel());
                }
                setEditableElement((SQLElement)attributes.get("editableElement"));
        }

        @Deactivate
        public void close() {
            try {
                sqlElement.close(new NullProgressMonitor());
            } catch (EditableElementException ex) {
                // Ignore
            }
        }

        /**
         * @param dataSource JDBC DataSource
         */
        @Reference
        public void setDataSource(DataSource dataSource) {
                this.dataSource = dataSource;
        }

        @Reference
        public void setExecutorService(ExecutorService executorService) {
                this.executorService = executorService;
        }

        public void unsetExecutorService(ExecutorService executorService) {
                this.executorService = null;
                if(sqlPanel != null) {
                        sqlPanel.unsetExecutorService(executorService);
                }
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

        @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption =
                ReferencePolicyOption.GREEDY)
        public void addActionFactory(SQLAction sqlAction) {
                sqlActionList.add(sqlAction);
                if(sqlPanel != null) {
                        sqlPanel.addActionFactory(sqlAction, this);
                }
        }

        public void removeActionFactory(SQLAction sqlAction) {
                sqlActionList.remove(sqlAction);
                if(sqlPanel != null) {
                        sqlPanel.removeActionFactory(sqlAction);
                }
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
                return sqlElement;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
            if(editableElement instanceof SQLElement) {
                this.sqlElement = (SQLElement) editableElement;
                sqlElement.setDocument(sqlPanel.getScriptPanel());
                sqlElement.addPropertyChangeListener(SQLElement.PROP_DOCUMENT_PATH,
                        EventHandler.create(PropertyChangeListener.class, this , "onPathChanged"));
                onPathChanged();
                LoadScript loadScript = new LoadScript(sqlElement);
                if(executorService != null) {
                    executorService.execute(loadScript);
                } else {
                    loadScript.execute();
                }
                if( sqlPanel != null) {
                    sqlPanel.setSqlElement(sqlElement);
                }
            }
        }

        public void onPathChanged() {
            if(!sqlElement.getDocumentPathString().isEmpty()) {
                dockingPanelParameters.setTitle(sqlElement.getDocumentPath().getName());
            }
        }

        @Override
        public JTextArea getTextArea() {
            return sqlPanel.getScriptPanel();
        }

        public RSyntaxTextArea getScriptPanel() {
            return sqlPanel.getScriptPanel();
        }
}
