/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.sif.demo;

import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;

/**
 *
 * @author ebocher
 */
public class MIPDemo {

        public static void main(String[] args) {

                MultiInputPanel multiInputPanel = new MultiInputPanel("A MIP demo");

                //Add a text field
                multiInputPanel.addInput("firstTextField", "A first text field", new TextBoxType(10));

                //Add a another text field

                multiInputPanel.addInput("secondTextField", "A second text field", new TextBoxType(20));

                multiInputPanel.addValidation(new MIPValidation() {

                        @Override
                        public String validate(MultiInputPanel mid) {
                                if (mid.getInput("firstTextField").isEmpty()) {
                                        return "The text cannot be null";
                                } else if (mid.getInput("firstTextField").length() > 0) {
                                        return "The text is " + mid.getInput("firstTextField");
                                } else {

                                        return null;
                                }
                        }
                });

                if (UIFactory.showDialog(multiInputPanel)) {
                        System.out.println("Value for the first text field " + multiInputPanel.getInput("firstTextField"));
                }

        }
}
