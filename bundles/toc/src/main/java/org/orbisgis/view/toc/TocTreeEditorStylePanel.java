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
package org.orbisgis.view.toc;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import org.apache.log4j.Logger;
import org.orbisgis.coremap.renderer.se.Style;
import org.orbisgis.sif.CRFlowLayout;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Panel editor of the tree node style.
 */
public class TocTreeEditorStylePanel extends JPanel implements TocTreeEditorPanel {
        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(Toc.class);
        private static final Logger LOGGER = Logger.getLogger("gui." + TocTreeEditorStylePanel.class);
        private JCheckBox check;
        private JTextField textField;
        private Style style;
        /**
         * 
         * @return The edited style label
         */
        @Override
        public String getLabel() {
                return textField.getText();
        }

        public TocTreeEditorStylePanel(final JTree tree,Style node) {
                style = node;
                setOpaque(false);
                FlowLayout fl = new FlowLayout(CRFlowLayout.LEADING);
                fl.setHgap(0);
                setLayout(fl);
                check = new JCheckBox();
                check.setOpaque(false);
                textField = new JTextField(14);
                textField.addKeyListener(new KeyAdapter() {

                        @Override
                        public void keyTyped(KeyEvent e) {
                                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                                        tree.stopEditing();
                                }
                        }

                });
                add(check);
                add(textField);
                setNodeCosmetic(style);
                //Add listeners
                check.addActionListener(
                        EventHandler.create(ActionListener.class,
                        style,
                        "setVisible",
                        "source.selected"));
        }

        /**
         * 
         * @return The style edited
         */
        @Override
        public Object getValue() {
                return style;
        }

        private void setNodeCosmetic(Style style) {
                check.setVisible(true);
                check.setSelected(style.isVisible());
                textField.setText(style.getName());
                textField.selectAll();
        }

}        