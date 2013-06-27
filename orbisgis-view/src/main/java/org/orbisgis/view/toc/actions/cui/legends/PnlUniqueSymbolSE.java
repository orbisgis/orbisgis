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

import org.apache.log4j.Logger;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.ConstantColorAndDashesPSLegend;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.JNumericSpinner;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

/**
 * This class proposes some methods that will be common to all the panels built
 * for unique symbols.
 * @author Alexis Guéganno
 */
public abstract class PnlUniqueSymbolSE extends  AbstractFieldPanel implements ILegendPanel, UIPanel {

        public static final double SPIN_STEP = 0.1;

        private static final Logger LOGGER = Logger.getLogger("gui."+PnlUniqueSymbolSE.class);
        private static final I18n I18N = I18nFactory.getI18n(PnlUniqueSymbolSE.class);
        private String id;
        private CanvasSE preview;

        /**
         * Rebuild the {@code CanvasSe} instance used to display a preview of
         * the current symbol.
         */
        public void initPreview(){
                Legend leg = getLegend();
                if(leg != null){
                        preview= new CanvasSE(leg.getSymbolizer());
                        preview.imageChanged();
                }
        }

        /**
         * Gets the {@code CanvasSe} instance used to display a preview of
         * the current symbol.
         * @return
         */
        public CanvasSE getPreview(){
                return preview;
        }

        /**
         * Build a {@code JLabel} from {@code name} with x-alignment set to
         * {@code Component.LEFT_ALIGNMENT}.
         * @param name
         * @return
         */
        public JLabel buildText(String name){
                JLabel c1 = new JLabel(name);
                c1.setHorizontalAlignment(SwingConstants.RIGHT);
                return c1;
        }

        /**
         * Retrieve a spinner with the wanted listener.
         *
         * @param cps The stroke that will be configured with the spinner.
         * @return
         *      The wanted {@code JNumericSpinner}.
         */
        public JSpinner getLineWidthSpinner(final ConstantPenStroke cps){
                SpinnerNumberModel model = new SpinnerNumberModel(
                        cps.getLineWidth(), 0, Double.POSITIVE_INFINITY, SPIN_STEP);
                final JSpinner jns = new JSpinner(model);
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, cps, "lineWidth", "source.value"));
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, preview, "imageChanged"));
                return jns;
        }

        /**
         * Gets a spinner that is linked with the opacity of the {@code
         * ConstantSolidFill} given in argument.
         *
         * @param cps
         * @return
         */
        public JSpinner getLineOpacitySpinner(final ConstantSolidFill cps){
                SpinnerNumberModel model = new SpinnerNumberModel(
                        cps.getOpacity(), 0, 1, SPIN_STEP);
                final JSpinner jns = new JSpinner(model);
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, cps, "opacity", "source.value"));
                jns.addChangeListener(
                        EventHandler.create(ChangeListener.class, preview, "imageChanged"));
                return jns;
        }

        /**
         * Get a {@code TextField} instance linked to the given parameter.
         *
         * @param cps
         *      The parameter we want to configure with our panel
         * @return A {@code JTextField}
         */
        public JTextField getDashArrayField(final ConstantColorAndDashesPSLegend cps){
                final JTextField jrf = new JTextField("", 8);
                jrf.setText(cps.getDashArray());
                jrf.setHorizontalAlignment(JFormattedTextField.RIGHT);
                jrf.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cps.setDashArray(((JTextField)e.getSource()).getText());
                    }
                });
                jrf.addFocusListener(new FocusAdapter() {
                    @Override public void focusLost(FocusEvent e) {
                        JTextField jtf = (JTextField)e.getSource();
                        String tmp = jtf.getText();
                        cps.setDashArray(tmp);
                        if(!tmp.equals(cps.getDashArray())){
                            LOGGER.warn(I18N.tr("Could not validate your input."));
                            jtf.setText(cps.getDashArray());
                        }
                    }
                });
                jrf.addFocusListener(
                        EventHandler.create(FocusListener.class, preview, "imageChanged"));
                return jrf;
        }

        /**
         * Get a {@code JPanel} that contains a {@code JLabel}. If the {@code
         * JLabel} is clicked, a dialog is open to let the user choose a color.
         * This {@code JLabel} is linked to the given {@code USParameter}.
         * @param c
         * @return
         */
        public JPanel getColorField(final ConstantSolidFill c){
                JLabel lblFill = getFilledLabel(c.getColor());
                PropertyChangeListener pcl = EventHandler.create(PropertyChangeListener.class,c,"color","newValue");
                PropertyChangeListener pcl2 = EventHandler.create(PropertyChangeListener.class, preview, "imageChanged");
                lblFill.addPropertyChangeListener("background", pcl);
                lblFill.addPropertyChangeListener("background", pcl2);
                JPanel jp = new JPanel();
                jp.add(lblFill);
                return jp;
        }

        @Override
        public String getId(){
                return id;
        }

        @Override
        public void setId(String id){
                this.id = id;
        }
}
