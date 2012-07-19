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

import java.awt.*;
import java.awt.event.FocusListener;
import java.beans.EventHandler;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Alexis Guéganno
 */
public class PnlStyle extends JPanel implements ISELegendPanel {
        protected final static I18n I18N = I18nFactory.getI18n(PnlStyle.class);
        private Style style;
        private JTextField txtName;
        private String id;

        /**
         * Gets the {@code Style} that has been used to create this panel.
         * @return
         */
        public Style getStyle() {
                return style;
        }

        /**
         * Sets the {@code Style} that has been used to create this panel.
         * @param style
         */
        public void setStyle(Style style) {
                this.style = style;
        }

        @Override
        public Component getComponent() {
                removeAll();
                FlowLayout fl = new FlowLayout();
                JPanel panel = new JPanel();
                fl.setVgap(0);
                this.setLayout(fl);
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                //We display the title
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                panel.add(new JLabel("Title : "), gbc);
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.LINE_START;
                txtName = new JTextField(style.getName(),10);
                txtName.addFocusListener(EventHandler.create(FocusListener.class, this, "setTitle","source.text","focusLost"));
                panel.add(txtName, gbc);
                this.add(panel);
                this.setPreferredSize(new Dimension(200, 100));
                this.setBorder(BorderFactory.createTitledBorder(
                I18N.tr("Style configuration")));
                return this;
        }

        @Override
        public void initialize(LegendContext lc) {
        }

        @Override
        public ISELegendPanel newInstance() {
                return new PnlStyle();
        }

        @Override
        public String getId() {
                return id;
        }

        @Override
        public void setId(String newId) {
                id = newId;
        }

        @Override
        public String validateInput() {
                return null;
        }

        public void setTitle(String title){
                style.setName(title);
        }

}
