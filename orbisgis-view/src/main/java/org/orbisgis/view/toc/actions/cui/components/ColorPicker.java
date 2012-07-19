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
package org.orbisgis.view.toc.actions.cui.components;

import java.awt.Color;
import java.awt.Component;
import java.net.URL;

import org.orbisgis.sif.UIPanel;

/**
 * 
 * @author david
 */
public class ColorPicker extends javax.swing.JPanel implements UIPanel {

	/** Creates new form ColorPicker */
	public ColorPicker() {
		initComponents(null);
	}



	public ColorPicker(Color initialColor) {
		initComponents(initialColor);
	}

	private void initComponents(Color initialColor) {
        if (initialColor == null){
		jColorChooser1 = new javax.swing.JColorChooser();
        } else {
		    jColorChooser1 = new javax.swing.JColorChooser(initialColor);
        }

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(
						jColorChooser1, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JColorChooser jColorChooser1;

	// End of variables declaration//GEN-END:variables

	public Color getColor() {
		return jColorChooser1.getColor();
	}

	public Component getComponent() {
		return this;
	}

	public URL getIconURL() {
		return null;
	}

	public String getInfoText() {
		return "Please, select the color";
	}

	public String getTitle() {
		return "Color Picker";
	}

	public String initialize() {
		return null;
	}

	public String postProcess() {
		return null;
	}

	public String validateInput() {
		return null;
	}

}
