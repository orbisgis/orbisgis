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

import org.orbisgis.core.renderer.se.fill.SolidFill;
import org.orbisgis.core.renderer.se.stroke.PenStroke;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFillLegend;
import org.orbisgis.legend.structure.fill.constant.NullSolidFillLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStrokeLegend;
import org.orbisgis.legend.thematic.constant.IUniqueSymbolArea;
import org.orbisgis.legend.thematic.constant.UniqueSymbolArea;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URL;

/**
 *
 * @author Alexis Guéganno
 */
public class PnlUniqueAreaSE extends PnlUniqueLineSE {
        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueAreaSE.class);
        private JPanel fill;
        private JPanel fillOpacity;
        private JCheckBox areaCheckBox;
        private ConstantSolidFillLegend solidFillMemory;

        /**
         * Here we can put all the Legend instances we want... but they have to
         * be unique symbol (ie constant) Legends.
         */
        private UniqueSymbolArea uniqueArea;
        private  boolean displayStroke;
        private boolean displayBoxes;

        /**
         * Default constructor. UOM will be displayed as well as the stroke configuration and the check boxes used to
         * enable or disable stroke and fill configuration panels.
         */
        public PnlUniqueAreaSE(){
            this(true, true, true);
        }

        /**
         * Constructor that is used to set if the UOM must be displayed or not.
         * @param uom If true, the uom will be displayed.
         */
        public PnlUniqueAreaSE(boolean uom){
            this(uom, true, true);
        }

        /**
         * Builds the panel.
         * @param uom If true, the combo used to configure the symbolizer UOM will be displayed.
         * @param displayStroke If true, the panel used to configure the symbol's stroke will be enabled.
         * @param displayBoxes If true,  the two boxes that are used to enable and disable the stroke and fill of
         *                     the symbol will be displayed.
         */
        public PnlUniqueAreaSE(boolean uom, boolean displayStroke, boolean displayBoxes){
            super(uom);
            this.displayStroke = displayStroke;
            this.displayBoxes = displayBoxes;
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public Legend getLegend() {
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
                if (uniqueArea == null) {
                        setLegend(new UniqueSymbolArea());
                }
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlUniqueAreaSE();
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
                UniqueSymbolArea ret = new UniqueSymbolArea();
                ret.getFillLegend().setColor(uniqueArea.getFillLegend().getColor());
                return ret;
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
                JPanel p1 = getLineBlock(uniqueArea.getPenStroke(), I18N.tr("Line configuration"));
                setFieldState(displayStroke, p1);
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 0, 5, 0);
                ConstantSolidFill leg = uniqueArea.getFillLegend();
                JPanel p2 = getAreaBlock(leg, I18N.tr("Fill configuration"));
                setAreaFieldsState(leg instanceof ConstantSolidFillLegend);
                glob.add(p2, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }

        /**
         * Builds the UI block used to configure the fill color of the
         * symbolizer.
         * @param fillLegend  The fill we want to configure.
         * @param title The title of the panel
         * @return The JPanel that can be used to configure the way the area will be filled.
         */
        public JPanel getAreaBlock(ConstantSolidFill fillLegend, String title) {
                if(getPreview() == null && getLegend() != null){
                        initPreview();
                }
                ConstantSolidFill fl = fillLegend instanceof ConstantSolidFillLegend ? fillLegend : solidFillMemory;
                JPanel glob = new JPanel();
                glob.setLayout(new BoxLayout(glob, BoxLayout.Y_AXIS));
                JPanel jp = new JPanel();
                int db = displayBoxes ? 1 : 0;
                GridLayout grid = new GridLayout(2+db,3);
                grid.setVgap(5);
                jp.setLayout(grid);
                if(displayBoxes){
                    //The JCheckBox that can be used to enable/disable the fill conf.
                    jp.add(buildText(I18N.tr("Enable fill : ")));
                    areaCheckBox = new JCheckBox("");
                    ActionListener acl = EventHandler.create(ActionListener.class, this, "onClickAreaCheckBox");
                    areaCheckBox.addActionListener(acl);
                    jp.add(areaCheckBox);
                    //We must check the CheckBox according to leg, not to legend.
                    //legend is here mainly to let us fill safely all our
                    //parameters.
                    areaCheckBox.setSelected(fillLegend instanceof ConstantSolidFillLegend);
                }
                //Color
                fill = getColorField(fl);
                jp.add(buildText(I18N.tr("Fill color :")));
                jp.add(fill);
                //Opacity
                fillOpacity = getLineOpacitySpinner(fl);
                jp.add(buildText(I18N.tr("Fill opacity :")));
                jp.add(fillOpacity);
                //We add a canvas to display a preview.
                glob.add(jp);
                glob.setBorder(BorderFactory.createTitledBorder(title));
                return glob;
        }

        /**
         * If {@code isLineOptional()}, a {@code JCheckBox} will be added in the
         * UI to let the user enable or disable the fill configuration. In fact,
         * clicking on it will recursively enable or disable the containers
         * contained in the configuration panel.
         */
        public void onClickAreaCheckBox(){
                if(areaCheckBox.isSelected()){
                        ((IUniqueSymbolArea)getLegend()).setFillLegend(solidFillMemory);
                        setAreaFieldsState(true);
                } else {
                        NullSolidFillLegend nsf = new NullSolidFillLegend();
                        ((IUniqueSymbolArea)getLegend()).setFillLegend(nsf);
                        setAreaFieldsState(false);
                }
                getPreview().imageChanged();
        }

        @Override
        protected boolean isLineOptional(){
                return displayBoxes;
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

        private void setAreaFieldsState(boolean state){
                setFieldState(state, fill);
                setFieldState(state, fillOpacity);
        }
}
