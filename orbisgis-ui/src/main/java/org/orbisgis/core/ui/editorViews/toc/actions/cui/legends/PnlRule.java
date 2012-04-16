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
		pnlLabels.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.minScale")));
		pnlLabels.add(new CarriageReturn());
		pnlLabels.add(new JLabel(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.maxScale")));
		this.add(pnlLabels);

		KeyListener keyAdapter = getKeyListener();
		JPanel pnlTexts = new JPanel();
		pnlTexts.setLayout(new CRFlowLayout());
		txtMinScale = new JTextField(10);
		txtMinScale.addKeyListener(keyAdapter);
		txtMaxScale = new JTextField(10);
		txtMaxScale.addKeyListener(keyAdapter);
		pnlTexts.add(txtMinScale);
                final MapTransform mt = legendContext.getCurrentMapTransform();
		btnCurrentScaleToMin = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.currentScale"));
		btnCurrentScaleToMin.addActionListener(new ActionListener() {
                        @Override
			public void actionPerformed(ActionEvent e) {
				txtMinScale.setText(Integer.toString((int) mt.getScaleDenominator()));
			}

		});
		pnlTexts.add(btnCurrentScaleToMin);
		pnlTexts.add(new CarriageReturn());
		pnlTexts.add(txtMaxScale);
		btnCurrentScaleToMax = new JButton(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendsPanel.currentScale"));
		btnCurrentScaleToMax.addActionListener(new ActionListener() {

                        @Override
			public void actionPerformed(ActionEvent e) {
				txtMaxScale.setText(Integer.toString((int) mt.getScaleDenominator()));
			}

		});
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
         * Get a KeyListener that will apply the given scales to the underlying
         * Rule each time a key is released.
         * @return
         */
        private KeyListener getKeyListener(){
                return EventHandler.create(KeyListener.class, this, "applyScales");
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

}
