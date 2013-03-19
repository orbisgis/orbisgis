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
package org.orbisgis.legend.structure.fill;

import java.beans.EventHandler;
import java.util.LinkedList;
import java.util.List;

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.parameter.SeParameter;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.legend.structure.recode.RecodedColor;
import org.orbisgis.legend.structure.recode.RecodedLegend;
import org.orbisgis.legend.structure.recode.RecodedLegendStructure;
import org.orbisgis.legend.structure.recode.RecodedReal;
import org.orbisgis.legend.structure.recode.type.TypeListener;

/**
 * A {@code Legend} that represents a {@code SolidFill} where the color is defined
 * according to a {@code Recode} operation.
 * @author Alexis Guéganno
 */
public class RecodedSolidFillLegend extends SolidFillLegend implements RecodedLegendStructure {

        /**
         * Build a new {@code RecodedSolidFill} from the given {@link SolidFill}. If it can't be recognized as a {@code
         * RecodedSolidFill}, you'll receive {@link UnsupportedOperationException} or {@link ClassCastException}.
         * @param fill
         */
        public RecodedSolidFillLegend(SolidFill fill){
            this(fill, new RecodedColor(fill.getColor()), new RecodedReal(fill.getOpacity()));
        }

        /**
         * Build a new {@code CategorizedSolidFillLegend} using the {@code 
         * SolidFill} and {@code Recode2ColorLegend} given in parameter.
         * @param fill The fill associated to this {@code RecodedSolidFillLegend}.
         * @param colorLegend The representation of the color
         * @param opacity The representation of the opacity
         */
        public RecodedSolidFillLegend(SolidFill fill, RecodedColor colorLegend, RecodedReal opacity) {
                super(fill, colorLegend, opacity);
                TypeListener tl = EventHandler.create(TypeListener.class, this, "replaceColor", "getSource.getParameter");
                colorLegend.addListener(tl);
                TypeListener tlZ = EventHandler.create(TypeListener.class, this, "replaceOpacity", "getSource.getParameter");
                opacity.addListener(tlZ);
        }

        /**
         * Replace the {@code ColorParameter} embedded in the inner SolidFill with {@code sp}. This method is called
         * when a type change occurs in the associated {@link RecodedColor} happens.
         * @param sp The new {@code ColorParameter}
         * @throws ClassCastException if sp is not a {@code ColorParameter}
         */
        public void replaceColor(SeParameter sp){
                SolidFill sf = getFill();
                sf.setColor((ColorParameter) sp);
        }

        /**
         * Replace the {@code RealParameter} embedded in the inner SolidFill with {@code sp}. This method is called
         * when a type change occurs in the associated {@link RecodedReal} happens.
         * @param sp The new {@code RealParameter}
         * @throws ClassCastException if sp is not a {@code RealParameter}
         */
        public void replaceOpacity(SeParameter sp){
                SolidFill sf = getFill();
                sf.setOpacity((RealParameter) sp);
        }

        @Override
        public List<RecodedLegend> getRecodedLegends() {
            LinkedList<RecodedLegend> ret = new LinkedList<RecodedLegend>();
            ret.add((RecodedColor)getFillColorLegend());
            ret.add((RecodedReal)getFillOpacityLegend());
            return ret;
        }
}
