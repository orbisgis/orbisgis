/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        private static final Logger LOGGER = LoggerFactory.getLogger("gui." + TocTreeEditorStylePanel.class);
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
