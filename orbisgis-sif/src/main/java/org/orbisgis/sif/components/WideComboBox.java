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
package org.orbisgis.sif.components;

import javax.swing.*;
import java.awt.*;

/**
 * A JComboBox whose popup is wide enough. Code taken from Santhosh Kumar's blog
 * (<a href="http://www.jroller.com/santhosh/entry/make_jcombobox_popup_wide_enough">article</a>).
 * He made this workaround after looking at
 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4618607">this bug</a>
 */
public class WideComboBox<E> extends JComboBox<E> {

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
    public WideComboBox(final E items[]) {
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
    public WideComboBox(ComboBoxModel<E> aModel) {
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

    @Override
    public E getSelectedItem() {
        return (E) super.getSelectedItem();
    }
}
