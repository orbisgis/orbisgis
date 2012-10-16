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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import javax.swing.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Panel associated to {@code Rule} instances in the legend edition UI.
 * @author Alexis Guéganno
 */
public class PnlRule extends JPanel implements ISELegendPanel {

        private static final I18n I18N = I18nFactory.getI18n(PnlRule.class);
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
         *
         * @param r
         */
        public void setRule(Rule r) {
                rule = r;
        }

        /**
         * Gets the Rule associated to this panel.
         *
         * @return
         */
        public Rule getRule() {
                return rule;
        }

        @Override
        public Component getComponent() {
                removeAll();
                FlowLayout fl = new FlowLayout();
                fl.setVgap(0);
                this.setLayout(fl);
                //We need the map transform to use the buttons
                final MapTransform mt = legendContext.getCurrentMapTransform();
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                //We display the title
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel(I18N.tr("Title : ")), gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                txtName = new JTextField(rule.getName(), 10);
                txtName.addFocusListener(EventHandler.create(FocusListener.class, this, "setTitle", "source.text", "focusLost"));
                panel.add(txtName, gbc);
                //We display the description
                //Label
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel("Description : "), gbc);
                //Text field
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.anchor = GridBagConstraints.LINE_START;
                txtDescription = new JTextArea("");
                txtDescription.setColumns(40);
                txtDescription.setRows(6);
                txtDescription.setLineWrap(true);
                txtDescription.addFocusListener(EventHandler.create(
                        FocusListener.class, this, "setDescription", "source.text", "focusLost"));
                JScrollPane jsp = new JScrollPane(txtDescription);
                jsp.setPreferredSize(txtDescription.getPreferredSize());
                panel.add(jsp, gbc);
                //We display the minScale
                KeyListener keyAdapter = EventHandler.create(KeyListener.class, this, "applyScales");
                //Text
                //We put the text field and the button in a single panel in order to
                JPanel min = new JPanel();
                FlowLayout flowMin = new FlowLayout();
                flowMin.setHgap(5);
                min.setLayout(flowMin);
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel(I18N.tr("Min. scale :")), gbc);
                //Text field
                txtMinScale = new JTextField(10);
                txtMinScale.addKeyListener(keyAdapter);
                txtMinScale.setText(getMinscale());
                min.add(txtMinScale);
                //Button
                btnCurrentScaleToMin = new JButton(I18N.tr("Current scale"));
                btnCurrentScaleToMin.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                txtMinScale.setText(Integer.toString((int) mt.getScaleDenominator()));
                                applyScales();
                        }
                });
                min.add(btnCurrentScaleToMin);
                //We add this dedicated panel to our GridBagLayout.
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 2;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(min, gbc);
                //We display the maxScale
                //Text
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel(I18N.tr("Max. scale :")), gbc);
                //We put the text field and the button in a single panel in order to
                //improve the UI.
                //Text field
                JPanel max = new JPanel();
                FlowLayout flowMax = new FlowLayout();
                flowMax.setHgap(5);
                max.setLayout(flowMax);
                txtMaxScale = new JTextField(10);
                txtMaxScale.addKeyListener(keyAdapter);
                txtMaxScale.setText(getMaxscale());
                max.add(txtMaxScale, gbc);
                //Button
                btnCurrentScaleToMax = new JButton(I18N.tr("Current scale"));
                btnCurrentScaleToMax.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                txtMaxScale.setText(Integer.toString((int) mt.getScaleDenominator()));
                                applyScales();
                        }
                });
                max.add(btnCurrentScaleToMax);
                //We add this dedicated panel to our GridBagLayout.
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 3;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(max, gbc);
                this.add(panel);
                this.setPreferredSize(new Dimension(200, 100));
                this.setBorder(BorderFactory.createTitledBorder(I18N.tr("Scale")));
                return this;
        }

        @Override
        public void initialize(LegendContext lc) {
                legendContext = lc;
                getComponent();
        }

        @Override
        public ISELegendPanel newInstance() {
                return new PnlRule();
        }

        @Override
        public String getId() {
                return id;
        }

        @Override
        public void setId(String id) {
                this.id = id;
        }

        /**
         * Apply to the Rule's name the text contained in the editor used to
         * manage it.
         */
        public void setTitle(String s) {
                rule.setName(s);
        }

        /**
         * Apply to the Rule's description the text contained in the editor used
         * to manage it.
         */
        public void setDescription(String s) {
                rule.setDescription(null);
        }

        /**
         * Apply the scales registered in the text fields of this panel to the
         * underlying {@code Rule}.
         */
        public void applyScales() {
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
                                stringBuilder.append(I18N.tr("Min. scale is not a valid number"));

                        }
                }
                if (maxScale.trim().length() != 0) {
                        try {
                                Integer.parseInt(maxScale);
                        } catch (NumberFormatException e) {
                                stringBuilder.append("\n");
                                stringBuilder.append(I18N.tr("Max. scale is not a valid number"));
                        }
                }
                String res = stringBuilder.toString();
                if (res != null && !res.isEmpty()) {
                        return res;
                } else {
                        return null;
                }
        }

        private String getMinscale() {
                if (rule.getMinScaleDenom() != null && rule.getMinScaleDenom() > Double.NEGATIVE_INFINITY) {
                        Double d = rule.getMinScaleDenom();
                        Integer i = d.intValue();
                        return i.toString();
                } else {
                        return "";
                }
        }

        private String getMaxscale() {
                if (rule.getMaxScaleDenom() != null && rule.getMaxScaleDenom() < Double.POSITIVE_INFINITY) {
                        Double d = rule.getMaxScaleDenom();
                        Integer i = d.intValue();
                        return i.toString();
                } else {
                        return "";
                }
        }
}
