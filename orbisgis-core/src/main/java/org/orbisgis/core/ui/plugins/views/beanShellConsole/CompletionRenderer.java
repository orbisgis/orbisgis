/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *  
 *  Lead Erwan BOCHER, scientific researcher, 
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer. 
 *  
 *  User support lead : Gwendall Petit, geomatic engineer. 
 * 
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
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
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 * 
 * or contact directly: 
 * erwan.bocher _at_ ec-nantes.fr 
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.beanShellConsole;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.javaManager.autocompletion.ClassOption;
import org.orbisgis.core.javaManager.autocompletion.ConstructorOption;
import org.orbisgis.core.javaManager.autocompletion.FieldOption;
import org.orbisgis.core.javaManager.autocompletion.InlineImplementationOption;
import org.orbisgis.core.javaManager.autocompletion.MethodOption;
import org.orbisgis.core.javaManager.autocompletion.VariableOption;

public class CompletionRenderer extends JPanel implements ListCellRenderer {

	private JLabel lbl;

	public CompletionRenderer() {
		lbl = new JLabel();
		lbl.setOpaque(true);
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
		flowLayout.setHgap(0);
		flowLayout.setVgap(1);
		this.setLayout(flowLayout);
		this.add(lbl);
		this.setBackground(Color.white);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		if (value instanceof VariableOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_LOCAL));
		} else if (value instanceof FieldOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_MEMBER));
		} else if (value instanceof ConstructorOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_CLASS));
		} else if (value instanceof MethodOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_MEMBER));
		} else if (value instanceof ClassOption) {
			ClassOption opt = (ClassOption) value;
			if (opt.isInterface()) {
				lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_INTER));
			} else {
				lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_CLASS));
			}
		} else if (value instanceof InlineImplementationOption) {
			lbl.setIcon(IconLoader.getIcon(IconNames.COMPL_INTER));
		} else {
			lbl.setIcon(null);
		}
		lbl.setText(value.toString());

		if (isSelected) {
			lbl.setBackground(Color.lightGray);
			lbl.setForeground(Color.white);
		} else {
			lbl.setBackground(Color.white);
			lbl.setForeground(Color.black);
		}
		this.doLayout();
		return this;
	}

}
