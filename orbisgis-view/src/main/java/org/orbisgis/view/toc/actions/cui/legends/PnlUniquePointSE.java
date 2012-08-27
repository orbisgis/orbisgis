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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeListener;
import org.orbisgis.core.renderer.se.PointSymbolizer;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.graphic.WellKnownName;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.analyzer.FillAnalyzer;
import org.orbisgis.legend.analyzer.PenStrokeAnalyzer;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.ConstantFormPoint;
import org.orbisgis.legend.thematic.constant.UniqueSymbolPoint;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.JNumericSpinner;
import org.orbisgis.view.components.ContainerItemProperties;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
                                PenStrokeAnalyzer psa = new PenStrokeAnalyzer(new PenStroke());
                                setPenStrokeMemory((ConstantPenStrokeLegend) psa.getLegend());
                        }
                        ConstantSolidFill csf = uniquePoint.getFillLegend();
                        if(csf instanceof ConstantSolidFillLegend){
                                setSolidFillMemory((ConstantSolidFillLegend) csf);
                        } else {
                                FillAnalyzer fa = new FillAnalyzer(new SolidFill());
                                setSolidFillMemory((ConstantSolidFillLegend) fa.getLegend());
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
                return "Unique symbol for points.";
        }
        

        @Override
        public Legend copyLegend() {
                return new UniqueSymbolPoint();
        }

        private void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel();
                GridBagLayout grid = new GridBagLayout();
                glob.setLayout(grid);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                JPanel p1 = getLineBlock(uniquePoint.getPenStroke(), I18N.tr("Line configuration"));
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 0, 5, 0);
                JPanel p2 = getAreaBlock(uniquePoint.getFillLegend(), I18N.tr("Fill configuration"));
                glob.add(p2, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 0, 5, 0);
                JPanel p3 = getPointBlock(uniquePoint, I18N.tr("Mark configuration"));
                glob.add(p3, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 3;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }

        /**
         * Builds the UI block used to configure the fill color of the
         * symbolizer.
         * @param fillLegend
         * @param title
         * @return
         */
        public JPanel getPointBlock(UniqueSymbolPoint point, String title) {
                if(getPreview() == null && getLegend() != null){
                        initPreview();
                }
                JPanel glob = new JPanel();
                glob.setLayout(new BoxLayout(glob, BoxLayout.Y_AXIS));
                JPanel jp = new JPanel();
                boolean canBeOnV = geometryType != SimpleGeometryType.POINT;
                int onV = canBeOnV ? 1 : 0;
                GridLayout grid = new GridLayout(5+onV,2);
                grid.setVgap(5);
                jp.setLayout(grid);
                //If geometryType != POINT, we must let the user choose if he
                //wants to draw symbols on centroid or on vertices.
                if(geometryType != SimpleGeometryType.POINT){
                        addPointOnVertices(point, jp);
                }
                //Uom
                jp.add(buildText(I18N.tr("Unit of measure :")));
                jp.add(getPointUomCombo());
                //Combobox
                jp.add(buildText(I18N.tr("Symbol form :")));
                jp.add(getWKNCombo(point));
                //Mark width
                jp.add(buildText(I18N.tr("Mark width :")));
                jp.add(getMarkWidth(point));
                //Mark height
                jp.add(buildText(I18N.tr("Mark height :")));
                jp.add(getMarkHeight(point));
                glob.add(jp);
                //We add a canvas to display a preview.
                glob.setBorder(BorderFactory.createTitledBorder(title));
                return glob;
        }

        /**
         * JNumericSpinner embedded in a JPanel to configure the width of the symbol
         * @param point
         * @return
         */

        private JPanel getMarkWidth(UniqueSymbolPoint point){
                CanvasSE prev = getPreview();
                final JNumericSpinner jns = new JNumericSpinner(4, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01);
                ChangeListener cl = EventHandler.create(ChangeListener.class, point, "viewBoxWidth", "source.value");
                jns.addChangeListener(cl);
                jns.setValue(point.getViewBoxWidth() == null? point.getViewBoxHeight() : point.getViewBoxWidth());
                jns.setMaximumSize(new Dimension(60,30));
                jns.setPreferredSize(new Dimension(60,30));
                ChangeListener cl2 = EventHandler.create(ChangeListener.class, prev, "repaint");
                jns.addChangeListener(cl2);
                return jns;
        }

        /**
         * JNumericSpinner embedded in a JPane to configure the height of the symbol
         * @param point
         * @return
         */
        private JPanel getMarkHeight(UniqueSymbolPoint point){
                CanvasSE prev = getPreview();
                final JNumericSpinner jns = new JNumericSpinner(4, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.01);
                ChangeListener cl = EventHandler.create(ChangeListener.class, point, "viewBoxHeight", "source.value");
                jns.addChangeListener(cl);
                jns.setValue(point.getViewBoxHeight() == null? point.getViewBoxWidth() : point.getViewBoxHeight());
                jns.setMaximumSize(new Dimension(60,30));
                jns.setPreferredSize(new Dimension(60,30));
                ChangeListener cl2 = EventHandler.create(ChangeListener.class, prev, "repaint");
                jns.addChangeListener(cl2);
                return jns;
        }

        /**
         * ComboBox to configure the WKN
         * @param point
         * @return
         */
        public JComboBox getWKNCombo(ConstantFormPoint point){
                CanvasSE prev = getPreview();
                wkns = getWknProperties();
                String[] values = new String[wkns.length];
                for (int i = 0; i < values.length; i++) {
                        values[i] = wkns[i].getLabel();
                }
                final JComboBox jcc = new JComboBox(values);
                ActionListener acl = EventHandler.create(ActionListener.class, prev, "repaint");
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
                ActionListener actionRef = EventHandler.create(ActionListener.class, prev, "repaint");
                bVertex.addActionListener(actionV);
                bVertex.addActionListener(actionRef);
                bCentroid.addActionListener(actionC);
                bCentroid.addActionListener(actionRef);
                bVertex.setSelected(((PointSymbolizer)point.getSymbolizer()).isOnVertex());
                bCentroid.setSelected(!((PointSymbolizer)point.getSymbolizer()).isOnVertex());
                jp.add(bVertex);
                jp.add(bCentroid);
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
         * @param pt
         * @return
         */
        protected JComboBox getPointUomCombo(){
                CanvasSE prev = getPreview();
                uoms= getUomProperties();
                String[] values = new String[uoms.length];
                for (int i = 0; i < values.length; i++) {
                        values[i] = I18N.tr(uoms[i].toString());
                }
                final JComboBox jcc = new JComboBox(values);
                ActionListener acl = EventHandler.create(ActionListener.class, prev, "repaint");
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
