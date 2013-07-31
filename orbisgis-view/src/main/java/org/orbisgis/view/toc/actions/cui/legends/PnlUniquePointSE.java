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
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.WellKnownName;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.ConstantFormPoint;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.panels.AreaPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.OnVertexOnCentroidPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.PointPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;

/**
 * "Unique Symbol - Point" UI.
 *
 * @author Alexis Guéganno
 */
public class PnlUniquePointSE extends PnlUniqueAreaSE {
        private static final I18n I18N = I18nFactory.getI18n(PnlUniquePointSE.class);
        private int geometryType = SimpleGeometryType.ALL;
        private ContainerItemProperties[] uoms;

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolPoint uniquePoint;
        private ContainerItemProperties[] wkns;

        /**
         * Default constructor. UOM will be displayed as well as the stroke
         * configuration and the check boxes used to enable or disable stroke
         * and fill configuration panels.
         */
        public PnlUniquePointSE() {
            this(true, true);
        }

        /**
         * Builds the panel.
         *
         * @param uom           If true, the combo used to configure the
         *                      symbolizer UOM will be displayed.
         * @param displayStroke If true, the panel used to configure the
         *                      symbol's stroke will be enabled.
         */
        public PnlUniquePointSE(boolean uom,
                                boolean displayStroke) {
            super(uom, displayStroke);
        }

        @Override
        public IUniqueSymbolArea getLegend() {
                return uniquePoint;
        }

        @Override
        public void setLegend(Legend legend) {
                if (legend instanceof UniqueSymbolPoint) {
                        uniquePoint = (UniqueSymbolPoint) legend;
                        ConstantPenStroke cps = uniquePoint.getPenStroke();
                        if(cps instanceof ConstantPenStrokeLegend ){
                                setPenStrokeMemory((ConstantPenStrokeLegend) cps);
                        } else {
                                setPenStrokeMemory(new ConstantPenStrokeLegend(new PenStroke()));
                        }
                        ConstantSolidFill csf = uniquePoint.getFillLegend();
                        if(csf instanceof ConstantSolidFillLegend){
                                setSolidFillMemory((ConstantSolidFillLegend) csf);
                        } else {
                                setSolidFillMemory(new ConstantSolidFillLegend(new SolidFill()));
                        }
                        initPreview();
                        this.initializeLegendFields();
                } else {
                        throw new IllegalArgumentException("The given Legend is not"
                                + "a UniqueSymbolPoint");
                }
        }

        @Override
        public void setGeometryType(int type) {
                geometryType = type;
        }

        /**
         * Gets the {@code SimpleGeometryType} that was used to build this
         * {@code PnlUniquePointSE}.
         * @return
         */
        public int getGeometryType(){
                return geometryType;
        }

        @Override
        public void initialize(LegendContext lc) {
            initialize(lc, new UniqueSymbolPoint());
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return (geometryType & SimpleGeometryType.ALL) != 0;
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
                return "Unique symbol for points";
        }
        

        @Override
        public Legend copyLegend() {
                return new UniqueSymbolPoint();
        }

        @Override
        public void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout("wrap 2"));

                glob.add(new LinePanel(uniquePoint,
                        getPreview(),
                        I18N.tr(BORDER_SETTINGS),
                        true,
                        isUomEnabled()));

                glob.add(new PointPanel(uniquePoint,
                        getPreview(),
                        I18N.tr("New Mark"),
                        isUomEnabled(),
                        geometryType));

                glob.add(new AreaPanel(uniquePoint,
                        getPreview(),
                        I18N.tr(FILL_SETTINGS),
                        isAreaOptional));

                glob.add(getPreviewPanel(), "growx");

                this.add(glob);
        }
}
