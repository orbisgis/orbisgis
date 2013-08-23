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
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusListener;
import java.beans.EventHandler;

/**
 * Style UI.
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 */
public final class PnlStyle extends JPanel implements ISELegendPanel {
        private static final I18n I18N = I18nFactory.getI18n(PnlStyle.class);
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
                JPanel panel = new JPanel(new MigLayout("wrap 2", "[align r][170]"));
                panel.setBorder(BorderFactory.createTitledBorder(I18N.tr("Style settings")));

                panel.add(new JLabel(I18N.tr("Title")));
                txtName = new JTextField(style.getName());
                txtName.addFocusListener(
                        EventHandler.create(FocusListener.class, this,
                                "setTitle","source.text","focusLost"));
                panel.add(txtName, "growx");

                this.add(panel);
                return this;
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

        /**
         * Sets the name of the inner style
         * @param title The new name.
         */
        public void setTitle(String title){
            String old = style.getName();
            style.setName(title);
            firePropertyChange(NAME_PROPERTY,old, title);
        }

        /**
         * Change silently the content of the field text that displays the name of the rule.
         * @param s The new text.
         */
        public void setTextFieldContent(String s){
            txtName.setText(s);
        }

}
