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
package org.orbisgis.view.toc.actions.cui.legend.model;

import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.PointParameters;
import org.orbisgis.legend.thematic.categorize.CategorizedPoint;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.legend.ui.PnlUniquePointSE;

import java.awt.event.ActionEvent;

/**
 * This editor is used to change the values stored in a Map of type CategorizedPoint. It will let the user handle a
 * PointParameters instance in a dedicated UI, similar to the one used for unique symbols.
 * @author alexis
 */
public class ParametersEditorCategorizedPoint extends ParametersEditorMappedLegend<Double, PointParameters> {

    /**
     * Editors for a LineParameters stored in a JTable. We'll open a dedicated dialog
     */
    public ParametersEditorCategorizedPoint(){
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(EDIT)){
            CategorizedPoint cp = (CategorizedPoint) getMappedLegend();
            Double key = (Double) getCellEditorValue();
            PointParameters lp = cp.get(key);
            UniqueSymbolPoint usl = new UniqueSymbolPoint(lp);
            if(!cp.isStrokeEnabled()){
                usl.setPenStroke(new NullPenStrokeLegend());
            }
            PnlUniquePointSE pls = new PnlUniquePointSE(usl, cp.isStrokeEnabled());
            if(UIFactory.showDialog(new UIPanel[]{pls}, true, true)){
                PointParameters edited = usl.getPointParameters();
                cp.put((Double) getCellEditorValue(), edited);
                fireEditingStopped();
            }
            fireEditingCanceled();
        }
    }
}
