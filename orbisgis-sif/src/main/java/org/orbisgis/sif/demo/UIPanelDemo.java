/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.sif.demo;

import java.awt.Component;
import java.net.URL;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;

/**
 *
 * @author Erwan Bocher
 */
public class UIPanelDemo {

        public static void main(String[] args) {
                SimpleDemoUIPanel sdp = new SimpleDemoUIPanel();                
                if (UIFactory.showDialog(sdp)){
                        System.out.println("The panel has been opened.");
                }
        }

        private static class SimpleDemoUIPanel implements UIPanel {

                private final JPanel panel;
                private final JTextField textField;

                public SimpleDemoUIPanel() {
                        panel = new JPanel();
                        textField = new JTextField("Du texte",12); 
                        panel.add(textField);
                }

                @Override
                public URL getIconURL() {
                        return null;
                }

                @Override
                public String getTitle() {
                        return "A demonstration.";
                }

                @Override
                public String validateInput() {
                        if(textField.getText().isEmpty()){
                                return "Big an error !";
                        }
                        else if(textField.getText().length()< 5){
                                return "You must write more than 5 caracters !";
                        }
                        return null;
                }

                @Override
                public Component getComponent() {
                        return panel;
                }
                
        }
}
