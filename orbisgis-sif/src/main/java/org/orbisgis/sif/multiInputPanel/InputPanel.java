/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE 
 * team located in University of South Brittany, Vannes.
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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

import net.miginfocom.swing.MigLayout;

import java.awt.*;
import java.util.List;
import javax.swing.*;

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
                 super(new MigLayout("wrap 2", "[] [grow] ", "[grow][grow]"));
                for (Input input : inputs) {

                        // If we have an init value, we set it.
                        String initialValue = input.getInitialValue();
                        if (initialValue != null) {
                                input.getType().setValue(initialValue);
                        }

                        // We retrieve the descriptive text
                        add(new JLabel(input.getText()));
                        // We retrieve the component where the user will set his inputs.
                        Component comp = input.getType().getComponent();
                        if (comp != null) {
                            add(comp, "grow");
                        } else {
                            add(Box.createGlue());
                        }
                }
        }
}
