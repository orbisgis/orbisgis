/**
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
package org.orbisgis.logpanel;

import java.beans.EventHandler;
import java.util.HashMap;
import java.util.Map;

import org.orbisgis.logpanel.api.MainLogMenuService;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComponent;

/**
 * The output manager, create then link/unlink appender with LOG4J.
 */
@Component(service = DockingPanel.class, immediate = true)
public class OutputManager implements DockingPanel {
        private static final I18n I18N = I18nFactory.getI18n(OutputManager.class);
        public static final String LOG_INFO = "output_info";
        public static final String LOG_ALL = "output_all";
        public static final String LOG_ERROR = "output_error";
        public static final String LOG_WARNING = "output_warning";
        public static final String LOG_DEBUG = "output_debug";
        
        private Map<String, PanelAppender> outputPanels = new HashMap<String, PanelAppender>();
        private MainOutputPanel mainPanel;
        //All panel additional objects
        private PanelAppender.ShowMessageListener outputAllListener;
        private OutputPanel allPanel;
        private LogReaderService logReaderService;

        @Activate
        public void init(Map<String, String> properties) {
                mainPanel = new MainOutputPanel();
                boolean debugConsole = "True".equals(properties.get("logpanel.debug"));
                makeOutputAll(debugConsole);
                if (debugConsole) {
                        makeOutputDebug();
                }
                makeOutputInfo();
                makeOutputWarning();
                makeOutputError();
        }

        @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
        public void addLogMenu(MainLogMenuService logMenuService) {
                mainPanel.getActions().addActionFactory(logMenuService, mainPanel);
        }

        public void removeLogMenu(MainLogMenuService logMenuService) {
                mainPanel.getActions().removeActionFactory(logMenuService);
        }

        @Reference
        public void setLogReaderService(LogReaderService logReaderService) {
                this.logReaderService = logReaderService;
        }

        public void unsetLogReaderService(LogReaderService logReaderService) {
                this.logReaderService = null;
                for (PanelAppender appender : outputPanels.values()) {
                        logReaderService.removeLogListener(appender);
                }
        }

        /**
         * Make the All Output panel
         * This panel accept root     <= Warning   >
         *                   root.gui <= Info      >
         */
        private void makeOutputAll(boolean showDebug) {
                PanelAppender app = makePanel(LogService.LOG_DEBUG, LogService.LOG_ERROR);
                allPanel = app.getGuiPanel();
                outputAllListener = EventHandler.create(PanelAppender.ShowMessageListener.class,this,"onNewLogMessage","");
                outputPanels.put(LOG_ALL, app);
                mainPanel.addSubPanel(I18N.tr("All"), app.getGuiPanel());
                mainPanel.showSubPanel(app.getGuiPanel()); //Select this panel by default                
        }

        public void onNewLogMessage(ShowMessageEventData evtMsg) {
            allPanel.print(evtMsg.getMessage(), evtMsg.getMessageColor());
        }
        /**
         * Make the Error Output panel
         * This panel accept root == Error      >
         */
        private void makeOutputError() {
                PanelAppender app = makePanel(LogService.LOG_ERROR, LogService.LOG_ERROR);
                app.getMessageEvent().addListener(this, outputAllListener);
                outputPanels.put(LOG_ERROR, app);
                logReaderService.addLogListener(app);
                mainPanel.addSubPanel(I18N.tr("Errors"), app.getGuiPanel());
        }

        private PanelAppender makePanel(int levelMin, int levelMax) {
                PanelAppender app = new PanelAppender(new OutputPanel(), levelMin, levelMax);
                return app;
        }

        /**
         * Make the Info Output panel
         * This panel accept root.gui == Info      >
         */
        private void makeOutputInfo() {
                PanelAppender app = makePanel(LogService.LOG_INFO, LogService.LOG_INFO);
                app.getMessageEvent().addListener(this, outputAllListener);
                outputPanels.put(LOG_INFO, app);
                logReaderService.addLogListener(app);
                mainPanel.addSubPanel(I18N.tr("Infos"), app.getGuiPanel());
        }

        /**
         * Make the warning Output panel
         * This panel accept root.gui == Info      >
         */
        private void makeOutputWarning() {
                PanelAppender app = makePanel(LogService.LOG_WARNING, LogService.LOG_WARNING);
                app.getMessageEvent().addListener(this, outputAllListener);
                outputPanels.put(LOG_WARNING, app);
                logReaderService.addLogListener(app);
                mainPanel.addSubPanel(I18N.tr("Warnings"), app.getGuiPanel());
        }
        /**
         * Make the debug Output panel
         * This panel accept root == Debug      >
         */
        private void makeOutputDebug() {
                PanelAppender app = makePanel(LogService.LOG_DEBUG, LogService.LOG_DEBUG);
                app.getMessageEvent().addListener(this, outputAllListener);
                outputPanels.put(LOG_DEBUG, app);
                logReaderService.addLogListener(app);
                mainPanel.addSubPanel(I18N.tr("Debug"), app.getGuiPanel());
        }

        @Override
        public DockingPanelParameters getDockingParameters() {
                return mainPanel.getDockingParameters();
        }

        @Override
        public JComponent getComponent() {
                return mainPanel;
        }
}
