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
package org.orbisgis.legend.structure.stroke.constant;

import java.awt.Color;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.stroke.Stroke;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;

/**
 * A {@link PenStroke} that is null can be considered as a constant : it is not dependant upon any
 * parameter and will always be (not) rendered the same way. It can happen in a {@link AreaSymbolizer} where
 * only the {@code Fill} is set.
 * @author Alexis Guéganno
 */
public class NullPenStrokeLegend implements ConstantPenStroke {

    @Override
    public ConstantSolidFill getFillLegend() {
        return new NullSolidFillLegend();
    }

    @Override
    public Color getLineColor() {
        return  null;
    }

    @Override
    public Stroke getStroke() {
        return null;
    }

    @Override
    public void setLineColor(Color col) {}

    @Override
    public double getLineWidth() {
        return  0;
    }

    @Override
    public void setLineWidth(double width) {}

    @Override
    public String getDashArray() {
        return "";
    }

    @Override
    public void setDashArray(String str) {}

    @Override
    public double getLineOpacity() {
        return 0;
    }

    @Override
    public void setLineOpacity(double tr) {}

    @Override
    public Uom getStrokeUom() {
        return Uom.PX;
    }

    @Override
    public void setStrokeUom(Uom u){}

}
