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
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import javax.swing.*;

import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.WideComboBox;

public class ComboBoxChoice implements InputType {

    private DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
    protected WideComboBox comp = new WideComboBox(comboModel);

    public ComboBoxChoice(String... choices) {
        this(choices, choices);
    }

    public ComboBoxChoice(String[] ids, String[] texts) {
        setChoices(ids, texts);
        ((JLabel) comp.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    private void setChoices(String[] ids, String[] texts) {
        for (int i = 0; i < texts.length; i++) {
            comboModel.addElement(new ContainerItemProperties(ids[i], texts[i]));
        }
        if (comp.getItemCount() > 0) {
            comp.setSelectedIndex(0);
        }
    }

    @Override
    public Component getComponent() {
        return comp;
    }


    @Override
    public String getValue() {
        if (comp.getSelectedIndex() != -1) {
            return ((ContainerItemProperties) comp.getSelectedItem()).getKey();
        } else {
            return "";
        }
    }

    /**
     * Find the key in combo items
     *
     * @param key Combo Item key
     * @return Index in the combo or -1 if not found
     */
    private int getIndexByKey(String key) {
        for (int id = 0; id < comboModel.getSize(); id++) {
            ContainerItemProperties item =
                    (ContainerItemProperties) comboModel.getElementAt(id);
            if (item.getKey().equals(key)) {
                return id;
            }
        }
        return -1;
    }

    @Override
    public void setValue(String value) {
        int valueIndex = getIndexByKey(value);
        if (valueIndex >= 0) {
            comp.setSelectedIndex(valueIndex);
        } else {
            comp.addItem(new ContainerItemProperties(value, value));
            comp.setSelectedIndex(comp.getItemCount() - 1);
        }
    }

    /**
     * @return Combo Box
     */
    public JComboBox getComboBox() {
        return comp;
    }
}
