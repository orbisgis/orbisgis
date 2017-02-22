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
package org.orbisgis.logpanel;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.orbisgis.logpanel.api.MainLogFrame;
import org.orbisgis.logpanel.api.MainLogMenuService;
import org.orbisgis.logpanel.icons.LogPanelIcon;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogReaderService;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;



/**
 * This panel includes all Output Type panel.
 */
public class MainOutputPanel extends JPanel implements MainLogFrame {
    private static final long serialVersionUID = 1L;
    private static final I18n I18N = I18nFactory.getI18n(MainOutputPanel.class);
    private DockingPanelParameters dockingParameters = new DockingPanelParameters(); /*!< docked panel properties */
    private JTabbedPane tabbedPane;
    private AtomicBoolean initialised = new AtomicBoolean(false);
    private ActionCommands actions = new ActionCommands();


    public MainOutputPanel() {
        dockingParameters.setName("mainLog");
        dockingParameters.setTitle(I18N.tr("Output"));
        dockingParameters.setTitleIcon(LogPanelIcon.getIcon("output"));
        dockingParameters.setCloseable(true);

        //Create the action tools
        actions.addAction(new DefaultAction(MainLogMenuService.A_CLEAR_ALL,I18N.tr("Clear all"),
                I18N.tr("Clear all log panels"),LogPanelIcon.getIcon("erase"),
                EventHandler.create(ActionListener.class,this,"onClearAll"),null));
        dockingParameters.setDockActions(actions.getActions());

        this.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        //Add the tabbed pane to this panel.
        add(tabbedPane,BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
            super.addNotify();
            if(!initialised.getAndSet(true)) {
                    //The following line enables to use scrolling tabs.
                    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            }
    }

    /**
     * Get the actions manager.
     * @return actions manager.
     */
    public ActionCommands getActions() {
        return actions;
    }

    /**
     * Found the tab id of the provided sub panel
     * @param subPanel
     * @return The tab id or -1 if not found
     */
    private int getSubPanel(OutputPanel subPanel) {
        for(int tabId=0;tabId<tabbedPane.getTabCount();tabId++) {
            java.awt.Component tabComp = tabbedPane.getComponentAt(tabId);
            if(tabComp!=null && tabComp.equals(subPanel)) {
                return tabId;
            }
        }
        return -1;        
    }
    /**
     * Show the provided pannel
     * @note the subPanel must be added before
     * @param subPanel 
     */
    public void showSubPanel(OutputPanel subPanel) {
        int tabid=getSubPanel(subPanel);
        if(tabid>=0) {
            tabbedPane.setSelectedIndex(tabid);
        }
    }
    
    /**
     * The user click on clear all button
     */
    public void onClearAll() {
        for(int tabId=0;tabId<tabbedPane.getTabCount();tabId++) {
            java.awt.Component tabComp = tabbedPane.getComponentAt(tabId);
            if(tabComp instanceof OutputPanel) {
                OutputPanel panel = (OutputPanel)tabComp;
                panel.onMenuClear();
            }            
        }            
    }
    
    public void addSubPanel(String tabLabel,OutputPanel subPanel) {
        tabbedPane.addTab(tabLabel, subPanel);
    }

    public DockingPanelParameters getDockingParameters() {
        return dockingParameters;
    }

    @Override
    public JTextPane getLogTextPane(int index) {
        return ((OutputPanel)tabbedPane.getTabComponentAt(index)).getTextPane();
    }

    @Override
    public String getLogTabName(int index) {
        return tabbedPane.getTitleAt(index);
    }

    @Override
    public int getTabCount() {
        return tabbedPane.getTabCount();
    }
}
