/**
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
package org.orbisgis.view.toc.actions.cui.legends.model;

import org.orbisgis.legend.thematic.AreaParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.legend.thematic.recode.RecodedArea;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniqueAreaSE;

import java.awt.event.ActionEvent;

/**
 * @author alexis
 */
public class ParametersEditorRecodedArea extends ParametersEditorMappedLegend<String, AreaParameters> {

    /**
     * Editors for a LineParameters stored in a JTable. We'll open a dedicated dialog
     */
    public ParametersEditorRecodedArea(){
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(EDIT)){
            RecodedArea rl = (RecodedArea) getMappedLegend();
            String key = (String) getCellEditorValue();
            AreaParameters lp = rl.get(key);
            UniqueSymbolArea usl = new UniqueSymbolArea(lp);
            PnlUniqueAreaSE pls = new PnlUniqueAreaSE(false, rl.isStrokeEnabled());
            pls.setLegend(usl);
            if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
                AreaParameters edited = usl.getAreaParameters();
                rl.put((String)getCellEditorValue(), edited);
                fireEditingStopped();
            }
            fireEditingCanceled();
        }
    }
}

