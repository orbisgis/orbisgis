
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.output;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.icons.OrbisGISIcon;



/**
 * This panel includes all Output Type panel
 */
public class MainOutputPanel extends JPanel implements DockingPanel {
    private DockingPanelParameters dockingParameters = new DockingPanelParameters(); /*!< docked panel properties */
    private JTabbedPane tabbedPane;
    public MainOutputPanel() {
        dockingParameters.setName("mainLog");
        dockingParameters.setTitle(I18N.getString("orbisgis.view.mainlog.title"));
        dockingParameters.setTitleIcon(OrbisGISIcon.getIcon("format-justify-fill"));
        dockingParameters.setCloseable(true);
        
        this.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        //Add the tabbed pane to this panel.
        add(tabbedPane,BorderLayout.CENTER);
         
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    /**
     * Found the tab id of the provided sub panel
     * @param subPanel
     * @return The tab id or -1 if not found
     */
    private int getSubPanel(OutputPanel subPanel) {
        for(int tabId=0;tabId<tabbedPane.getTabCount();tabId++) {
            Component tabComp = tabbedPane.getComponentAt(tabId);
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
    
    public void addSubPanel(String tabLabel,OutputPanel subPanel) {
        tabbedPane.addTab(tabLabel, subPanel);
    }
    public DockingPanelParameters getDockingParameters() {
        return dockingParameters;
    }

    public Component getComponent() {
        return this;
    }
    
}
