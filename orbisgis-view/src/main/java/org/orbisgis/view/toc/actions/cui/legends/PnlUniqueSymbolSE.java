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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.structure.fill.constant.ConstantSolidFill;
import org.orbisgis.legend.structure.stroke.constant.ConstantPenStroke;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.sif.components.JNumericSpinner;
import org.orbisgis.view.toc.actions.cui.components.CanvasSE;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This class proposes some methods that will be common to all the panels built
 * for unique symbols.
 * @author Alexis Guéganno
 */
public abstract class PnlUniqueSymbolSE extends  JPanel implements ILegendPanel, UIPanel {

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
                        preview.repaint();
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
                c1.setAlignmentX(Component.LEFT_ALIGNMENT);
                return c1;
        }

        /**
         * Retrieve a spinner with the wanted listener.
         * @param min
         *      The minimum value authorized in this spinner.
         * @param max
         *      The maximum value authorized in this spinner.
         * @param inc
         *      The value that will be added (or substracted) when using a
         *      button of the spinner.
         * @return
         *      The wanted {@code JNumericSpinner}.
         */
        public JNumericSpinner getLineWidthSpinner(final ConstantPenStroke cps){
                final JNumericSpinner jns = new JNumericSpinner(4, 0, Integer.MAX_VALUE, 0.01);
                ChangeListener cl = EventHandler.create(ChangeListener.class, cps, "lineWidth", "source.value");
                jns.addChangeListener(cl);
                jns.setValue(cps.getLineWidth());
                jns.setMaximumSize(new Dimension(60,30));
                jns.setPreferredSize(new Dimension(60,30));
                ChangeListener cl2 = EventHandler.create(ChangeListener.class, preview, "repaint");
                jns.addChangeListener(cl2);
                return jns;
        }

        /**
         * Gets a spinner that is linked with the opacity of the {@code
         * ConstantSolidFill} given in argument.
         * @param cps
         * @return
         */
        public JNumericSpinner getLineOpacitySpinner(final ConstantSolidFill cps){
                final JNumericSpinner jns = new JNumericSpinner(4, 0, 1, 0.01);
                ChangeListener cl = EventHandler.create(ChangeListener.class, cps, "opacity", "source.value");
                jns.addChangeListener(cl);
                jns.setValue(cps.getOpacity());
                jns.setMaximumSize(new Dimension(60,30));
                jns.setPreferredSize(new Dimension(60,30));
                ChangeListener cl2 = EventHandler.create(ChangeListener.class, preview, "repaint");
                jns.addChangeListener(cl2);
                return jns;
        }

        /**
         * Get a {@code TextField} instance linked to the given parameter.
         * @param s
         *      The parameter we want to configure with our panel
         * @return
         *      A {@code JTextField} embedded in a {@code JPanel}.
         */
        public JPanel getDashArrayField(final ConstantPenStroke cps){
                JPanel cont = new JPanel();
                final JTextField jrf = new JTextField(8);
                ActionListener al = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                cps.setDashArray(((JTextField)e.getSource()).getText());
                        }
                };
                jrf.addActionListener(al);
                FocusListener fl = new FocusListener() {
                        @Override public void focusGained(FocusEvent e) {}
                        @Override public void focusLost(FocusEvent e) {
                                JTextField jtf = (JTextField)e.getSource();
                                String tmp = jtf.getText();
                                cps.setDashArray(tmp);
                                if(!tmp.equals(cps.getDashArray())){
                                        LOGGER.warn(I18N.tr("Could not validate your input."));
                                        jtf.setText(cps.getDashArray());
                                }
                        }
                };
                jrf.addFocusListener(fl);
                FocusListener prev = EventHandler.create(FocusListener.class, preview, "repaint");
                jrf.addFocusListener(prev);
                jrf.setText(cps.getDashArray());
                cont.add(jrf);
                return cont;
        }

        /**
         * Get a {@code JPanel} that contains a {@code JLabel}. If the {@code
         * JLabel} is clicked, a dialog is open to let the user choose a color.
         * This {@code JLabel} is linked to the given {@code USParameter}.
         * @param c
         * @return
         */
        public JPanel getColorField(final ConstantSolidFill c){
                JLabel lblFill = new JLabel();
                MouseListener ma = EventHandler.create(MouseListener.class,this,"chooseFillColor","","mouseClicked");
                lblFill.addMouseListener(ma);
                PropertyChangeListener pcl = EventHandler.create(PropertyChangeListener.class,c,"color","newValue");
                PropertyChangeListener pcl2 = EventHandler.create(PropertyChangeListener.class, preview, "repaint");
                lblFill.addPropertyChangeListener("background", pcl);
                lblFill.addPropertyChangeListener("background", pcl2);
                lblFill.setBackground(c.getColor());
                lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
                lblFill.setPreferredSize(new Dimension(40, 20));
                lblFill.setMaximumSize(new Dimension(40, 20));
                lblFill.setOpaque(true);
                JPanel jp = new JPanel();
                jp.add(lblFill);
                return jp;
        }

        /**
         * This method will let the user choose a color that will be set as the
         * background of the source of the event.
         * @param e
         */
	public void chooseFillColor(MouseEvent e) {
                Component source = (Component)e.getSource();
                if(source.isEnabled()){
                        ColorPicker picker = new ColorPicker();
                        if (UIFactory.showDialog(picker)) {
                                Color color = picker.getColor();
                                source.setBackground(color);
                        }
                }
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
