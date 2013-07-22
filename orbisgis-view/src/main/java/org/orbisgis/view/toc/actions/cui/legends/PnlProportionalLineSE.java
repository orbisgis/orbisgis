/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.ClassificationUtils;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.ProportionalStrokeLegend;
import org.orbisgis.legend.thematic.proportional.ProportionalLine;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.WideComboBox;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.panels.UomCombo;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * "Proportional Line" UI.
 *
 * @author Alexis Guéganno
 */
public class PnlProportionalLineSE extends PnlUniqueLineSE {

        private ProportionalLine legend;
        private static final I18n I18N = I18nFactory.getI18n(PnlProportionalLineSE.class);
        private static final Logger LOGGER = Logger.getLogger("gui."+PnlProportionalLineSE.class);
        private DataSource ds;
        private JPanel lineColor;
        private JSpinner lineOpacity;
        private JTextField lineDash;

        @Override
        public Legend getLegend() {
                return legend;
        }

        @Override
        public void setLegend(Legend legend) {
                if(legend instanceof ProportionalLine){
                        this.legend = (ProportionalLine)legend;
                        initPreview();
                        initializeLegendFields();
                } else {
                        throw new IllegalArgumentException(I18N.tr("The given legend is"
                                + "not an instance of proportional line."));
                }
        }

        @Override
        public void setGeometryType(int type) {
        }

        @Override
        public boolean acceptsGeometryType(int geometryType) {
                return geometryType == SimpleGeometryType.LINE ||
                        geometryType == SimpleGeometryType.POLYGON||
                        geometryType == SimpleGeometryType.ALL;
        }

        @Override
        public Legend copyLegend() {
                ProportionalLine usl = new ProportionalLine();
                ProportionalStrokeLegend strokeLeg = (ProportionalStrokeLegend) legend.getStrokeLegend();
                ProportionalStrokeLegend newLeg = (ProportionalStrokeLegend) usl.getStrokeLegend();
                newLeg.setDashArray(strokeLeg.getDashArray());
                try{
                        newLeg.setFirstValue(strokeLeg.getFirstValue());
                        newLeg.setFirstData(strokeLeg.getFirstData());
                        newLeg.setSecondValue(strokeLeg.getSecondValue());
                        newLeg.setSecondData(strokeLeg.getSecondData());
                        newLeg.setLineColor(strokeLeg.getLineColor());
                } catch(ParameterException pe){
                        LOGGER.error(I18N.tr("Could not copy the ProportionalLine."), pe);
                }
                return usl;
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public void initialize(LegendContext lc) {
                if (legend == null) {
                        setLegend(new ProportionalLine());
                }
                setGeometryType(lc.getGeometryType());
                ILayer layer = lc.getLayer();
                if(layer != null){
                        ds = layer.getDataSource();
                }
                initializeLegendFields();
        }

        @Override
        public ILegendPanel newInstance() {
                return new PnlProportionalLineSE();
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
                return "Proportional Line";
        }

        @Override
        protected void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel(new MigLayout());
                glob.add(getLineBlock());
                glob.add(getPreviewPanel());
                this.add(glob);
        }

