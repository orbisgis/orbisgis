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
package org.orbisgis.view.toc.actions.cui.legends;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.net.URL;

/**
 * "Unique Symbol - Line" UI.
 *
 * {@code JPanel} that ca nbe used to configure simple constant {@code
 * LineSymbolizer} instances that have been recognized as unique symbols made
 * justof one simple {@code PenStroke}.
 * @author Alexis Guéganno
 */
public class PnlUniqueLineSE extends PnlUniqueSymbolSE {
        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueLineSE.class);

        private ConstantPenStrokeLegend penStrokeMemory;
        private final boolean displayUom;

        public static final String LINE_SETTINGS = I18n.marktr("Line settings");
        public static final String BORDER_SETTINGS = I18n.marktr("Border settings");
        public static final String MARK_SETTINGS = I18n.marktr("Mark settings");

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolLine uniqueLine;

        /**
         * Default constructor. The UOM combo box is displayed.
         */
        public PnlUniqueLineSE() {
            this(true);
        }

        /**
         * Builds a new PnlUniqueLineSE choosing if we want to display the uom combo box.
         *
         * @param uom if true, the uom combo box will be displayed.
         */
        public PnlUniqueLineSE(boolean uom){
            super();
            this.displayUom = uom;
        }

        /**
         * Returns true if the combo box used to configure the unit of measures must be displayed, false if they must not.
         * @return true if the combo box used to configure the unit of measures must be displayed, false if they must not.
         */
        protected boolean isUomEnabled(){
            return displayUom;
        }

        @Override
        public Legend getLegend() {
                return uniqueLine;
        }

        @Override
        public void setLegend(Legend legend) {
                if (legend instanceof UniqueSymbolLine) {
                        uniqueLine = (UniqueSymbolLine) legend;
                        if(uniqueLine.getPenStroke() instanceof ConstantPenStrokeLegend){
                                penStrokeMemory = (ConstantPenStrokeLegend) uniqueLine.getPenStroke();
                        } else {
                                penStrokeMemory = new ConstantPenStrokeLegend(new PenStroke());
                        }
                        initPreview();
                        initializeLegendFields();
                } else {
                        throw new IllegalArgumentException("The given Legend is not"
                                + "a UniqueSymbolLine");
                }
        }

        @Override
        public void setGeometryType(int type){
        }

        /**
         * Initialize the panel. This method is called just after the panel
         * creation.
         * @param lc LegendContext is useful to get some information about the
         * layer in edition.
         */
        @Override
        public void initialize(LegendContext lc) {
            initialize(lc, new UniqueSymbolLine());
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.LINE ||
                        geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
        }

        @Override
        public String validateInput() {
                return null;
        }

        @Override
        public URL getIconURL() {
                return UIFactory.getDefaultIcon();
        }

        @Override
        public String getTitle() {
                return "Unique symbol for lines";
        }

        @Override
        public Legend copyLegend() {
                UniqueSymbolLine usl = new UniqueSymbolLine();
                usl.getPenStroke().setDashArray(uniqueLine.getPenStroke().getDashArray());
                usl.getPenStroke().setLineWidth(uniqueLine.getPenStroke().getLineWidth());
                usl.getPenStroke().setLineColor(uniqueLine.getPenStroke().getLineColor());
                return usl;
        }

        /**
         * In order to improve the user experience, it may be interesting to
         * store the {@code ConstantPenStrokeLegend} as a field before removing
         * it. This way, we will be able to use it back directly... unless the
         * editor as been closed before, of course.
         * @param cpsl
         */
        protected void setPenStrokeMemory(ConstantPenStrokeLegend cpsl){
                penStrokeMemory = cpsl;
        }

        @Override
        public void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout());
                glob.add(new LinePanel(uniqueLine,
                        getPreview(),
                        I18N.tr(LINE_SETTINGS),
                        false,
                        displayUom));
                glob.add(getPreviewPanel());
                this.add(glob);
        }
}
