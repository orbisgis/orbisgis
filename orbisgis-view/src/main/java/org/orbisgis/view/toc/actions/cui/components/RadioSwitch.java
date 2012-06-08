/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.view.toc.actions.cui.components;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author maxence
 */
public abstract class RadioSwitch extends JPanel implements ActionListener {

	public RadioSwitch(String[] options, int initialChoice) {

		super(new FlowLayout());

		JRadioButton[] rbs = new JRadioButton[options.length];
		int i = 0;
		ButtonGroup group = new ButtonGroup();
		for (String opt : options) {
			rbs[i] = new JRadioButton(opt);
			rbs[i].setActionCommand(Integer.toString(i));
			if (i == initialChoice) {
				rbs[i].setSelected(true);
			}
			group.add(rbs[i]);
			rbs[i].addActionListener(this);
			this.add(rbs[i]);
			i++;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int choice = -1;
		try{
		  choice = Integer.parseInt(e.getActionCommand());
		}
		catch (Exception ex){
		}
		valueChanged(choice);
	}

	/**
	 * This method will be called each time the selection change
	 * When errors occur or nothing (should never happen...), -1 is sent,
	 * otherwise, the id of the selection is sent
	 * @param choice
	 */
	protected abstract void valueChanged(int choice);
}
