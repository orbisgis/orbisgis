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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
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
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.SimpleGeometryType;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Alexis Guéganno
 */
public class PnlProportionalLine extends PnlUniqueSymbolSE {

        private ProportionalLine legend;
        private static final I18n I18N = I18nFactory.getI18n(PnlProportionalLine.class);
        private static final Logger LOGGER = Logger.getLogger("gui."+PnlProportionalLine.class);
        private DataSource ds;
        private JComboBox lineUom;
        private JPanel lineColor;
        private JPanel lineOpacity;
        private JPanel lineDash;
//        private JComboBox fieldCombo;

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
                        geometryType == SimpleGeometryType.POLYGON;
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
        public ISELegendPanel newInstance() {
                return new PnlProportionalLine();
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

        private void initializeLegendFields() {
                this.removeAll();
                JPanel glob = new JPanel();
                GridBagLayout grid = new GridBagLayout();
                glob.setLayout(grid);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                JPanel p1 = getLineBlock((ProportionalStrokeLegend)legend.getStrokeLegend(), "Line configuration");
                glob.add(p1, gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                glob.add(getPreview(), gbc);
                this.add(glob);
        }

        /**
         * Gets a panel containing all the fields to edit a unique line.
         * @param legend
         * @param title
         * @return
         */
        public JPanel getLineBlock(ProportionalStrokeLegend leg, String title){
                if(getPreview() == null && getLegend() != null){
                        initPreview();
                }
                ProportionalStrokeLegend strokeLeg = (ProportionalStrokeLegend) legend.getStrokeLegend();
                JPanel glob = new JPanel();
                glob.setLayout(new BoxLayout(glob, BoxLayout.Y_AXIS));
                JPanel jp = new JPanel();
                GridLayout grid = new GridLayout(7,2);
                grid.setVgap(5);
                jp.setLayout(grid);
                lineUom = getLineUomCombo(legend);
                lineColor = getColorField((ConstantSolidFill)strokeLeg.getFillAnalysis());
                lineOpacity = getLineOpacitySpinner((ConstantSolidFill)strokeLeg.getFillAnalysis());
                lineDash = getDashArrayField(strokeLeg);
                //Field
                jp.add(buildText(I18N.tr("Field Name :")));
                jp.add(getFieldComboBox());
                //Uom
                jp.add(buildText(I18N.tr("Unit of measure :")));
                jp.add(lineUom);
                //Width
                jp.add(buildText(I18N.tr("Max width :")));
                jp.add(getSecondConf(legend));
                jp.add(buildText(I18N.tr("Min width :")));
                jp.add(getFirstConf(legend));
                //Color
                jp.add(buildText(I18N.tr("Line color :")));
                jp.add(lineColor);
                //Transparency
                jp.add(buildText(I18N.tr("Line opacity :")));
                jp.add(lineOpacity);
                //Dash array
                jp.add(buildText(I18N.tr("Dash array :")));
                jp.add(lineDash);
                glob.add(jp);
                //We add a canvas to display a preview.
                glob.setBorder(BorderFactory.createTitledBorder(title));
                return glob;
        }

        private JPanel getSecondConf(ProportionalLine prop){
                CanvasSE prev = getPreview();
                JPanel ret = new JPanel();
                JFormattedTextField jftf = new JFormattedTextField(new DecimalFormat());
                jftf.setColumns(8);
                try {
                        jftf.setValue(prop.getSecondValue());
                } catch (ParameterException ex) {
                        LOGGER.error(I18N.tr("Can't retrieve the maximum value of"
                                + " the symbol"), ex);
                }
                PropertyChangeListener al = EventHandler.create(PropertyChangeListener.class, prop, "secondValue", "source.value");
                jftf.addPropertyChangeListener("value", al);
                PropertyChangeListener al2 = EventHandler.create(PropertyChangeListener.class, prev, "repaint");
                jftf.addPropertyChangeListener("value", al2);
                ret.add(jftf);
                return ret;
        }

        private JPanel getFirstConf(ProportionalLine prop){
                JPanel ret = new JPanel();
                JFormattedTextField jftf = new JFormattedTextField(new DecimalFormat());
                jftf.setColumns(8);
                try {
                        jftf.setValue(prop.getFirstValue());
                } catch (ParameterException ex) {
                        LOGGER.error(I18N.tr("Can't retrieve the minimum value of"
                                + " the symbol"), ex);
                }
                PropertyChangeListener al = EventHandler.create(PropertyChangeListener.class, prop, "firstValue", "source.value");
                jftf.addPropertyChangeListener("value", al);
                ret.add(jftf);
                return ret;
        }

        private JComboBox getFieldComboBox(){
                if(ds != null){
                        JComboBox jcc = getNumericFieldCombo(ds);
                        ActionListener acl2 = EventHandler.create(ActionListener.class,
                                this, "updateField", "source.selectedItem");
                        String field = legend.getLookupFieldName();
                        if(field != null && !field.isEmpty()){
                                jcc.setSelectedItem(field);
                        }
                        jcc.addActionListener(acl2);
                        updateField((String)jcc.getSelectedItem());
                        return jcc;
                } else {
                        return new JComboBox();
                }
        }

        /**
         * Used when the field against which the analysis is made changes.
         * @param obj
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
                        getPreview().repaint();
                } catch (DriverException ex) {
                        LOGGER.error("", ex);
                } catch (ParameterException ex) {
                        LOGGER.error("", ex);
                }
        }
}
