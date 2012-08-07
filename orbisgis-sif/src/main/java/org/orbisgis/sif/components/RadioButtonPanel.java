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
package org.orbisgis.sif.components;

import java.awt.Component;
import java.awt.FlowLayout;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

/**
 * An {@code UIPanel} that will let the user choose one option in a list of
 * {@code String} instances. Configured with a list of {@code String} and a
 * title.
 * @author Alexis Guéganno
 */
public class RadioButtonPanel implements UIPanel{

        private List<String> choices;
        private String title;
        private JPanel pane;
        private ButtonGroup bg;

        /**
         * Instanciates the panel, using the given list of {@code String} and
         * the given title.
         * @param choices
         * @param title
         */
        public RadioButtonPanel(List<String> choices, String title){
                this.choices = choices;
        }

        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public String validateInput() {
                for(String s : choices){
                        if(s == null){
                                return UIFactory.getI18n().tr("Must not contain null Strings");
                        }
                }
                return null;
        }

        @Override
        public Component getComponent() {
                pane = new JPanel();
                pane.setLayout(new FlowLayout());
                bg = new ButtonGroup();
                for(String s: choices){
                        JRadioButton jrb = new JRadioButton(s);
                        bg.add(jrb);
                        pane.add(jrb);
                }
                return pane;
        }

        /**
         * Get the text that has been selected by the user.
         * @return
         */
        public String getSelectedText(){
                Enumeration<AbstractButton> en = bg.getElements();
                while(en.hasMoreElements()){
                        JRadioButton jrb = (JRadioButton)en.nextElement();
                        if(jrb.isSelected()){
                                return jrb.getText();
                        }
                }
                return null;
        }

        @Override
        public URL getIconURL() {
                return UIFactory.getDefaultIcon();
        }
}
