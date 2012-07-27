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
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import java.util.HashMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class ComboBoxChoice implements InputType {

	private HashMap<String, String> idText = new HashMap<String, String>();
	private JComboBox comp;

	public ComboBoxChoice(String... choices) {
		this(choices, choices);
	}

	public ComboBoxChoice(String[] ids, String[] texts) {
		setChoices(ids, texts);
	}

	private void setChoices(String[] ids, String[] texts) {
		for (int i = 0; i < texts.length; i++) {
			idText.put(ids[i], texts[i]);
		}
		comp = new JComboBox(ids);
		comp.setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel ret = (JLabel) super.getListCellRendererComponent(list,
						value, index, isSelected, cellHasFocus);
				ret.setText(idText.get(value));

				return ret;
			}
		});
		if (ids.length > 0) {
			comp.setSelectedIndex(0);
		}
	}

        @Override
	public Component getComponent() {
		return comp;
	}
        

        @Override
	public String getValue() {
		return (String) comp.getSelectedItem();
	}

        @Override
	public void setValue(String value) {
		comp.setSelectedItem(value);
	}

}