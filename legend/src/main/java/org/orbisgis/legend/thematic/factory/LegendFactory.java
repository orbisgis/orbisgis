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
package org.orbisgis.legend.thematic.factory;

import org.orbisgis.coremap.renderer.se.AreaSymbolizer;
import org.orbisgis.coremap.renderer.se.LineSymbolizer;
import org.orbisgis.coremap.renderer.se.PointSymbolizer;
import org.orbisgis.coremap.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.analyzer.symbolizers.AreaSymbolizerAnalyzer;
import org.orbisgis.legend.analyzer.symbolizers.LineSymbolizerAnalyzer;
import org.orbisgis.legend.analyzer.symbolizers.PointSymbolizerAnalyzer;

/**
 * Factory dedicated to the generation of {@code Legend} instances. It uses
 * {@code Symbolizers} in input, and produce the corresponding {@code Legend}.
 * @author Alexis Guéganno
 */
public final class LegendFactory {

        private LegendFactory(){}

        /**
         * Try to build a {@code Legend} instance from the given {@code
         * Symbolizer}.
         * @param sym
         * @return
         */
        public static Legend getLegend(Symbolizer sym){
                if(sym instanceof PointSymbolizer){
                        return getLegend((PointSymbolizer) sym);
                } else if(sym instanceof LineSymbolizer){
                        return getLegend((LineSymbolizer) sym);
                } else if(sym instanceof AreaSymbolizer){
                        return getLegend((AreaSymbolizer) sym);
                } else {
                        throw new UnsupportedOperationException("not supported yet");
                }

        }

        private static Legend getLegend(PointSymbolizer sym){
                PointSymbolizerAnalyzer psa = new PointSymbolizerAnalyzer(sym);
                return (Legend) psa.getLegend();
        }

        private static Legend getLegend(LineSymbolizer sym){
                LineSymbolizerAnalyzer psa = new LineSymbolizerAnalyzer(sym);
                return (Legend) psa.getLegend();
        }

        private static Legend getLegend(AreaSymbolizer sym){
                AreaSymbolizerAnalyzer psa = new AreaSymbolizerAnalyzer(sym);
                return (Legend) psa.getLegend();
        }

}
