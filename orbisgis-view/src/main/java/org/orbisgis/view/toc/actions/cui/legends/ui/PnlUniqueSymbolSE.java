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
package org.orbisgis.view.toc.actions.cui.legends.ui;

import org.orbisgis.legend.Legend;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;

/**
 * Base class for "Unique Symbol" UIs.
 *
 * This class proposes some methods that will be common to all the panels built
 * for unique symbols.
 * @author Alexis Guéganno
 */
public abstract class PnlUniqueSymbolSE extends AbstractFieldPanel implements UIPanel {

        private String id;
        private CanvasSE preview;

        /**
         * Rebuild the {@code CanvasSe} instance used to display a preview of
         * the current symbol.
         */
        public void initPreview(){
                Legend leg = getLegend();
                if(leg != null){
                        preview = new CanvasSE(leg.getSymbolizer());
                        preview.imageChanged();
                }
        }

        /**
         * Gets the {@code CanvasSE} instance used to display a preview of
         * the current symbol.
         *
         * @return Preview of the symbol.
         */
        public CanvasSE getPreview(){
            if (preview == null) {
                initPreview();
            }
            return preview;
        }

        @Override
        public String getId(){
                return id;
        }

        @Override
        public void setId(String id){
                this.id = id;
        }
}
