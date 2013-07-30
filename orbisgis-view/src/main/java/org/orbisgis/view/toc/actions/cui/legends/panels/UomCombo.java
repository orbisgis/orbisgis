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
package org.orbisgis.view.toc.actions.cui.legends.panels;

import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * A JPanel containing a combo that can be used to choose an unit of measure
 * @author Alexis Guéganno
 */
public class UomCombo extends JPanel {
    private static final I18n I18N = I18nFactory.getI18n(UomCombo.class);
    private ContainerItemProperties[] units;
    private JComboBox jcc;

    /**
     * Creates a JPanel containing
     * @param orig The unit of measure that must be displayed in the inner JComboBox after its creation.
     * @param units The known units of measure
     * @param label The label we want to display along the JComboBox.
     */
    public UomCombo(Uom orig, ContainerItemProperties[] units, String label){
        super();
        this.units = units;
        JComboBox jcb = getPointUomCombo(orig);
        this.add(new JLabel(label));
        this.add(jcb);
    }

    /**
     * ComboBox to configure the unit of measure used to draw th stroke.
     * @return A JComboBox the user can use to set the unit of measure of the symbol's dimensions.
     */
    private JComboBox getPointUomCombo(Uom uom){
        String[] values = new String[units.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = I18N.tr(units[i].toString());
        }
        jcc = new JComboBox(values);
        jcc.setSelectedItem(uom.toString().toUpperCase());
        return jcc;
    }

    /**
     * Adds an ActionListener that is directly put on the JComboBox
     * @param al The action listener.
     */
    public void addActionListener(ActionListener al){
        jcc.addActionListener(al);
    }

    /**
     * Gets the inner JComboBox
     * @return  The inner JComboBox.
     */
    public JComboBox getCombo(){
        return jcc;
    }
}
