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
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legends.panels.LinePanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.OnVertexOnCentroidPanel;
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
        public Legend getLegend() {
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
                        getPenStrokeMemory(),
                        true,
                        true));

                glob.add(getPointBlock(uniquePoint,
                                       I18N.tr(MARK_SETTINGS)));

                glob.add(getAreaBlock(uniquePoint.getFillLegend(),
                                      I18N.tr(FILL_SETTINGS)));

                glob.add(getPreviewPanel(), "growx");

                this.add(glob);
        }

        /**
         * Builds the UI block used to configure the fill color of the
         * symbolizer.
         * @param point
         * @param title
         * @return
         */
        public JPanel getPointBlock(UniqueSymbolPoint point, String title) {
                if(getPreview() == null && getLegend() != null){
                        initPreview();
                }

                JPanel jp = new JPanel(new MigLayout("wrap 2", COLUMN_CONSTRAINTS));
                jp.setBorder(BorderFactory.createTitledBorder(title));

                if(isUomEnabled()){
                    // If geometryType != POINT, we must let the user choose if
                    // he wants to draw symbols on centroid or on vertices.
                    if (geometryType != SimpleGeometryType.POINT) {
                        jp.add(new JLabel(I18N.tr(PLACE_SYMBOL_ON)), "span 1 2");
                        jp.add(new OnVertexOnCentroidPanel(uniquePoint, getPreview()), "span 1 2");
                    }
                    // Unit of measure - symbol size
                    jp.add(new JLabel(I18N.tr(SYMBOL_SIZE_UNIT)));
                    jp.add(getPointUomCombo(), COMBO_BOX_CONSTRAINTS);
                }

                // Well-known name
                jp.add(new JLabel(I18N.tr(SYMBOL)));
                jp.add(getWKNCombo(point), COMBO_BOX_CONSTRAINTS);
                // Mark width
                jp.add(new JLabel(I18N.tr(WIDTH)));
                jp.add(getMarkWidth(point), "growx");
                // Mark height
                jp.add(new JLabel(I18N.tr(HEIGHT)));
                jp.add(getMarkHeight(point), "growx");

                return jp;
        }

        /**
         * JSpinner to configure the width of the symbol
         * @param point
         * @return
         */

        private JSpinner getMarkWidth(UniqueSymbolPoint point){
                double initialValue = (point.getViewBoxWidth() == null)
                        ? point.getViewBoxHeight()
                        : point.getViewBoxWidth();
                SpinnerNumberModel model = new SpinnerNumberModel(
                        initialValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SPIN_STEP);
                final JSpinner jns = new JSpinner(model);
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, point, "viewBoxWidth", "source.value"));
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, getPreview(), "imageChanged"));
                return jns;
        }

        /**
         * JSpinner to configure the height of the symbol
         * @param point
         * @return
         */
        private JSpinner getMarkHeight(UniqueSymbolPoint point){
                double initialValue = (point.getViewBoxHeight() == null)
                        ? point.getViewBoxWidth()
                        : point.getViewBoxHeight();
                SpinnerNumberModel model = new SpinnerNumberModel(
                        initialValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SPIN_STEP);
                final JSpinner jns = new JSpinner(model);
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, point, "viewBoxHeight", "source.value"));
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, getPreview(), "imageChanged"));
                return jns;
        }

        /**
         * ComboBox to configure the WKN
         * @param point
         * @return
         */
        public WideComboBox getWKNCombo(ConstantFormPoint point){
                CanvasSE prev = getPreview();
                wkns = getWknProperties();
                String[] values = new String[wkns.length];
                for (int i = 0; i < values.length; i++) {
                        values[i] = wkns[i].getLabel();
                }
                final WideComboBox jcc = new WideComboBox(values);
                ActionListener acl = EventHandler.create(ActionListener.class, prev, "imageChanged");
                ActionListener acl2 = EventHandler.create(ActionListener.class, this, "updateWKNComboBox", "source.selectedIndex");
                jcc.addActionListener(acl2);
                jcc.addActionListener(acl);
                jcc.setSelectedItem(point.getWellKnownName().toUpperCase());
                return jcc;
        }


        protected ContainerItemProperties[] getWknProperties(){
                WellKnownName[] us = WellKnownName.values();
                ContainerItemProperties[] cips = new ContainerItemProperties[us.length];
                for(int i = 0; i<us.length; i++){
                        WellKnownName u = us[i];
                        ContainerItemProperties cip = new ContainerItemProperties(u.name(), u.toLocalizedString());
                        cips[i] = cip;
                }
                return cips;
        }

        /**
         * Sets the underlying graphic to use the ith element of the combobox
         * as its well-known name. Used when changing the combobox selection.
         * @param index
         */
        public void updateWKNComboBox(int index){
                ((ConstantFormPoint)getLegend()).setWellKnownName((wkns[index].getKey()));
        }


        /**
         * ComboBox to configure the unit of measure used to draw th stroke.
         * @return
         */
        protected WideComboBox getPointUomCombo(){
                CanvasSE prev = getPreview();
                uoms = getUomProperties();
                String[] values = new String[uoms.length];
                for (int i = 0; i < values.length; i++) {
                        values[i] = I18N.tr(uoms[i].toString());
                }
                final WideComboBox jcc = new WideComboBox(values);
                ActionListener acl = EventHandler.create(ActionListener.class, prev, "imageChanged");
                ActionListener acl2 = EventHandler.create(ActionListener.class, this, "updateSUComboBox", "source.selectedIndex");
                jcc.addActionListener(acl2);
                jcc.addActionListener(acl);
                jcc.setSelectedItem(((ConstantFormPoint)getLegend()).getSymbolUom().toString().toUpperCase());
                return jcc;
        }
        /**
         * Sets the underlying graphic to use the ith element of the combobox
         * as its uom. Used when changing the combobox selection.
         * @param index
         */
        public void updateSUComboBox(int index){
                ((ConstantFormPoint)getLegend()).setSymbolUom(Uom.fromString(uoms[index].getKey()));
        }
}
