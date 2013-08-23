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

import javax.swing.*;
import java.awt.*;

/**
 * A JComboBox whose popup is wide enough. Code taken from Santhosh Kumar's blog
 * (<a href="http://www.jroller.com/santhosh/entry/make_jcombobox_popup_wide_enough">article</a>).
 * He made this workaround after looking at
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4618607">this bug</a>
 */
public class WideComboBox extends JComboBox {

    private boolean layingOut = false;

    /**
     * Constructs an empty {@code WideComboBox}.
     */
    public WideComboBox() {
        super();
        align();
    }

    /**
     * Constructs a {@code WideComboBox} containing the given items.
     *
     * @param items Items to put in the combo box.
     */
    public WideComboBox(final Object items[]) {
        super(items);
        align();
    }

    /**
     * Constructs a {@code WideComboBox} that takes its items from an
     * existing {@code ComboBoxModel}.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the
     *               displayed list of items
     */
    public WideComboBox(ComboBoxModel aModel) {
        super(aModel);
        align();
    }

    private void align() {
        ((JLabel) getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    @Override
    public Dimension getSize() {
        Dimension dim = super.getSize();
        if (!layingOut) {
            dim.width = Math.max(dim.width, getPreferredSize().width);
        }
        return dim;
    }
}
