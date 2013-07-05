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
import org.orbisgis.core.renderer.se.PointSymbolizer;
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
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;

/**
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
         * Default constructor. UOM will be displayed as well as the stroke configuration and the check boxes used to
         * enable or disable stroke and fill configuration panels.
         */
        public PnlUniquePointSE(){
            this(true, true, true);
        }

        /**
         * Builds the panel.
         * @param uomAndOnVertex If true, the combo used to configure the symbolizer UOM and the radio buttons used to
         *                       decide if the symbol must be displayed on vertices or on centroid will be displayed.
         * @param displayStroke If true, the panel used to configure the symbol's stroke will be enabled.
         * @param displayBoxes If true,  the two boxes that are used to enable and disable the stroke and fill of
         *                     the symbol will be displayed.
         */
        public PnlUniquePointSE(boolean uomAndOnVertex, boolean displayStroke, boolean displayBoxes){
            super(uomAndOnVertex,displayStroke,displayBoxes);
        }

        @Override
        public Component getComponent() {
                return this;
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

        /**
         * Initialize the panel. This method is called just after the panel
         * creation.</p> <p>WARNING : the panel will be empty after calling this
         * method. Indeed, there won't be any {@code Legend} instance associated
         * to it. Use the
         * {@code setLegend} method to achieve this goal.
         *
         * @param lc LegendContext is useful to get some information about the
         * layer in edition.
         */
        @Override
        public void initialize(LegendContext lc) {
                if (uniquePoint == null) {
                        setLegend(new UniqueSymbolPoint());
                }
                setGeometryType(lc.getGeometryType());
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return (geometryType & SimpleGeometryType.ALL) != 0;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniquePointSE();
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

        private void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout("wrap 2"));

                JPanel lb = getLineBlock(uniquePoint.getPenStroke(),
                                         BORDER_SETTINGS);
                ComponentUtil.setFieldState(isStrokeEnabled(), lb);
                glob.add(lb);

                glob.add(getPointBlock(uniquePoint,
                                       I18N.tr("Mark settings")));

                glob.add(getAreaBlock(uniquePoint.getFillLegend(),
                        PnlUniqueAreaSE.FILL_SETTINGS));

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
                        addPointOnVertices(point, jp);
                    }
                    // Unit of measure
                    jp.add(new JLabel(I18N.tr("Unit of measure")));
                    jp.add(getPointUomCombo(), "growx");
                }

                // Well-known name
                jp.add(new JLabel(I18N.tr("Symbol")));
                jp.add(getWKNCombo(point), "width 90!");
                // Mark width
                jp.add(new JLabel(I18N.tr("Width")));
                jp.add(getMarkWidth(point), "growx");
                // Mark height
                jp.add(new JLabel(I18N.tr("Height")));
                jp.add(getMarkHeight(point), "growx");

                return jp;
        }

        /**
         * JNumericSpinner embedded in a JPanel to configure the width of the symbol
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
         * JNumericSpinner embedded in a JPane to configure the height of the symbol
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
                ((JLabel)jcc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
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
         * If called, this method will add a {@code ButtonGroup} made of two
         * {@code JRadioButton}s that will be used to choose if the symbols
         * must be drawn on vertices or on the centroid of the input geometry.
         * @param point
         * @param jp
         */
        public void addPointOnVertices(ConstantFormPoint point, JPanel jp){
                CanvasSE prev = getPreview();
                JRadioButton bVertex = new JRadioButton(I18N.tr("On vertex"));
                JRadioButton bCentroid = new JRadioButton(I18N.tr("On centroid"));
                ButtonGroup bg = new ButtonGroup();
                bg.add(bVertex);
                bg.add(bCentroid);
                ActionListener actionV = EventHandler.create(ActionListener.class, point, "setOnVertex");
                ActionListener actionC = EventHandler.create(ActionListener.class, point, "setOnCentroid");
                ActionListener actionRef = EventHandler.create(ActionListener.class, prev, "imageChanged");
                bVertex.addActionListener(actionV);
                bVertex.addActionListener(actionRef);
                bCentroid.addActionListener(actionC);
                bCentroid.addActionListener(actionRef);
                bVertex.setSelected(((PointSymbolizer)point.getSymbolizer()).isOnVertex());
                bCentroid.setSelected(!((PointSymbolizer)point.getSymbolizer()).isOnVertex());
                jp.add(bVertex, "split 2, span 2");
                jp.add(bCentroid, "gapleft push");
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
        protected JComboBox getPointUomCombo(){
                CanvasSE prev = getPreview();
                uoms = getUomProperties();
                String[] values = new String[uoms.length];
                for (int i = 0; i < values.length; i++) {
                        values[i] = I18N.tr(uoms[i].toString());
                }
                final JComboBox jcc = new JComboBox(values);
                ((JLabel)jcc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
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
