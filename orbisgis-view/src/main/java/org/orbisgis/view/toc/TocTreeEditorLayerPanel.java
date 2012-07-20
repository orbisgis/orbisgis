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
package org.orbisgis.view.toc;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.EventHandler;
import java.io.IOException;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.sif.CRFlowLayout;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Panel editor of the tree layer node.
 */
public class TocTreeEditorLayerPanel extends JPanel implements TocTreeEditorPanel {
        private static final long serialVersionUID = 1L;
        protected final static I18n I18N = I18nFactory.getI18n(Toc.class);
        private final static Logger LOGGER = Logger.getLogger("gui." + TocTreeEditorLayerPanel.class);
        private JCheckBox check;
        private JLabel iconAndLabel;
        private JTextField textField;
        private ILayer layer;
        
        /**
         * @return The edited layer label
         */
        @Override
        public String getLabel() {
                return textField.getText();
        }

        public TocTreeEditorLayerPanel(final JTree tree,ILayer node) {
                layer = node;
                setOpaque(false);
                FlowLayout fl = new FlowLayout(CRFlowLayout.LEADING);
                fl.setHgap(0);
                setLayout(fl);
                check = new JCheckBox();
                check.setOpaque(false);
                iconAndLabel = new JLabel();
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
                add(iconAndLabel);
                add(textField);
                setNodeCosmetic(node);
                //Add listeners
                check.addActionListener(
                        EventHandler.create(ActionListener.class,
                        node,
                        "setVisible",
                        "source.selected"));
        }

        /**
         * 
         * @return The layer edited
         */
        @Override
        public Object getValue() {
                return layer;
        }

        private void setNodeCosmetic(ILayer node) {
                check.setVisible(true);
                check.setSelected(node.isVisible());

                Icon icon = null;
                try {
                        icon = TocAbstractRenderer.getLayerIcon(node);
                } catch (DriverException e) {
                        LOGGER.error(e);
                } catch (IOException e) {
                        LOGGER.error(e);
                }
                if (icon != null) {
                        iconAndLabel.setIcon(icon);
                }
                iconAndLabel.setVisible(true);

                textField.setText(node.getName());
                textField.selectAll();
        }

}        