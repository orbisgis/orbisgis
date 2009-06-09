/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.orbisgis.sif.SQLUIPanel;

public class ComboBoxChoice implements InputType {

	private HashMap<String, String> idText = new HashMap<String, String>();
	protected JComboBox comp;

	public ComboBoxChoice(String... choices) {
		this(choices, choices);
	}

	public ComboBoxChoice(String[] ids, String[] texts) {
		setChoices(ids, texts);
	}

	protected void setChoices(String[] options) {
		setChoices(options, options);
	}
	
	protected void setChoices(String[] ids, String[] texts) {
		for (int i = 0; i < texts.length; i++) {
			idText.put(ids[i], texts[i]);
		}
		comp = new JComboBox(ids);
		comp.setRenderer(new BasicComboBoxRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel ret = (JLabel) super.getListCellRendererComponent(list, value,
						index, isSelected, cellHasFocus);
				
				ret.setText(idText.get(value));
				
				return ret;				
			}
		});
		if (ids.length > 0) {
			comp.setSelectedIndex(0);
		}
	}

	public Component getComponent() {
		return comp;
	}

	public int getType() {
		return SQLUIPanel.STRING;
	}

	public String getValue() {
		return (String) comp.getSelectedItem();
	}

	public void setValue(String value) {
		comp.setSelectedItem(value);
	}

	public boolean isPersistent() {
		return true;
	}

}
