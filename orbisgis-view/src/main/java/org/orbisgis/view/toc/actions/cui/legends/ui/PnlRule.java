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
package org.orbisgis.view.toc.actions.cui.legends.ui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;

/**
 * Rule UI.
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 */
public final class PnlRule extends JPanel implements ISELegendPanel {
        private static final I18n I18N = I18nFactory.getI18n(PnlRule.class);
        private JTextField txtMinScale;
        private JTextField txtMaxScale;
        private JTextField txtName;
        private Rule rule;
        private LegendContext legendContext;
        private String id;

        private boolean descriptionAlreadyFocused = false;

        /**
         * Create a new {@code PnlRule} with the given {@code LegendContext}.
         *
         * @param lc LegendContext
         */
        public PnlRule(LegendContext lc) {
            this.legendContext = lc;
        }

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

                JPanel panel = new JPanel(new MigLayout("wrap 3", "[align r][95][]"));
                panel.setBorder(BorderFactory.createTitledBorder(
                        I18N.tr("Rule settings")));

                // Title
                panel.add(new JLabel(I18N.tr("Title")));

                txtName = new JTextField(rule.getName());
                txtName.addFocusListener(
                        EventHandler.create(FocusListener.class, this,
                                "setTitle", "source.text", "focusLost"));
                panel.add(txtName, "span 2, growx");

                // Description
                panel.add(new JLabel(I18N.tr("Description")));
                JTextArea txtDescription = new JTextArea("");
                txtDescription.setRows(6);
                txtDescription.setLineWrap(true);
                txtDescription.setWrapStyleWord(true);
                txtDescription.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (!descriptionAlreadyFocused) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "It is not yet possible to save descriptions.\n" +
                                            "See https://github.com/irstv/orbisgis/issues/486.",
                                    "Warning",
                                    JOptionPane.WARNING_MESSAGE);
                            descriptionAlreadyFocused = true;
                        }
                    }
                });
                txtDescription.addFocusListener(
                        EventHandler.create(FocusListener.class, this,
                                "setDescription", "source.text", "focusLost"));
                panel.add(new JScrollPane(txtDescription), "span 2, growx");

                // Min scale
                panel.add(new JLabel(I18N.tr("Min. scale")));
                txtMinScale = new JTextField(getMinscale());
                KeyListener keyAdapter =
                        EventHandler.create(KeyListener.class, this, "applyScales");
                txtMinScale.addKeyListener(keyAdapter);
                panel.add(txtMinScale, "growx");
                JButton btnCurrentScaleToMin = new JButton(I18N.tr("Current"));
                btnCurrentScaleToMin.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtMinScale.setText(Integer.toString(
                                (int) legendContext.getCurrentMapTransform()
                                        .getScaleDenominator()));
                        applyScales();
                    }
                });
                panel.add(btnCurrentScaleToMin);

                // Max scale
                panel.add(new JLabel(I18N.tr("Max. scale")));
                txtMaxScale = new JTextField(getMaxscale());
                txtMaxScale.addKeyListener(keyAdapter);
                panel.add(txtMaxScale, "growx");
                JButton btnCurrentScaleToMax = new JButton(I18N.tr("Current"));
                btnCurrentScaleToMax.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        txtMaxScale.setText(Integer.toString(
                                (int) legendContext.getCurrentMapTransform()
                                        .getScaleDenominator()));
                        applyScales();
                    }
                });
                panel.add(btnCurrentScaleToMax);

                this.add(panel);
                return this;
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
            String old = rule.getName();
            rule.setName(s);
            firePropertyChange(NAME_PROPERTY,old, s);
        }

        /**
         * Change silently the content of the field text that displays the name of the rule.
         * @param s The new text.
         */
        public void setTextFieldContent(String s){
            txtName.setText(s);
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
