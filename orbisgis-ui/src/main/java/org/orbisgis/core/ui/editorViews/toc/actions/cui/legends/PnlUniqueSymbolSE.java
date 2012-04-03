/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.preview.JNumericSpinner;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.ColorPicker;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.legend.thematic.constant.USNumericParameter;
import org.orbisgis.legend.thematic.constant.USParameter;
import org.orbisgis.legend.thematic.constant.UniqueSymbol;

/**
 *
 * @author alexis
 */
public abstract class PnlUniqueSymbolSE extends  JPanel implements ILegendPanel, UIPanel {

        protected void initializeLegendFields(){
		CRFlowLayout flowLayout = new CRFlowLayout();
                this.setLayout(flowLayout);
                UniqueSymbol us = (UniqueSymbol) getLegend();
                List<USParameter> params = us.getParameters();
                JPanel texts = buildTexts(params);
                JPanel fields = buildFields(params);
                this.add(texts);
                this.add(fields);
        }

        private JPanel buildTexts(List<USParameter> list){
		JPanel pnlTexts = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		pnlTexts.setLayout(flowLayout);
                for(USParameter a : list){
                        JLabel lab = new JLabel(a.getName());
                        pnlTexts.add(lab);
                        pnlTexts.add(new CarriageReturn());
                }
                return pnlTexts;
        }

        private JPanel buildFields(List<USParameter> list){
		JPanel pnlFields = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
                pnlFields.setLayout(flowLayout);
                for(USParameter a : list){
                        JPanel field = null;
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
                        if(field != null){
                                pnlFields.add(field);
                                pnlFields.add(new CarriageReturn());
                        }
                }
                return pnlFields;

        }

        /**
         * Retrieve a spinner with the wanted listener.
         * @param min
         * @param max
         * @param inc
         * @return
         */
        public JNumericSpinner getSpinner(int min, int max, double inc, final USNumericParameter<Double> a){
                final JNumericSpinner jns = new JNumericSpinner(4, min, max, inc);
		jns.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent evt) {
                                a.setValue(jns.getValue());
			}
		});
                jns.setValue(a.getValue());
                return jns;
        }

        public JPanel getTextField(final USParameter<String> s){
                JPanel cont = new JPanel();
                final JTextField jrf = new JTextField(8);
                ActionListener al = EventHandler.create(ActionListener.class, s, "setValue","source.text");
                jrf.addActionListener(al);
                jrf.setText(s.getValue());
                cont.add(jrf);
                return cont;
        }

        public JPanel getColorField(final USParameter<Color> c){
                JPanel cont = new JPanel();
		cont.setLayout(new CRFlowLayout());
		JLabel lblFill = new JLabel();
                MouseListener ma = EventHandler.create(MouseListener.class,this,"chooseFillColor","","mouseClicked");
                lblFill.addMouseListener(ma);
                PropertyChangeListener pcl = EventHandler.create(PropertyChangeListener.class, c, "setValue", "source.background" );
                lblFill.addPropertyChangeListener("background", pcl);
                lblFill.setBackground(c.getValue());
		lblFill.setBorder(BorderFactory.createLineBorder(Color.black));
		lblFill.setPreferredSize(new Dimension(40, 20));
		lblFill.setOpaque(true);
                cont.add(lblFill);
                return cont;
        }

	public void chooseFillColor(MouseEvent e) {
		ColorPicker picker = new ColorPicker();
		if (UIFactory.showDialog(picker)) {
			Color color = picker.getColor();
                        Component source = (Component)e.getSource();
                        source.setBackground(color);
		}
	}
}
