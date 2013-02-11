/*
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
package org.orbisgis.legend.analyzer.symbolizers;

import org.orbisgis.core.renderer.se.common.Halo;
import org.orbisgis.core.renderer.se.fill.Fill;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.graphic.MarkGraphic;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 * This class extends {@link ParametersAnalyzer} and provides the ability to
 * validate some input {@code SymbolizerNode} by checking their type and the
 * type of some of their children.
 * @author Alexis Guéganno
 */
public class SymbolizerTypeAnalyzer extends ParametersAnalyzer {

        /**
         * A {@link Fill} is valid if and only if it is a {@code SolidFill}
         * instance.
         * @param f
         * @return {@code f instanceof SolidFill}.
         */
        public boolean validateFill(Fill f){
                return f instanceof SolidFill;
        }

        /**
         * A {@link Stroke} is valid if and only if it is a {@code PenStroke}
         * built with a valid {@code Fill} instance.
         * @param s
         * @return
         */
        public boolean validateStroke(Stroke s){
                if(s instanceof PenStroke){
                        return validateFill(((PenStroke)s).getFill());
                } else {
                        return false;
                }
        }

        /**
         * Validate the given fill and stroke. Both {@code f} and {@code s} can be null. If they are not, they must be
         * valid using either {@code validateStroke} or {@code validateFill}.
         * @param f
         * @param s
         * @return
         */
        public boolean validateStrokeAndFill(Stroke s, Fill f){
            return (f == null || validateFill(f)) && (s ==  null || validateStroke(s));
        }

        /**
         * A {@code Graphic} is valid if and only if it is an instance of {@code
         * MarkGraphic} built with both valid {@code Stroke} and {@code Fill}.
         * @param g
         * @return
         */
        public boolean validateGraphic(Graphic g){
                //Halo validation and management have to be improved
                if(g instanceof MarkGraphic){
                        MarkGraphic mg = (MarkGraphic) g;
                        Halo h = mg.getHalo();
                        boolean b = validateStrokeAndFill(mg.getStroke(),mg.getFill());
                        if(h!=null){
                                return b && validateFill(h.getFill());
                        } else {
                                return b;
                        }
                } else {
                        return false;
                }
        }

}
