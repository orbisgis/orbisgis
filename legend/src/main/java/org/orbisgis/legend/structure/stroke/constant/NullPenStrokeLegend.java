/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
import org.orbisgis.core.renderer.se.stroke.Stroke;
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

}
