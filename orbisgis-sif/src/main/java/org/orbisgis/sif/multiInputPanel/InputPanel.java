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
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * {@code InputPanel} is intended to build a {@code JPanel} that will present
 * many {@link Input} to the user in the same place. It is built from a set of
 * {@code Input}, and presents the editable values to the user associated to
 * their description.
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class InputPanel extends JPanel {

        /**
         * Builds a new {@code InputPanel} from the {@code List} of {@code Input}.
         * @param inputs
         */
        public InputPanel(List<Input> inputs) {
                JPanel margin = new JPanel();
                GridLayout gl = new GridLayout(0, 2);
                margin.setLayout(gl);
                FlowLayout lfl = new FlowLayout();
                lfl.setAlignment(FlowLayout.LEFT);
                FlowLayout rfl = new FlowLayout();
                rfl.setAlignment(FlowLayout.RIGHT);
                String currentGroup = null;
                JPanel currentPanel = null;
                for (Input input : inputs) {
                        JPanel labelPanel = new JPanel();
                        labelPanel.setLayout(rfl);
                        //We retrieve the descriptive text
                        JLabel label = new JLabel(input.getText());
                        labelPanel.add(label);
                        //We retrieve the component where the user will set his inputs.
                        JPanel compPanel = new JPanel();
                        compPanel.setLayout(lfl);
                        Component comp = input.getType().getComponent();
                        if (comp != null) {
                                compPanel.add(comp);
                        }
                        //If we have an init value, we set it.
                        String initialValue = input.getInitialValue();
                        if (initialValue != null) {
                                input.getType().setValue(initialValue);
                        }

                        String group = input.getGroup();
                        if ((group == null) || !group.equals(currentGroup)) {
                                //Here we have a basic mechanism to group inputs :
                                //If the group value changes, we create a new Panel
                                //with a dedicated border. We can't come back to
                                //an existing group, apparently...
                                gl.setColumns(1);
                                if (currentPanel != null) {
                                        margin.add(currentPanel);
                                }
                                currentGroup = group;
                                currentPanel = new JPanel();
                                currentPanel.setLayout(new GridLayout(0, 2));
                                if (group != null) {
                                        currentPanel.setBorder(BorderFactory.createTitledBorder(currentGroup));
                                }
                        }
                        currentPanel.add(labelPanel);
                        currentPanel.add(compPanel);
                }
                if (currentPanel != null) {
                        margin.add(currentPanel);
                }
                this.add(margin);
        }
}
