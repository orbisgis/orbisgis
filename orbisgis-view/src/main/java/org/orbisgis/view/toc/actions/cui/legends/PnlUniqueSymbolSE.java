/**
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.orbisgis.legend.thematic.constant.USNumericParameter;
import org.orbisgis.legend.thematic.constant.USParameter;
import org.orbisgis.legend.thematic.constant.UniqueSymbol;
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
         * Fill this {@code JPanel} with all the needed fields.
         */
        public JPanel getLegendBlock(List<USParameter<?>> params, String title){
                if(preview == null && getLegend() != null){
                        initPreview();
                }
                JPanel glob = new JPanel();
                glob.setLayout(new BoxLayout(glob, BoxLayout.Y_AXIS));
                JPanel jp = new JPanel();
                GridLayout grid = new GridLayout(params.size(),2);
                grid.setVgap(5);
                jp.setLayout(grid);
                for(USParameter p : params){
                        JComponent c1 = buildText(p);
                        JComponent c2 = buildField(p);
                        jp.add(c1);
                        jp.add(c2);
                }
                glob.add(jp);
                //We add a canvas to display a preview.
                glob.setBorder(BorderFactory.createTitledBorder(title));
                return glob;
        }

        /**
         * Rebuild the {@code CanvasSe} instance used to display a preview of
         * the current symbol.
         */
        public void initPreview(){
                if(getLegend() != null){
                        UniqueSymbol us = (UniqueSymbol) getLegend();
                        preview= new CanvasSE(us.getSymbolizer());
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

        private JLabel buildText(USParameter param){
                JLabel c1 = new JLabel(param.getName());
                c1.setAlignmentX(Component.LEFT_ALIGNMENT);
                return c1;
        }

        private JComponent buildField(USParameter a){
                JComponent field = null;
                if(a instanceof USNumericParameter){
                        //We prepare a spinner
                        USNumericParameter usn = (USNumericParameter)a;
                        field = getSpinner(
                                ((Double) usn.getMinValue()).intValue(),
                                ((Double) usn.getMaxValue()).intValue(), 
                                1.0,
                                usn);
                } else if(a.getValue() instanceof String){
                        field = getTextField(a);
                } else if(a.getValue() instanceof Color){
                        field = getColorField(a);
                }
                field.setAlignmentX(Component.CENTER_ALIGNMENT);
                return field;

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
        public JNumericSpinner getSpinner(int min, int max, double inc,
                        final USNumericParameter<Double> a){
                final JNumericSpinner jns = new JNumericSpinner(4, min, max, inc);
                jns.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent evt) {
                                a.setValue(jns.getValue());
                    }
                });
                jns.setValue(a.getValue());
                jns.setMaximumSize(new Dimension(60,30));
                jns.setPreferredSize(new Dimension(60,30));
                ChangeListener cl = EventHandler.create(ChangeListener.class, preview, "repaint");
                jns.addChangeListener(cl);
                return jns;
        }

        /**
         * Get a {@code TextField} instance linked to the given parameter.
         * @param s
         *      The parameter we want to configure with our panel
         * @return
         *      A {@code JTextField} embedded in a {@code JPanel}.
         */
        public JPanel getTextField(final USParameter<String> s){
                JPanel cont = new JPanel();
                final JTextField jrf = new JTextField(8);
                ActionListener al = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                                s.setValue(((JTextField)e.getSource()).getText());
                        }
                };
                jrf.addActionListener(al);
                FocusListener fl = new FocusListener() {
                        @Override public void focusGained(FocusEvent e) {}
                        @Override public void focusLost(FocusEvent e) {
                                JTextField jtf = (JTextField)e.getSource();
                                String tmp = jtf.getText();
                                s.setValue(tmp);
                                if(!tmp.equals(s.getValue())){
                                        LOGGER.warn(I18N.tr("Could not validate your input."));
                                        jtf.setText(s.getValue());
                                }
                        }
                };
                jrf.addFocusListener(fl);
                FocusListener prev = EventHandler.create(FocusListener.class, preview, "repaint");
                jrf.addFocusListener(prev);
                jrf.setText(s.getValue());
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
        public JPanel getColorField(final USParameter<Color> c){
                JLabel lblFill = new JLabel();
                MouseListener ma = EventHandler.create(MouseListener.class,this,"chooseFillColor","","mouseClicked");
                lblFill.addMouseListener(ma);
                PropertyChangeListener pcl =  new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                                c.setValue((Color) evt.getNewValue());
                        }
                };
                PropertyChangeListener pcl2 = EventHandler.create(PropertyChangeListener.class, preview, "repaint");
                lblFill.addPropertyChangeListener("background", pcl);
                lblFill.addPropertyChangeListener("background", pcl2);
                lblFill.setBackground(c.getValue());
                lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
                lblFill.setPreferredSize(new Dimension(40, 20));
                lblFill.setMaximumSize(new Dimension(40, 20));
                lblFill.setOpaque(true);
                JPanel jp = new JPanel();
                jp.add(lblFill);
                return jp;
        }

	public void chooseFillColor(MouseEvent e) {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
                        Component source = (Component)e.getSource();
                        source.setBackground(color);
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
