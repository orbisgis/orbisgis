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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.icons.OrbisGISIcon;


/**
 * @brief This is the GeoCatalog panel. That Panel show the list of avaible layers
 * 
 * It is connected with the layer collection model.
 */
public class Catalog extends JPanel implements DockingPanel {
    private DockingPanelParameters dockingParameters = new DockingPanelParameters();

    public Catalog() {
            dockingParameters.setTitle(I18N.getString("orbisgis.org.orbisgis.Catalog.title"));
            dockingParameters.setTitleIcon(OrbisGISIcon.getIcon("geocatalog"));
            this.setLayout(new BorderLayout());
            this.add(new JScrollPane(), BorderLayout.CENTER);
    }

    public DockingPanelParameters getDockingParameters() {
        return dockingParameters;
    }
}