        /**
         * Gets a panel containing all the fields to edit a unique line.
         * @return
         */
        public JPanel getLineBlock(){
                if(getPreview() == null && getLegend() != null){
                        initPreview();
                }

                ProportionalStrokeLegend strokeLeg = (ProportionalStrokeLegend) legend.getStrokeLegend();
                ConstantSolidFill csf = (ConstantSolidFill)strokeLeg.getFillAnalysis();

                JPanel jp = new JPanel(new MigLayout("wrap 2", COLUMN_CONSTRAINTS));

                // Field
                jp.add(new JLabel(I18N.tr(FIELD)));
                jp.add(getFieldComboBox(), COMBO_BOX_CONSTRAINTS);
                // Color
                jp.add(new JLabel(I18N.tr("Color")));
                lineColor = getColorField(csf);
                jp.add(lineColor);
                // Unit of Measure - line width
                jp.add(new JLabel(I18N.tr(LINE_WIDTH_UNIT)));
                UomCombo lineUom = getLineUomCombo(legend);
                lineUom.addActionListener(
                        EventHandler.create(ActionListener.class, getPreview(), "imageChanged"));
                jp.add(lineUom.getCombo(), COMBO_BOX_CONSTRAINTS);
                // Max width
                jp.add(new JLabel(I18N.tr("Max width")));
                jp.add(getSecondConf(legend), "growx");
                // Min width
                jp.add(new JLabel(I18N.tr("Min width")));
                jp.add(getFirstConf(legend), "growx");
                // Opacity
                jp.add(new JLabel(I18N.tr(OPACITY)));
                lineOpacity = getLineOpacitySpinner(csf);
                jp.add(lineOpacity, "growx");
                // Dash array
                jp.add(new JLabel(I18N.tr(DASH_ARRAY)));
                lineDash = getDashArrayField(strokeLeg);
                jp.add(lineDash, "growx");
                jp.setBorder(BorderFactory.createTitledBorder(
                        I18N.tr(LINE_SETTINGS)));
                return jp;
        }

        private JFormattedTextField getSecondConf(ProportionalLine prop){
                CanvasSE prev = getPreview();
                JFormattedTextField jftf = new JFormattedTextField(new DecimalFormat());
                try {
                        jftf.setValue(prop.getSecondValue());
                } catch (ParameterException ex) {
                        LOGGER.error(I18N.tr("Can't retrieve the maximum value of"
                                + " the symbol"), ex);
                }
                jftf.addPropertyChangeListener(
                        "value",
                        EventHandler.create(PropertyChangeListener.class, prop, "secondValue", "source.value"));
                jftf.addPropertyChangeListener(
                        "value",
                        EventHandler.create(PropertyChangeListener.class, prev, "imageChanged"));
                jftf.setHorizontalAlignment(SwingConstants.RIGHT);
                return jftf;
        }

        private JFormattedTextField getFirstConf(ProportionalLine prop){
                JFormattedTextField jftf = new JFormattedTextField(new DecimalFormat());
                try {
                        jftf.setValue(prop.getFirstValue());
                } catch (ParameterException ex) {
                        LOGGER.error(I18N.tr("Can't retrieve the minimum value of"
                                + " the symbol"), ex);
                }
                jftf.addPropertyChangeListener(
                        "value",
                        EventHandler.create(PropertyChangeListener.class, prop, "firstValue", "source.value"));
                jftf.setHorizontalAlignment(SwingConstants.RIGHT);
                return jftf;
        }

        /**
         * Creates and fill the combo box that will be used to compute the
         * analysis.
         * @return The combo box we can use to choose the text.
         */
        private WideComboBox getFieldComboBox(){
                if(ds != null){
                        WideComboBox jcc = getNumericFieldCombo(ds);
                        ActionListener acl2 = EventHandler.create(ActionListener.class,
                                this, "updateField", "source.selectedItem");
                        String field = legend.getLookupFieldName();
                        if(field != null && !field.isEmpty()){
                                jcc.setSelectedItem(field);
                        }
                        jcc.addActionListener(acl2);
                        updateField((String)jcc.getSelectedItem());
                        ((JLabel)jcc.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
                        return jcc;
                } else {
                        return new WideComboBox();
                }
        }

        /**
         * Used when the field against which the analysis is made changes.
         * @param obj The new field name.
         */
        public void updateField(String obj){
                try {
                        double[] mnm=ClassificationUtils.getMinAndMax(ds, new RealAttribute(obj));
                        legend.setFirstData(mnm[0]);
                        legend.setSecondData(mnm[1]);
                        legend.setLookupFieldName(obj);
                        Map<String, Object> sample = new HashMap<String, Object>();
                        sample.put(obj, mnm[1]);
                        getPreview().setSampleDatasource(sample);
                        getPreview().setDisplayed(true);
                        getPreview().imageChanged();
                } catch (DriverException ex) {
                        LOGGER.error("", ex);
                } catch (ParameterException ex) {
                        LOGGER.error("", ex);
                }
        }
}
