/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui.legends;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import javax.swing.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendContext;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.IRulePanel;
import org.orbisgis.utils.I18N;

/**
 * Panel associated to {@code Rule} instances in the legend edition UI.
 * @author alexis
 */
public class PnlRule extends JPanel implements IRulePanel {

	private JButton btnCurrentScaleToMin;
	private JButton btnCurrentScaleToMax;
	private JTextField txtMinScale;
	private JTextField txtMaxScale;
        private JTextField txtName;
        private JTextArea txtDescription;
        private Rule rule;
        private LegendContext legendContext;
        private String id;

        /**
         * Sets the Rule associated to this panel.
         * @param r
         */
        public void setRule(Rule r){
                rule = r;
        }

        /**
         * Gets the Rule associated to this panel.
         * @return
         */
        public Rule getRule(){
                return rule;
        }

        @Override
        public Component getComponent() {
                removeAll();
		FlowLayout fl = new FlowLayout();
		fl.setVgap(0);
		this.setLayout(fl);
		JPanel pnlLabels = new JPanel();
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setVgap(14);
		pnlLabels.setLayout(flowLayout);
                //The labels used to define the text fields.
                pnlLabels.add(new JLabel("Title : "));
		pnlLabels.add(new CarriageReturn());
                pnlLabels.add(new JLabel("Description : "));
		pnlLabels.add(new CarriageReturn());
		pnlLabels.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.minScale")));
		pnlLabels.add(new CarriageReturn());
		pnlLabels.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.maxScale")));
		this.add(pnlLabels);
                //We need the fields now...
		JPanel pnlTexts = new JPanel();
		pnlTexts.setLayout(new CRFlowLayout());
                //Title management
                txtName = new JTextField(rule.getName(),10);
                txtName.addFocusListener(EventHandler.create(FocusListener.class, this, "setTitle","source.text","focusLost"));
                //Description management
                txtDescription = new JTextArea(rule.getDescription());
                txtDescription.setColumns(40);
                txtDescription.setRows(6);
                txtDescription.setLineWrap(true);
                txtDescription.addFocusListener(EventHandler.create(
                        FocusListener.class, this, "setDescription","source.text","focusLost"));
                //Scale management.
		KeyListener keyAdapter = EventHandler.create(KeyListener.class, this, "applyScales");
                //Min
		txtMinScale = new JTextField(10);
		txtMinScale.addKeyListener(keyAdapter);
                txtMinScale.setText(getMinscale());
                //Max
		txtMaxScale = new JTextField(10);
		txtMaxScale.addKeyListener(keyAdapter);
                txtMaxScale.setText(getMaxscale());
                //We need the map transform to use the buttons
                final MapTransform mt = legendContext.getCurrentMapTransform();
                //Button for min
		btnCurrentScaleToMin = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.currentScale"));
		btnCurrentScaleToMin.addActionListener(new ActionListener() {
                        @Override
			public void actionPerformed(ActionEvent e) {
				txtMinScale.setText(Integer.toString((int) mt.getScaleDenominator()));
			}

		});
                //Button for max
		btnCurrentScaleToMax = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.currentScale"));
		btnCurrentScaleToMax.addActionListener(new ActionListener() {

                        @Override
			public void actionPerformed(ActionEvent e) {
				txtMaxScale.setText(Integer.toString((int) mt.getScaleDenominator()));
			}

		});
                //We add all our text fields.
                pnlTexts.add(txtName);
		pnlTexts.add(new CarriageReturn());
                pnlTexts.add(txtDescription);
		pnlTexts.add(new CarriageReturn());
		pnlTexts.add(txtMinScale);
		pnlTexts.add(btnCurrentScaleToMin);
		pnlTexts.add(new CarriageReturn());
		pnlTexts.add(txtMaxScale);
		pnlTexts.add(btnCurrentScaleToMax);
		this.add(pnlTexts);

		this.setPreferredSize(new Dimension(200, 100));
		this.setBorder(BorderFactory.createTitledBorder(
			I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.scale")));
		return this;
        }

        @Override
        public void initialize(LegendContext lc) {
                legendContext=lc;
                getComponent();
        }

        @Override
        public IRulePanel newInstance() {
                return new PnlRule();
        }

        @Override
        public String getId(){
                return id;
        }

        @Override
        public void setId(String id){
                this.id = id;
        }

        /**
         * Apply to the Rule's name the text contained in the editor used to
         * manage it.
         */
        public void setTitle(String s){
                rule.setName(s);
        }

        /**
         * Apply to the Rule's description the text contained in the editor used
         * to manage it.
         */
        public void setDescription(String s){
                rule.setDescription(s);
        }

        /**
         * Apply the scales registered in the text fields of this panel to
         * the underlying {@code Rule}.
         */
        public void applyScales(){
                String minScale = txtMinScale.getText();
                if (minScale.trim().length() != 0) {
                        try {
                                Double min = Double.parseDouble(minScale);
                                rule.setMinScaleDenom(min);
                        } catch (NumberFormatException e1) {
                        }
                } else {
                        rule.setMinScaleDenom(Double.NEGATIVE_INFINITY);
                }
                String maxScale = txtMaxScale.getText();
                if (maxScale.trim().length() != 0) {
                        try {
                                Double max = Double.parseDouble(maxScale);
                                rule.setMaxScaleDenom(max);
                        } catch (NumberFormatException e1) {
                        }
                } else {
                        rule.setMaxScaleDenom(Double.POSITIVE_INFINITY);
                }
        }

        @Override
        public String validateInput() {
		String minScale = txtMinScale.getText();
		String maxScale = txtMaxScale.getText();
                StringBuilder stringBuilder = new StringBuilder();

		if (minScale.trim().length() != 0) {
			try {
				Integer.parseInt(minScale);
			} catch (NumberFormatException e) {
                                stringBuilder.append(
                                        I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.minScaleIsNotAValidNumber"));

			}
		}
		if (maxScale.trim().length() != 0) {
			try {
				Integer.parseInt(maxScale);
			} catch (NumberFormatException e) {
                                stringBuilder.append("\n");
                                stringBuilder.append(
                                        I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.maxScaleIsNotAValidNumber"));
			}
		}
                String res = stringBuilder.toString();
                if(res != null && !res.isEmpty()){
                        return res;
                } else {
                        return null;
                }
        }

        private String getMinscale() {
                if(rule.getMinScaleDenom() != null && rule.getMinScaleDenom()>Double.NEGATIVE_INFINITY){
                        Double d = rule.getMinScaleDenom();
                        Integer i = d.intValue();
                        return i.toString();
                } else {
                        return "";
                }
        }

        private String getMaxscale() {
                if(rule.getMaxScaleDenom() != null && rule.getMaxScaleDenom()<Double.POSITIVE_INFINITY){
                        Double d = rule.getMaxScaleDenom();
                        Integer i = d.intValue();
                        return i.toString();
                } else {
                        return "";
                }
        }

}
