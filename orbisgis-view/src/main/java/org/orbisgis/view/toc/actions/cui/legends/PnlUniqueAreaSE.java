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
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legends.panels.AreaPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.net.URL;

/**
 * "Unique Symbol - Area" UI.
 *
 * This panel is used to configure a unique symbol configuration for an area symbolizer.
 * @author Alexis Guéganno
 */
public class PnlUniqueAreaSE extends PnlUniqueLineSE {
        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueAreaSE.class);
        private ConstantSolidFillLegend solidFillMemory;
        public static final String FILL_SETTINGS = I18n.marktr("Fill settings");

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolArea uniqueArea;
        protected boolean isAreaOptional;

        /**
         * Default constructor. UOM will be displayed as well as the stroke
         * configuration and the check boxes used to enable or disable stroke
         * and fill configuration panels.
         */
        public PnlUniqueAreaSE(){
            this(true);
        }

        /**
         * Constructor that is used to set if the UOM must be displayed or not.
         *
         * @param uom If true, the uom will be displayed.
         */
        public PnlUniqueAreaSE(boolean uom){
            this(uom, true);
        }

        /**
         * Builds the panel.
         *
         * @param uom            If true, the combo used to configure the
         *                       symbolizer UOM will be displayed.
         * @param isAreaOptional If true,  the two boxes that are used to enable
         *                       and disable the stroke and fill of the symbol
         *                       will be displayed.
         */
        public PnlUniqueAreaSE(boolean uom,
                               boolean isAreaOptional){
            super(uom);
            this.isAreaOptional = isAreaOptional;
        }

        @Override
        public IUniqueSymbolArea getLegend() {
                return uniqueArea;
        }

        @Override
        public void setLegend(Legend legend) {
                if (legend instanceof UniqueSymbolArea) {
                        uniqueArea = (UniqueSymbolArea) legend;
                        ConstantPenStroke cps = uniqueArea.getPenStroke();
                        if(cps instanceof ConstantPenStrokeLegend ){
                                setPenStrokeMemory((ConstantPenStrokeLegend) cps);
                        } else {
                                setPenStrokeMemory(new ConstantPenStrokeLegend(new PenStroke()));
                        }
                        ConstantSolidFill csf = uniqueArea.getFillLegend();
                        if(csf instanceof ConstantSolidFillLegend){
                                setSolidFillMemory((ConstantSolidFillLegend) csf);
                        } else {
                            setSolidFillMemory(new ConstantSolidFillLegend(new SolidFill()));
                        }
                        initPreview();
                        this.initializeLegendFields();
                } else {
                        throw new IllegalArgumentException("The given Legend is not"
                                + "a UniqueSymbolArea");
                }
        }

        /**
         * Initialize the panel. This method is called just after the panel
         * creation.
         *
         * @param lc LegendContext is useful to get some information about the
         * layer in edition.
         */
        @Override
        public void initialize(LegendContext lc) {
            initialize(lc, new UniqueSymbolArea());
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.POLYGON||
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
                UniqueSymbolArea ret = new UniqueSymbolArea();
                ret.getFillLegend().setColor(uniqueArea.getFillLegend().getColor());
                return ret;
        }

        @Override
        public void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout());

                glob.add(new LinePanel(uniqueArea,
                        getPreview(),
                        I18N.tr(BORDER_SETTINGS),
                        true,
                        true),
                        "cell 0 0, span 1 2, aligny top");

                glob.add(new AreaPanel(uniqueArea,
                        getPreview(),
                        I18N.tr(FILL_SETTINGS),
                        isAreaOptional),
                        "cell 1 0, growx");

                glob.add(getPreviewPanel(), "cell 1 1, growx");
                this.add(glob);
        }

    /**
         * In order to improve the user experience, it may be interesting to
         * store the {@code ConstantSolidFillLegend} as a field before removing
         * it. This way, we will be able to use it back directly... unless the
         * editor as been closed before, of course.
         * @param fill The fill we want to keep in memory.
         */
        protected void setSolidFillMemory(ConstantSolidFillLegend fill){
                solidFillMemory = fill;
        }
}
