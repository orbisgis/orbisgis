/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ColorPicker;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.legend.thematic.constant.USNumericParameter;
import org.orbisgis.legend.thematic.constant.USParameter;
import org.orbisgis.legend.thematic.constant.UniqueSymbol;

/**
 * This class proposes some methods that will be common to all the panels built
 * for unique symbols.
 * @author alexis
 */
public abstract class PnlUniqueSymbolSE extends  JPanel implements ILegendPanel, UIPanel {

        private String id;

        /**
         * Fill this {@code JPanel} with all the needed fields.
         */
        protected void initializeLegendFields(){
                this.removeAll();
                UniqueSymbol us = (UniqueSymbol) getLegend();
                List<USParameter<?>> params = us.getParameters();
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
                this.add(jp);
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
                ActionListener al = EventHandler.create(ActionListener.class, s, "setValue","source.text");
                jrf.addActionListener(al);
                FocusListener fl = EventHandler.create(FocusListener.class, s, "setValue","source.text","focusLost");
                jrf.addFocusListener(fl);
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
                PropertyChangeListener pcl = EventHandler.create(PropertyChangeListener.class, c, "setValue", "source.background" );
                lblFill.addPropertyChangeListener("background", pcl);
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
