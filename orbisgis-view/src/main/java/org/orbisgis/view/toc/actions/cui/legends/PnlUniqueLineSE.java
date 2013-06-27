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

import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.structure.stroke.constant.NullPenStrokeLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolLine;
import org.orbisgis.legend.thematic.constant.UniqueSymbolLine;
import org.orbisgis.legend.thematic.uom.StrokeUom;
import org.orbisgis.sif.ComponentUtil;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.UomCombo;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;

/**
 * {@code JPanel} that ca nbe used to configure simple constant {@code
 * LineSymbolizer} instances that have been recognized as unique symbols made
 * justof one simple {@code PenStroke}.
 * @author Alexis Guéganno
 */
public class PnlUniqueLineSE extends PnlUniqueSymbolSE {
    private static final I18n I18N = I18nFactory.getI18n(PnlUniqueLineSE.class);
        private ConstantPenStrokeLegend penStrokeMemory;
        private JCheckBox lineCheckBox;
        private JSpinner lineWidth;
        private JPanel lineColor;
        private JSpinner lineOpacity;
        private JComboBox uOMBox;
        private JFormattedTextField lineDash;
        private ContainerItemProperties[] uoms;
        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolLine uniqueLine;
        private final boolean displayUom;

        /**
         * Default constructor. The UOM combo box is displayed.
         */
        public PnlUniqueLineSE(){
            this(true);
        }

        /**
         * Builds a new PnlUniqueLineSE choosing if we want to display the uom combo box.
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
        public Component getComponent() {
                return this;
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
                if (uniqueLine == null) {
                        setLegend(new UniqueSymbolLine());
                }
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.LINE ||
                        geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniqueLineSE();
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
                return "Unique symbol for lines.";
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
         * This method will be used during the panel construction to determine
         * if the block of parameters can be disabled or not. In the case of
         * pure lines (ie for this class), this method will return {@code true}.
         * @return
         */
        protected boolean isLineOptional(){
                return false;
        }

        /**
         * Gets a panel containing all the fields to edit a unique line.
         * @param leg
         * @param title
         * @return
         */
        public JPanel getLineBlock(ConstantPenStroke leg, String title){
                if(getPreview() == null && getLegend() != null){
                        initPreview();
                }
                ConstantPenStroke legend = leg instanceof ConstantPenStrokeLegend ? leg : penStrokeMemory;

                JPanel jp = new JPanel();
                GridLayout grid = new GridLayout(
                        0, 2, HGAP, VGAP);
                jp.setLayout(grid);
                jp.setBorder(BorderFactory.createTitledBorder(title));

                UomCombo lineUom = getLineUomCombo((StrokeUom) getLegend());
                CanvasSE prev = getPreview();
                ActionListener aclUom = EventHandler.create(ActionListener.class, prev, "imageChanged");
                lineUom.addActionListener(aclUom);

                if(isLineOptional()){
                        lineCheckBox = new JCheckBox(I18N.tr("Enable"));
                        lineCheckBox.addActionListener(
                                EventHandler.create(ActionListener.class, this, "onClickLineCheckBox"));
                        jp.add(lineCheckBox);
                        // We must check the CheckBox according to leg, not to legend.
                        // legend is here mainly to let us fill safely all our
                        // parameters.
                        lineCheckBox.setSelected(leg instanceof ConstantPenStrokeLegend);
                } else {
                        // Just add blank space
                        jp.add(Box.createGlue());
                }
                // Line color
                lineColor = getColorField(legend.getFillLegend());
                jp.add(lineColor);

                // Unit of measure
                if(displayUom){
                    jp.add(buildText(I18N.tr("Unit of measure")));
                    uOMBox = lineUom.getCombo();
                    jp.add(uOMBox);
                }
                // Line width
                jp.add(buildText(I18N.tr("Width")));
                lineWidth = getLineWidthSpinner(legend);
                jp.add(lineWidth);
                // Line opacity
                jp.add(buildText(I18N.tr("Opacity")));
                lineOpacity = getLineOpacitySpinner(legend.getFillLegend());
                jp.add(lineOpacity);
                // Dash array
                jp.add(buildText(I18N.tr("Dash array")));
                lineDash = getDashArrayField((ConstantColorAndDashesPSLegend)legend);
                jp.add(lineDash);
                if(isLineOptional()){
                    setLineFieldsState(leg instanceof ConstantPenStrokeLegend);
                }
                return jp;
        }

        /**
         * Change the state of all the fields used for the line configuration.
         * @param enable
         */
        public void setLineFieldsState(boolean enable){
            ComponentUtil.setFieldState(enable,lineWidth);
            ComponentUtil.setFieldState(enable, lineColor);
            ComponentUtil.setFieldState(enable,lineOpacity);
            ComponentUtil.setFieldState(enable,lineDash);
            if (displayUom) {
                if (uOMBox != null) {
                    ComponentUtil.setFieldState(enable,uOMBox);
                }
            }
        }

        /**
         * If {@code isLineOptional()}, a {@code JCheckBox} will be added in the
         * UI to let the user enable or disable the line configuration. In fact,
         * clicking on it will recursively enable or disable the containers
         * contained in the configuration panel.
         */
        public void onClickLineCheckBox(){
                if(lineCheckBox.isSelected()){
                        ((IUniqueSymbolLine)getLegend()).setPenStroke(penStrokeMemory);
                        setLineFieldsState(true);
                        getPreview().imageChanged();
                } else {
                        //We must replace the old PenStroke representation with
                        //its null representation
                        NullPenStrokeLegend npsl = new NullPenStrokeLegend();
                        ((IUniqueSymbolLine)getLegend()).setPenStroke(npsl);
                        setLineFieldsState(false);
                        getPreview().imageChanged();
                }
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

        private void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel();
                GridBagLayout grid = new GridBagLayout();
                glob.setLayout(grid);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                JPanel p1 = getLineBlock(uniqueLine.getPenStroke(), "Line configuration");
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }
}
