/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ColorModel;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.grap.lut.LutDisplay;
import org.grap.lut.LutGenerator;
import org.sif.CRFlowLayout;
import org.sif.CarriageReturn;

public class RasterDefaultStylePanel extends JPanel {
	private JComboBox jComboBox;
	private JLabel jLabel;

	public RasterDefaultStylePanel() {
		ColorModel cm = LutGenerator.colorModel(LutGenerator.getDefaultLUTS()[0]);
		LutDisplay lutDisplay = new LutDisplay(cm);
		
		jLabel = new JLabel();
		jLabel.setIcon(new ImageIcon(lutDisplay.getImagePlus().getImage()));
		
		jComboBox = new JComboBox(LutGenerator.getDefaultLUTS());
		jComboBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        String lutName = (String)jComboBox.getSelectedItem();
		        ColorModel cm = LutGenerator.colorModel(lutName);
				LutDisplay lutDisplay = new LutDisplay(cm);
				jLabel.setIcon(new ImageIcon(lutDisplay.getImagePlus().getImage()));
		    }
		});
		
		CRFlowLayout flowLayout = new CRFlowLayout();
		flowLayout.setAlignment(CRFlowLayout.CENTER);
		this.setLayout(flowLayout);
		add(jComboBox);
		add(new CarriageReturn());
		add(jLabel);
	}

	public String getJComboBoxSelection() {
		return (String) jComboBox.getSelectedItem();
	}

	public String getJTextFieldEntry() {
		return jLabel.getText();
	}

	public void setLut(String fieldValue) {
		jComboBox.setSelectedItem(fieldValue);
		
	}
	
	
}
