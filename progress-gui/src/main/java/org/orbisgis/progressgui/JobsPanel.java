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
package org.orbisgis.progressgui;

import java.awt.BorderLayout;
import java.beans.EventHandler;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataListener;

import org.orbisgis.progressgui.icons.JobsIcon;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This panel shows the list of active jobs.
 */
public class JobsPanel extends JPanel implements DockingPanel {
        public static final String PANEL_NAME = "jobslist";
        private static final I18n I18N = I18nFactory.getI18n(JobsPanel.class);
        private static final Logger LOGGER = LoggerFactory.getLogger("gui." + JobsPanel.class);
        private DockingPanelParameters dockingParameters = new DockingPanelParameters();
        private JobListPanel jobList;
        private JobListCellRenderer jobListRender;
        private JobListModel jobListModel;
        
        public JobsPanel() {
                super(new BorderLayout());
                dockingParameters.setName(PANEL_NAME);
                dockingParameters.setTitle(I18N.tr("Running jobs"));
                dockingParameters.setTitleIcon(JobsIcon.getIcon("completion_local"));
                makeJobList();
                add(new JScrollPane(jobList), BorderLayout.CENTER);             
        }

        @Override
        public void removeNotify() {
                jobListModel.dispose();
                super.removeNotify();
        }

        
        
        private void makeJobList() {
                jobList = new JobListPanel();
                jobListRender = new JobListCellRenderer();
                jobList.setRenderer(jobListRender);
                jobListModel = new JobListModel().listenToBackgroundManager();
                jobList.setModel(jobListModel);
                jobList.getModel().addListDataListener(EventHandler.create(ListDataListener.class,this,"onListContentChanged"));
        }
        
        /**
         * The list content has been updated,
         * the panel title label must be updated
         */
        public void onListContentChanged() {
                if(jobList!=null && jobList.getModel()!=null) {
                        dockingParameters.setTitle(I18N.tr("Running jobs ({0})",jobList.getModel().getSize()));
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
}
