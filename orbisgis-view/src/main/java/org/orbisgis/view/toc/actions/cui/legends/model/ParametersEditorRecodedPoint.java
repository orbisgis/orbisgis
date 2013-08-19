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

import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.legend.thematic.recode.RecodedPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.legends.ui.PnlUniquePointSE;

import java.awt.event.ActionEvent;

/**
 * @author alexis
 */
public class ParametersEditorRecodedPoint extends ParametersEditorMappedLegend<String, PointParameters> {

    /**
     * Editors for a LineParameters stored in a JTable. We'll open a dedicated dialog
     */
    public ParametersEditorRecodedPoint(){
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(EDIT)){
            RecodedPoint rl = (RecodedPoint) getMappedLegend();
            String key = (String) getCellEditorValue();
            PointParameters lp = rl.get(key);
            UniqueSymbolPoint usl = new UniqueSymbolPoint(lp);
            PnlUniquePointSE pls = new PnlUniquePointSE(usl, rl.isStrokeEnabled());
            if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
                PointParameters edited = usl.getPointParameters();
                rl.put((String)getCellEditorValue(), edited);
                fireEditingStopped();
            }
            fireEditingCanceled();
        }
    }
}
