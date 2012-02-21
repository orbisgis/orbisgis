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
package org.orbisgis.view.geocatalog;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import org.gdms.source.SourceManager;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.geocatalog.renderer.DataSourceListCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;


/**
 * @brief This is the GeoCatalog panel. That Panel show the list of avaible DataSource
 * 
 * This is connected with the SourceManager model.
 */
public class Catalog extends JPanel implements DockingPanel {
    private DockingPanelParameters dockingParameters = new DockingPanelParameters(); /*!< GeoCatalog docked panel properties */
    JPopupMenu popupMenu; /*!< Popup of GeoCatalog Source List */
    JList sourceList;
    SourceListModel sourceListContent;

    /**
     * For the Unit test purpose
     * @return The source list instance
     */
    public JList getSourceList() {
        return sourceList;
    }
    
    /**
     * Default constructor
     */
    public Catalog(SourceManager sourceManager) {
            super(new BorderLayout());
            dockingParameters.setTitle(I18N.getString("orbisgis.org.orbisgis.Catalog.title"));
            dockingParameters.setTitleIcon(OrbisGISIcon.getIcon("geocatalog"));
            //Add the Source List in a Scroll Pane, 
            //then add the scroll pane in this panel
            add(new JScrollPane(makeSourceList(sourceManager)), BorderLayout.CENTER);
            
    }
    
    /**
     * Create the Source List ui compenent
     */
    private JList makeSourceList(SourceManager sourceManager) {
        sourceList = new JList();
        //Set the list content renderer
        sourceList.setCellRenderer(new DataSourceListCellRenderer()); 
        //Create the list content manager
        sourceListContent = new SourceListModel(sourceManager); 
        //Replace the default model by the GeoCatalog model
        sourceList.setModel(sourceListContent); 
        //Attach the content to the DataSource instance
        sourceListContent.setListeners();
        return sourceList;
    }
    /**
     * Free listeners, Catalog must not be reachable to let the Garbage Collector
     * free this instance
     */
    public void dispose() {
        sourceListContent.dispose();
    }
    /**
     * Give information on the behaviour of this panel related to the current
     * docking system
     * @return The panel parameter instance
     */
    public DockingPanelParameters getDockingParameters() {
        return dockingParameters;
    }

    public Component getComponent() {
        return this;
    }
}
