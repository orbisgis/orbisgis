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

import org.orbisgis.coremap.renderer.se.fill.SolidFill;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.coremap.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.structure.fill.constant.ConstantFillLegend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.literal.RealLiteralLegend;
import org.orbisgis.legend.structure.literal.StringLiteralLegend;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;

/**
 * This class is used to represent instances of {@code PenStroke} whose
 * parameters (width, fill and dash array) are all constant.
 * @author Alexis Guéganno
 */
public class ConstantPenStrokeLegend extends ConstantColorAndDashesPSLegend implements ConstantPenStroke{

        /**
         * Build a new instance of {@code ConstantPenStrokeLegend}. Use at your own risk : if ps can't be recognize
         * as a ConstantPenStrokeLegend, you'll receive runtime exceptions...
         * @param ps
         * @throws ClassCastException if some {@code ps} fields can't be recognize as valid parameters for this legend.
         */
        public ConstantPenStrokeLegend(PenStroke ps) {
                super(ps, new RealLiteralLegend((RealLiteral)ps.getWidth()),
                            new ConstantSolidFillLegend((SolidFill) ps.getFill()),
                            new StringLiteralLegend((StringLiteral) ps.getDashArray()));
        }

        public ConstantPenStrokeLegend(){
                super();
        }

        /**
         * Build a new instance of {@code ConstantPenStrokeLegend}.
         * @param ps
         * @param width
         * @param fill
         * @param dash
         */
        public ConstantPenStrokeLegend(PenStroke ps, RealLiteralLegend width,
                        ConstantFillLegend fill, StringLiteralLegend dash) {
                super(ps, width, fill, dash);
        }

        /**
         * Get the width of the associated {@code PenStroke}.
         * @return
         */
        @Override
        public double getLineWidth() {
            return ((RealLiteralLegend) getLineWidthLegend()).getDouble();
        }

        /**
         * Set the width of the associated {@code PenStroke}.
         * @param width
         */
        @Override
        public void setLineWidth(double width) {
            ((RealLiteralLegend) getLineWidthLegend()).setDouble(width);
        }

        @Override
        public ConstantSolidFill getFillLegend() {
                return (ConstantSolidFill)getFillAnalysis();
        }
}
